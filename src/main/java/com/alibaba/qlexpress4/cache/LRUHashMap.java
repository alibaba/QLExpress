package com.alibaba.qlexpress4.cache;


import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午3:48
 */
public class LRUHashMap<K, V>{
    static final int CPU_NUMBERS = Runtime.getRuntime().availableProcessors();

    static final int NUMBER_OF_READ_BUFFERS = ceilingNextPowerOfTwo(CPU_NUMBERS);

    static final int READ_BUFFERS_MASK = NUMBER_OF_READ_BUFFERS - 1;

    static final int READ_BUFFER_THRESHOLD = 32;

    static final int READ_BUFFER_DRAIN_THRESHOLD = 2 * READ_BUFFER_THRESHOLD;

    static final int READ_BUFFER_SIZE = 2 * READ_BUFFER_DRAIN_THRESHOLD;

    static final int READ_BUFFER_INDEX_MASK = READ_BUFFER_SIZE - 1;

    static final int WRITE_BUFFER_DRAIN_THRESHOLD = 16;

    static int ceilingNextPowerOfTwo(int x) {
        return 1 << (Integer.SIZE - Integer.numberOfLeadingZeros(x - 1));
    }

    final ConcurrentMap<K, Node<K, V>> data;
    final int concurrencyLevel;
    @GuardedBy("evictionLock")
    final long[] readBufferReadCount;
    @GuardedBy("evictionLock")
    final LRUDeque<Node<K, V>> evictionDeque;
    @GuardedBy("evictionLock")
    final AtomicLong calculateSize;
    @GuardedBy("evictionLock")
    final AtomicLong capacity;

    final Lock evictionLock;
    final Queue<Runnable> writeBuffer;
    final AtomicLong[] readBufferWriteCount;
    final AtomicLong[] readBufferDrainAtWriteCount;
    final AtomicReference<Node<K, V>>[][] readBuffers;
    final AtomicReference<LRUStatus> status;


    public LRUHashMap(int size) {
        concurrencyLevel = 16;
        capacity = new AtomicLong(size);
        data = new ConcurrentHashMap<>(16, 0.75f, concurrencyLevel);
        evictionLock = new ReentrantLock();
        calculateSize = new AtomicLong();
        evictionDeque = new LRUDeque<>();
        writeBuffer = new ConcurrentLinkedQueue<>();
        status = new AtomicReference<>(LRUStatus.IDLE);
        readBufferReadCount = new long[NUMBER_OF_READ_BUFFERS];
        readBufferWriteCount = new AtomicLong[NUMBER_OF_READ_BUFFERS];
        readBufferDrainAtWriteCount = new AtomicLong[NUMBER_OF_READ_BUFFERS];
        readBuffers = new AtomicReference[NUMBER_OF_READ_BUFFERS][READ_BUFFER_SIZE];
        for (int i = 0; i < NUMBER_OF_READ_BUFFERS; i++) {
            readBufferWriteCount[i] = new AtomicLong();
            readBufferDrainAtWriteCount[i] = new AtomicLong();
            readBuffers[i] = new AtomicReference[READ_BUFFER_SIZE];
            for (int j = 0; j < READ_BUFFER_SIZE; j++) {
                readBuffers[i][j] = new AtomicReference<>();
            }
        }
    }


    static void checkNotNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }
    @GuardedBy("evictionLock")
    boolean exceedLimit() {
        return calculateSize.get() > capacity.get();
    }

    @GuardedBy("evictionLock")
    void evict() {
        while (exceedLimit()) {
            final Node<K, V> node = evictionDeque.poll();
            if (node == null) {
                return;
            }
            data.remove(node.key, node);
            makeDead(node);
        }
    }

    void afterRead(Node<K, V> node) {
        final int bufferIndex = readBufferIndex();
        final long writeCount = recordRead(bufferIndex, node);
        drainOnReadIfNeeded(bufferIndex, writeCount);
    }

    static int readBufferIndex() {
        return ((int) Thread.currentThread().getId()) & READ_BUFFERS_MASK;
    }


    long recordRead(int bufferIndex, Node<K, V> node) {
        final AtomicLong counter = readBufferWriteCount[bufferIndex];
        final long writeCount = counter.get();
        counter.lazySet(writeCount + 1);

        final int index = (int) (writeCount & READ_BUFFER_INDEX_MASK);
        readBuffers[bufferIndex][index].lazySet(node);

        return writeCount;
    }

    void drainOnReadIfNeeded(int bufferIndex, long writeCount) {
        final long pending = (writeCount - readBufferDrainAtWriteCount[bufferIndex].get());
        final boolean delayable = (pending < READ_BUFFER_THRESHOLD);
        final LRUStatus status = this.status.get();
        if (status.shouldDrainBuffers(delayable)) {
            tryToDrainBuffers();
        }
    }

    void afterWrite(Runnable task) {
        writeBuffer.add(task);
        status.lazySet(LRUStatus.REQUIRED);
        tryToDrainBuffers();
    }

    void tryToDrainBuffers() {
        if (evictionLock.tryLock()) {
            try {
                status.lazySet(LRUStatus.PROCESSING);
                drainBuffers();
            } finally {
                status.compareAndSet(LRUStatus.PROCESSING, LRUStatus.IDLE);
                evictionLock.unlock();
            }
        }
    }

    @GuardedBy("evictionLock")
    void drainBuffers() {
        drainReadBuffers();
        drainWriteBuffer();
    }

    @GuardedBy("evictionLock")
    void drainReadBuffers() {
        final int start = (int) Thread.currentThread().getId();
        final int end = start + NUMBER_OF_READ_BUFFERS;
        for (int i = start; i < end; i++) {
            drainReadBuffer(i & READ_BUFFERS_MASK);
        }
    }

    @GuardedBy("evictionLock")
    void drainReadBuffer(int bufferIndex) {
        final long writeCount = readBufferWriteCount[bufferIndex].get();
        for (int i = 0; i < READ_BUFFER_DRAIN_THRESHOLD; i++) {
            final int index = (int) (readBufferReadCount[bufferIndex] & READ_BUFFER_INDEX_MASK);
            final AtomicReference<Node<K, V>> slot = readBuffers[bufferIndex][index];
            final Node<K, V> node = slot.get();
            if (node == null) {
                break;
            }

            slot.lazySet(null);
            applyRead(node);
            readBufferReadCount[bufferIndex]++;
        }
        readBufferDrainAtWriteCount[bufferIndex].lazySet(writeCount);
    }

    @GuardedBy("evictionLock")
    void applyRead(Node<K, V> node) {
        if (evictionDeque.contains(node)) {
            evictionDeque.moveToBack(node);
        }
    }

    @GuardedBy("evictionLock")
    void drainWriteBuffer() {
        for (int i = 0; i < WRITE_BUFFER_DRAIN_THRESHOLD; i++) {
            final Runnable task = writeBuffer.poll();
            if (task == null) {
                break;
            }
            task.run();
        }
    }


    @GuardedBy("evictionLock")
    void makeDead(Node<K, V> node) {
        for (; ; ) {
            NodeValue<V> current = node.get();
            NodeValue<V> dead = new NodeValue<>(current.value, 0);
            if (node.compareAndSet(current, dead)) {
                calculateSize.lazySet(calculateSize.get() - Math.abs(current.weight));
                return;
            }
        }
    }

    /**
     * Adds the node to the page replacement policy.
     */
    final class AddTask implements Runnable {
        final Node<K, V> node;
        final int weight;

        AddTask(Node<K, V> node, int weight) {
            this.weight = weight;
            this.node = node;
        }

        @Override
        @GuardedBy("evictionLock")
        public void run() {
            calculateSize.lazySet(calculateSize.get() + weight);

            if (node.get().isAlive()) {
                evictionDeque.add(node);
                evict();
            }
        }
    }

    final class UpdateTask implements Runnable {
        final int diff;
        final Node<K, V> node;

        public UpdateTask(Node<K, V> node, int diff) {
            this.diff = diff;
            this.node = node;
        }

        @Override
        @GuardedBy("evictionLock")
        public void run() {
            calculateSize.lazySet(calculateSize.get() + diff);
            applyRead(node);
            evict();
        }
    }


    public V getElement(Object key) {
        final Node<K, V> node = data.get(key);
        if (node == null) {
            return null;
        }
        afterRead(node);
        return node.getValue();
    }

    public V putElement(K key, V value) {
        return put(key, value, false);
    }


    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int size() {
        return data.size();
    }

    V put(K key, V value, boolean onlyIfAbsent) {
        checkNotNull(key);
        checkNotNull(value);

        final int weight = 1;
        final NodeValue<V> newNodeValue = new NodeValue<>(value, weight);
        final Node<K, V> node = new Node<>(key, newNodeValue);

        for (; ; ) {
            final Node<K, V> prior = data.putIfAbsent(node.key, node);
            if (prior == null) {
                afterWrite(new AddTask(node, weight));
                return null;
            } else if (onlyIfAbsent) {
                afterRead(prior);
                return prior.getValue();
            }
            for (; ; ) {
                final NodeValue<V> oldNodeValue = prior.get();
                if (!oldNodeValue.isAlive()) {
                    break;
                }

                if (prior.compareAndSet(oldNodeValue, newNodeValue)) {
                    final int weightedDifference = weight - oldNodeValue.weight;
                    if (weightedDifference == 0) {
                        afterRead(prior);
                    } else {
                        afterWrite(new UpdateTask(prior, weightedDifference));
                    }
                    return oldNodeValue.value;
                }
            }
        }
    }

    enum LRUStatus {
        IDLE {
            @Override
            boolean shouldDrainBuffers(boolean delayable) {
                return !delayable;
            }
        },

        REQUIRED {
            @Override
            boolean shouldDrainBuffers(boolean delayable) {
                return true;
            }
        },

        PROCESSING {
            @Override
            boolean shouldDrainBuffers(boolean delayable) {
                return false;
            }
        };
        abstract boolean shouldDrainBuffers(boolean delayable);
    }

    @Immutable
    static final class NodeValue<V> {
        final int weight;
        final V value;

        NodeValue(V value, int weight) {
            this.weight = weight;
            this.value = value;
        }

        boolean contains(Object o) {
            return (o == value) || value.equals(o);
        }

        boolean isAlive() {
            return weight > 0;
        }

    }

    static final class Node<K, V> extends AtomicReference<NodeValue<V>> implements Linked<Node<K, V>> {
        final K key;
        @GuardedBy("evictionLock")
        Node<K, V> prev;
        @GuardedBy("evictionLock")
        Node<K, V> next;
        NodeValue<V> nodeValue;

        Node(K key, NodeValue<V> nodeValue) {
            super(nodeValue);
            this.key = key;
            this.nodeValue = nodeValue;
        }

        @Override
        @GuardedBy("evictionLock")
        public Node<K, V> getPrevious() {
            return prev;
        }

        @Override
        @GuardedBy("evictionLock")
        public void setPrevious(Node<K, V> prev) {
            this.prev = prev;
        }

        @Override
        @GuardedBy("evictionLock")
        public Node<K, V> getNext() {
            return next;
        }

        @Override
        @GuardedBy("evictionLock")
        public void setNext(Node<K, V> next) {
            this.next = next;
        }

        V getValue() {
            return get().value;
        }

    }
}
