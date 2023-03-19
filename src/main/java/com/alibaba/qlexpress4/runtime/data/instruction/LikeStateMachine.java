package com.alibaba.qlexpress4.runtime.data.instruction;

import com.alibaba.qlexpress4.utils.BasicUtil;

import java.util.function.Function;

/**
 * @Author TaoKan
 * @Date 2023/1/25 上午11:26
 */
public class LikeStateMachine {
    private LikeState start;
    private LikeState current;
    private LikeStateMatchType likeStateMatchType;
    private Function<LikeFunctionParam, Boolean> function;
    private String resultPattern;
    private int size;

    private LikeStateMachine() {
    }

    public static LikeStateMachineBuilder builder() {
        return new LikeStateMachineBuilder();
    }

    private boolean matchEquals(String desc, String resultPattern) {
        return desc.equals(resultPattern);
    }

    private boolean matchContains(String desc, String resultPattern) {
        return desc.contains(resultPattern);
    }

    private boolean matchStartsWith(String desc, String resultPattern) {
        return desc.startsWith(resultPattern);
    }

    private boolean matchEndsWith(String desc, String resultPattern) {
        return desc.endsWith(resultPattern);
    }

    private boolean matchComplex(String dest) {
        final int destLen = dest.length();
        int index = 0;
        LikeStateMatcher likeStateMatcher = new LikeStateMatcher(this.start);
        for (int i = 0; i < size; i++) {
            LikeState likeState = likeStateMatcher.getCurrent();
            String currWords = likeState.getWords();
            if (likeState.isCurrentLikePatternEquals(1)) {
                //right
                boolean result = dest.startsWith(currWords);
                if (result != true) {
                    return false;
                }
                index += currWords.length();
                likeStateMatcher.goNext();
            } else if (likeState.isCurrentLikePatternEquals(0)) {
                //left
                return dest.endsWith(currWords);
            } else {
                if (likeState.next() == null) {
                    //surround
                    return dest.substring(index, destLen).contains(currWords);
                } else if (likeState.isNextLikePatternEquals(0)) {
                    //surround-left
                    String t = dest.substring(index, destLen - likeState.next().getWords().length());
                    boolean result = t.contains(currWords);
                    if (result != true) {
                        return false;
                    }
                    index += currWords.length();
                    likeStateMatcher.goNext();
                } else {
                    int wordIndex = 0;
                    int length = currWords.length();
                    for (int j = index; j < destLen; j++) {
                        if (dest.charAt(j) == currWords.charAt(wordIndex)) {
                            wordIndex++;
                        }
                        if(wordIndex == length){
                           likeStateMatcher.goNext();
                           break;
                        }
                        index++;
                    }
                    if(index >= destLen){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean match(String dest) {
        if (LikeStateMatchType.COMPLEX.equals(this.likeStateMatchType)) {
            return matchComplex(dest);
        }
        return this.function.apply(new LikeFunctionParam(dest, this.resultPattern));
    }

    private LikeStateMachine compile(String pattern) {
        LikeStateMachine likeStateMachine = new LikeStateMachine();
        StringBuilder stringBuilder = new StringBuilder();

        final int patternLen = pattern.length();
        boolean endOfPercentSign = false, isRight = false, noSign = true;
        int index = 0, percentCount = 0;
        for (int i = 0; i < patternLen; i++) {
            char word = pattern.charAt(i);
            if (i < patternLen - 1 && pattern.charAt(i) != BasicUtil.LIKE_PERCENT_SIGN
                    && pattern.charAt(i + 1) == BasicUtil.LIKE_PERCENT_SIGN) {
                isRight = true;
            }
            if (word == BasicUtil.LIKE_PERCENT_SIGN) {
                if (i == patternLen - 1) {
                    endOfPercentSign = true;
                }
                if (stringBuilder.toString().length() == 0) {
                    index++;
                    percentCount++;
                } else {
                    createLinkNode(likeStateMachine, stringBuilder.toString(),
                            enhanceNormalWordsLikeWordPatternEnum(likeStateMachine, percentCount, index, isRight));
                    index += 2;
                    isRight = false;
                    percentCount++;
                    stringBuilder = new StringBuilder();
                }
                noSign = false;
            } else {
                stringBuilder.append(word);
            }
        }
        if (!endOfPercentSign) {
            index++;
            if (percentCount == 0) {
                createLinkNode(likeStateMachine, stringBuilder.toString(), LikeWordPatternEnum.NONE);
            } else {
                createLinkNode(likeStateMachine, stringBuilder.toString(),
                        enhanceNormalWordsLikeWordPatternEnum(likeStateMachine, percentCount, index, isRight));
            }
        } else {
            if (likeStateMachine.size == 0) {
                createLinkNode(likeStateMachine, stringBuilder.toString(), LikeWordPatternEnum.NONE);
            }
        }
        likeStateMachine.current = likeStateMachine.start;
        if (noSign) {
            likeStateMachine.resultPattern = pattern;
            likeStateMachine.setFunction(x -> matchEquals(x.getDest(), x.getResultPattern()));
            likeStateMachine.likeStateMatchType = LikeStateMatchType.EQUALS;
        }
        if (percentCount == patternLen) {
            likeStateMachine.likeStateMatchType = LikeStateMatchType.ALL_MATCH;
            likeStateMachine.setFunction(x -> true);
        }
        if (likeStateMachine.likeStateMatchType == null) {
            likeStateMachine.likeStateMatchType = LikeStateMatchType.COMPLEX;
        }
        return likeStateMachine;
    }


    private void setMatchTypeOfMachine(Function<LikeFunctionParam, Boolean> function, LikeStateMachine likeStateMachine, LikeStateMatchType likeStateMatchType) {
        if (likeStateMachine.likeStateMatchType == null) {
            likeStateMachine.likeStateMatchType = likeStateMatchType;
            likeStateMachine.setFunction(function);
        } else {
            likeStateMachine.likeStateMatchType = LikeStateMatchType.COMPLEX;
        }
    }

    private LikeWordPatternEnum enhanceNormalWordsLikeWordPatternEnum(LikeStateMachine likeStateMachine, int percentCount, int index, boolean isRight) {
        if (isRight) {
            if (percentCount == 0) {
                setMatchTypeOfMachine(x -> matchStartsWith(x.getDest(), x.getResultPattern()), likeStateMachine, LikeStateMatchType.START_WITH);
                return LikeWordPatternEnum.RIGHT_PERCENT_SIGN;
            }
            setMatchTypeOfMachine(x -> matchContains(x.getDest(), x.getResultPattern()), likeStateMachine, LikeStateMatchType.CONTAINS);
            return LikeWordPatternEnum.SURROUND_PERCENT_SIGN;
        }
        setMatchTypeOfMachine(x -> matchEndsWith(x.getDest(), x.getResultPattern()), likeStateMachine, LikeStateMatchType.END_WITH);
        return LikeWordPatternEnum.LEFT_PERCENT_SIGN;
    }


    private void addLink(LikeState likeState) {
        if (this.size == 0) {
            this.start = likeState;
            this.current = likeState;
        } else {
            LikeState curr = this.current;
            likeState.setPrev(curr);
            curr.setNext(likeState);
            this.current = likeState;
        }
        size++;
    }

    private void createLinkNode(LikeStateMachine likeStateMachine, String resultPattern, LikeWordPatternEnum likeWordPatternEnum) {
        LikeStateWord likeStatementWord = new LikeStateWord(resultPattern, likeWordPatternEnum.getValue());
        LikeState likeState = new LikeState();
        likeState.setInfo(likeStatementWord);
        likeStateMachine.addLink(likeState);
        likeStateMachine.resultPattern = resultPattern;
    }


    public Function<LikeFunctionParam, Boolean> getFunction() {
        return function;
    }

    public void setFunction(Function<LikeFunctionParam, Boolean> function) {
        this.function = function;
    }


    public static class LikeStateMachineBuilder {
        private String pattern;

        public LikeStateMachineBuilder loadPattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public LikeStateMachine build() {
            return new LikeStateMachine().compile(this.pattern);
        }
    }

    private enum LikeStateMatchType {
        START_WITH, END_WITH, CONTAINS, EQUALS, ALL_MATCH, COMPLEX
    }
}
