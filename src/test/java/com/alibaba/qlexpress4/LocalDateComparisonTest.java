package com.alibaba.qlexpress4;

import org.junit.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class LocalDateComparisonTest {
    
    @Test
    public void localDateLessThanTest() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> context = new HashMap<>();
        context.put("date1", LocalDate.of(2024, 1, 1));
        context.put("date2", LocalDate.of(2024, 12, 31));
        QLResult result = runner.execute("date1 < date2", context, QLOptions.DEFAULT_OPTIONS);
        assertTrue((Boolean) result.getResult());
    }
    
    @Test
    public void localDateGreaterThanTest() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> context = new HashMap<>();
        context.put("date1", LocalDate.of(2024, 1, 1));
        context.put("date2", LocalDate.of(2024, 12, 31));
        QLResult result = runner.execute("date2 > date1", context, QLOptions.DEFAULT_OPTIONS);
        assertTrue((Boolean) result.getResult());
    }
    
    @Test
    public void localDateEqualTest() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> context = new HashMap<>();
        context.put("date1", LocalDate.of(2024, 1, 1));
        context.put("date2", LocalDate.of(2024, 1, 1));
        QLResult result = runner.execute("date1 == date2", context, QLOptions.DEFAULT_OPTIONS);
        assertTrue((Boolean) result.getResult());
    }
    
    @Test
    public void localDateTimeComparisonTest() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> context = new HashMap<>();
        context.put("dt1", LocalDateTime.of(2024, 1, 1, 0, 0));
        context.put("dt2", LocalDateTime.of(2024, 12, 31, 23, 59));
        QLResult result = runner.execute("dt1 < dt2", context, QLOptions.DEFAULT_OPTIONS);
        assertTrue((Boolean) result.getResult());
    }
    
    @Test
    public void localTimeComparisonTest() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> context = new HashMap<>();
        context.put("t1", LocalTime.of(9, 0));
        context.put("t2", LocalTime.of(17, 0));
        QLResult result = runner.execute("t1 < t2", context, QLOptions.DEFAULT_OPTIONS);
        assertTrue((Boolean) result.getResult());
    }
    
    @Test
    public void localDateLessEqualTest() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> context = new HashMap<>();
        context.put("date1", LocalDate.of(2024, 1, 1));
        context.put("date2", LocalDate.of(2024, 1, 1));
        QLResult result = runner.execute("date1 <= date2", context, QLOptions.DEFAULT_OPTIONS);
        assertTrue((Boolean) result.getResult());
    }
    
    @Test
    public void localDateGreaterEqualTest() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> context = new HashMap<>();
        context.put("date1", LocalDate.of(2024, 12, 31));
        context.put("date2", LocalDate.of(2024, 1, 1));
        QLResult result = runner.execute("date1 >= date2", context, QLOptions.DEFAULT_OPTIONS);
        assertTrue((Boolean) result.getResult());
    }
    
    @Test
    public void localDateNotEqualTest() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> context = new HashMap<>();
        context.put("date1", LocalDate.of(2024, 1, 1));
        context.put("date2", LocalDate.of(2024, 12, 31));
        QLResult result = runner.execute("date1 != date2", context, QLOptions.DEFAULT_OPTIONS);
        assertTrue((Boolean) result.getResult());
    }
}
