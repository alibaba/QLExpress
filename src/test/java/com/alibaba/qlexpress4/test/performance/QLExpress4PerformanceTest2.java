package com.alibaba.qlexpress4.test.performance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.test.performance.operator.InOperator;
import com.alibaba.qlexpress4.test.performance.operator.IntersectOperator;
import com.alibaba.qlexpress4.test.performance.operator.NotInOperator;
import com.alibaba.qlexpress4.test.performance.operator.NotIntersectOperator;

import org.junit.Test;

/**
 * @author 冰够
 */
public class QLExpress4PerformanceTest2 {
    private static final InitOptions initOptions = InitOptions.DEFAULT_OPTIONS;
    // private static final InitOptions initOptions = InitOptions.builder().debug(true).build();
    private static final QLOptions qlOptions = QLOptions.builder().cache(true).build();
    private static final Express4Runner express4Runner = new Express4Runner(initOptions);

    static {
        express4Runner.replaceDefaultOperator("in", InOperator.getInstance());
        express4Runner.addOperator("not_in", NotInOperator.getInstance());
        express4Runner.addOperator("intersect", IntersectOperator.getInstance());
        express4Runner.addOperator("not_intersect", NotIntersectOperator.getInstance());
    }

    /**
     * i:0, execute time cost:  3754us
     * i:1, execute time cost:   731us
     * i:2, execute time cost:   574us
     * i:3, execute time cost:   593us
     * i:4, execute time cost:   616us
     *
     * @throws IOException
     */
    @Test
    public void test_performance1() throws IOException {
        String script = readScript();

        // 执行一次(空Map）
        express4Runner.execute(script, Collections.emptyMap(), qlOptions);

        // 正式执行
        Map<String, Object> bizContext = buildBizContext();
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            express4Runner.execute(script, bizContext, qlOptions);
            long end = System.nanoTime();
            System.out.printf("i:%s, execute time cost:%6sus%n", i, (end - start) / 1000);
        }
    }

    /**
     * i:0, execute time cost:  1160us
     * i:1, execute time cost:   891us
     * i:2, execute time cost:   620us
     * i:3, execute time cost:   555us
     * i:4, execute time cost:   557us
     *
     * @throws IOException
     */
    @Test
    public void test_performance2() throws IOException {
        String script = readScript();

        // 执行一次（非空Map）
        Map<String, Object> map = new HashMap<>();
        map.put("sellerId", 0L);
        map.put("categoryIds", Collections.singletonList(0L));
        map.put("itemTags", Collections.singletonList(0));
        map.put("spCode", "swarm-up-sp-code");
        express4Runner.execute(script, map, qlOptions);
        // Set<String> outVarNames = express4Runner.getOutVarNames(script);
        // System.out.println("outVarNames = " + outVarNames);

        // 正式执行
        Map<String, Object> bizContext = buildBizContext();
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            express4Runner.execute(script, bizContext, qlOptions);
            long end = System.nanoTime();
            System.out.printf("i:%s, execute time cost:%6sus%n", i, (end - start) / 1000);
        }
    }

    private Map<String, Object> buildBizContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("sellerId", 2025L);
        context.put("categoryIds", Arrays.asList(50011972, 201268793, 50005050));
        context.put("itemTags",
            Arrays.asList(2049, 16897, 22145, 33217, 34113, 36417, 37953, 39553, 40897, 52737, 65281, 67521, 83073, 87361, 87425, 96833, 100609,
                101889, 106881, 107585, 111105, 111169, 115329, 120385, 120577, 123905, 137281, 139393, 141121, 141889, 145857, 162561, 164929,
                165057, 170817, 170881, 173313, 180289, 188481, 189057, 189569, 189889, 191873, 192769, 199553, 200321, 203841, 213889, 219777,
                221825, 223425, 223553, 225601, 235585, 235649, 239041, 239425, 241985, 243329, 245697, 249601, 250369, 252609, 253313, 255489,
                256129, 258625, 260033, 260737, 265409, 266817, 276097, 277185, 285825, 286337, 291585, 297473, 303553, 303617, 303681, 307713,
                308545, 310081, 315713, 317121, 320065, 321473, 323905, 329089, 334785, 340673, 344577, 347905, 350017, 350529, 353793, 353985,
                354369, 354817, 356545, 356609, 360001, 363585, 368833, 376705, 376769, 385153, 388097, 396033, 397057, 397953, 403713, 409985,
                414145, 414337, 416001, 416065, 417473, 417537, 418753, 419073, 459265, 464641, 466049, 479233, 481153, 503681, 505217, 523649,
                527681, 544257, 549825, 552129, 558273, 563905, 567361, 573697, 576193, 591425, 591489, 599041, 602561, 609473, 621185, 622913,
                622977, 623041, 623105, 630721, 630913, 631041, 631105, 631169, 638913, 646657, 648961, 649089, 654273, 676673, 698369, 698561,
                698625, 698817, 698945, 699073, 704833, 709761, 710913, 716673, 724929, 724993, 725057, 725121, 733633, 733697, 737409, 746305,
                755073, 770817, 774081, 774657, 778049, 780609, 780673, 781505, 783233, 784897, 786113));
        return context;
    }

    private String readScript() throws IOException {
        Path path = Paths.get("src/test/resources/testsuite/bingo/script.ql");
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
