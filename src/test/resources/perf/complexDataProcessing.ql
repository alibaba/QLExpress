import java.lang.String;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Double;
import java.lang.Boolean;
import java.lang.Math;
import java.lang.System;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Date;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Complex Data Processing Engine
 * This script demonstrates complex business logic with multiple functions,
 * nested conditions, loops, and extensive data transformations.
 */

// Configuration constants
Integer MAX_RETRY_COUNT = 5;
Integer BATCH_SIZE = 100;
Double THRESHOLD_RATIO = 0.85;
Long TIMEOUT_MILLIS = 30000L;
Boolean ENABLE_CACHE = true;
Boolean STRICT_MODE = false;

/**
 * Calculate weighted score based on multiple factors
 */
function calculateWeightedScore(Map dataPoint, Map weights) {
    Double totalScore = 0.0;
    Double totalWeight = 0.0;

    if (dataPoint == null || weights == null) {
        return 0.0;
    }

    for (String key : weights.keySet()) {
        if (dataPoint.containsKey(key)) {
            Double value = (Double) dataPoint.get(key);
            Double weight = (Double) weights.get(key);

            if (value != null && weight != null && weight > 0) {
                totalScore = totalScore + (value * weight);
                totalWeight = totalWeight + weight;
            }
        }
    }

    if (totalWeight > 0) {
        return totalScore / totalWeight;
    } else {
        return 0.0;
    }
}

/**
 * Validate data quality and completeness
 */
function validateDataQuality(List records, Map schema) {
    Integer validCount = 0;
    Integer invalidCount = 0;
    List errorMessages = new ArrayList();

    for (Integer i = 0; i < records.size(); i++) {
        Map record = (Map) records.get(i);
        Boolean isValid = true;

        // Check required fields
        for (String fieldName : schema.keySet()) {
            Map fieldSpec = (Map) schema.get(fieldName);
            Boolean required = (Boolean) fieldSpec.get("required");

            if (required != null && required) {
                if (!record.containsKey(fieldName) || record.get(fieldName) == null) {
                    isValid = false;
                    errorMessages.add("Record " + i + " missing required field: " + fieldName);
                }
            }

            // Type validation
            if (record.containsKey(fieldName) && record.get(fieldName) != null) {
                String expectedType = (String) fieldSpec.get("type");
                Object value = record.get(fieldName);

                if ("number".equals(expectedType) && !(value instanceof Number)) {
                    isValid = false;
                    errorMessages.add("Record " + i + " field " + fieldName + " has wrong type");
                } else if ("string".equals(expectedType) && !(value instanceof String)) {
                    isValid = false;
                    errorMessages.add("Record " + i + " field " + fieldName + " has wrong type");
                }
            }
        }

        if (isValid) {
            validCount = validCount + 1;
        } else {
            invalidCount = invalidCount + 1;
        }
    }

    Map result = new HashMap();
    result.put("validCount", validCount);
    result.put("invalidCount", invalidCount);
    result.put("totalCount", records.size());
    result.put("qualityRatio", (Double) validCount / records.size());
    result.put("errors", errorMessages);

    return result;
}

/**
 * Transform and normalize data
 */
function transformData(List rawData, Map transformRules) {
    List transformedData = new ArrayList();

    for (Integer idx = 0; idx < rawData.size(); idx++) {
        Map rawRecord = (Map) rawData.get(idx);
        Map transformedRecord = new HashMap();

        for (String sourceField : transformRules.keySet()) {
            Map rule = (Map) transformRules.get(sourceField);
            String targetField = (String) rule.get("target");
            String operation = (String) rule.get("operation");

            if (rawRecord.containsKey(sourceField)) {
                Object rawValue = rawRecord.get(sourceField);
                Object transformedValue = rawValue;

                if ("uppercase".equals(operation) && rawValue instanceof String) {
                    transformedValue = ((String) rawValue).toUpperCase();
                } else if ("lowercase".equals(operation) && rawValue instanceof String) {
                    transformedValue = ((String) rawValue).toLowerCase();
                } else if ("multiply".equals(operation) && rawValue instanceof Number) {
                    Double multiplier = (Double) rule.get("factor");
                    transformedValue = ((Number) rawValue).doubleValue() * multiplier;
                } else if ("round".equals(operation) && rawValue instanceof Number) {
                    transformedValue = Math.round(((Number) rawValue).doubleValue());
                } else if ("abs".equals(operation) && rawValue instanceof Number) {
                    transformedValue = Math.abs(((Number) rawValue).doubleValue());
                }

                transformedRecord.put(targetField, transformedValue);
            }
        }

        // Copy unmapped fields
        for (String field : rawRecord.keySet()) {
            if (!transformedRecord.containsKey(field)) {
                transformedRecord.put(field, rawRecord.get(field));
            }
        }

        transformedData.add(transformedRecord);
    }

    return transformedData;
}

/**
 * Aggregate data with grouping and statistics
 */
function aggregateData(List data, List groupByFields, Map aggregations) {
    Map groups = new HashMap();

    // Group data
    for (Integer i = 0; i < data.size(); i++) {
        Map record = (Map) data.get(i);
        String groupKey = "";

        for (Integer j = 0; j < groupByFields.size(); j++) {
            String field = (String) groupByFields.get(j);
            Object value = record.get(field);
            groupKey = groupKey + field + ":" + value + "|";
        }

        if (!groups.containsKey(groupKey)) {
            groups.put(groupKey, new ArrayList());
        }

        List groupRecords = (List) groups.get(groupKey);
        groupRecords.add(record);
    }

    // Calculate aggregations
    List results = new ArrayList();

    for (String groupKey : groups.keySet()) {
        List groupRecords = (List) groups.get(groupKey);
        Map aggResult = new HashMap();

        // Add group key fields
        String[] keyParts = groupKey.split("\\|");
        for (Integer k = 0; k < keyParts.length - 1; k++) {
            String[] fieldValue = keyParts[k].split(":");
            if (fieldValue.length == 2) {
                aggResult.put(fieldValue[0], fieldValue[1]);
            }
        }

        // Calculate aggregations
        for (String aggField : aggregations.keySet()) {
            Map aggSpec = (Map) aggregations.get(aggField);
            String operation = (String) aggSpec.get("operation");

            if ("count".equals(operation)) {
                aggResult.put(aggField + "_count", groupRecords.size());
            } else if ("sum".equals(operation)) {
                Double sum = 0.0;
                for (Integer m = 0; m < groupRecords.size(); m++) {
                    Map rec = (Map) groupRecords.get(m);
                    Object val = rec.get(aggField);
                    if (val instanceof Number) {
                        sum = sum + ((Number) val).doubleValue();
                    }
                }
                aggResult.put(aggField + "_sum", sum);
            } else if ("avg".equals(operation)) {
                Double sum = 0.0;
                Integer count = 0;
                for (Integer m = 0; m < groupRecords.size(); m++) {
                    Map rec = (Map) groupRecords.get(m);
                    Object val = rec.get(aggField);
                    if (val instanceof Number) {
                        sum = sum + ((Number) val).doubleValue();
                        count = count + 1;
                    }
                }
                aggResult.put(aggField + "_avg", count > 0 ? sum / count : 0.0);
            } else if ("max".equals(operation)) {
                Double max = null;
                for (Integer m = 0; m < groupRecords.size(); m++) {
                    Map rec = (Map) groupRecords.get(m);
                    Object val = rec.get(aggField);
                    if (val instanceof Number) {
                        Double numVal = ((Number) val).doubleValue();
                        if (max == null || numVal > max) {
                            max = numVal;
                        }
                    }
                }
                aggResult.put(aggField + "_max", max);
            } else if ("min".equals(operation)) {
                Double min = null;
                for (Integer m = 0; m < groupRecords.size(); m++) {
                    Map rec = (Map) groupRecords.get(m);
                    Object val = rec.get(aggField);
                    if (val instanceof Number) {
                        Double numVal = ((Number) val).doubleValue();
                        if (min == null || numVal < min) {
                            min = numVal;
                        }
                    }
                }
                aggResult.put(aggField + "_min", min);
            }
        }

        results.add(aggResult);
    }

    return results;
}

/**
 * Filter data based on complex conditions
 */
function filterData(List data, Map filterConditions) {
    List filteredData = new ArrayList();

    for (Integer i = 0; i < data.size(); i++) {
        Map record = (Map) data.get(i);
        Boolean matchesAllConditions = true;

        for (String field : filterConditions.keySet()) {
            Map condition = (Map) filterConditions.get(field);
            String operator = (String) condition.get("operator");
            Object expectedValue = condition.get("value");
            Object actualValue = record.get(field);

            Boolean matches = false;

            if ("equals".equals(operator)) {
                matches = (actualValue != null && actualValue.equals(expectedValue));
            } else if ("notEquals".equals(operator)) {
                matches = (actualValue == null || !actualValue.equals(expectedValue));
            } else if ("greaterThan".equals(operator) && actualValue instanceof Number && expectedValue instanceof Number) {
                matches = ((Number) actualValue).doubleValue() > ((Number) expectedValue).doubleValue();
            } else if ("lessThan".equals(operator) && actualValue instanceof Number && expectedValue instanceof Number) {
                matches = ((Number) actualValue).doubleValue() < ((Number) expectedValue).doubleValue();
            } else if ("greaterOrEqual".equals(operator) && actualValue instanceof Number && expectedValue instanceof Number) {
                matches = ((Number) actualValue).doubleValue() >= ((Number) expectedValue).doubleValue();
            } else if ("lessOrEqual".equals(operator) && actualValue instanceof Number && expectedValue instanceof Number) {
                matches = ((Number) actualValue).doubleValue() <= ((Number) expectedValue).doubleValue();
            } else if ("contains".equals(operator) && actualValue instanceof String && expectedValue instanceof String) {
                matches = ((String) actualValue).contains((String) expectedValue);
            } else if ("startsWith".equals(operator) && actualValue instanceof String && expectedValue instanceof String) {
                matches = ((String) actualValue).startsWith((String) expectedValue);
            } else if ("endsWith".equals(operator) && actualValue instanceof String && expectedValue instanceof String) {
                matches = ((String) actualValue).endsWith((String) expectedValue);
            } else if ("in".equals(operator) && expectedValue instanceof List) {
                List valueList = (List) expectedValue;
                matches = valueList.contains(actualValue);
            }

            if (!matches) {
                matchesAllConditions = false;
                break;
            }
        }

        if (matchesAllConditions) {
            filteredData.add(record);
        }
    }

    return filteredData;
}

/**
 * Join two datasets
 */
function joinDatasets(List leftData, List rightData, String leftKey, String rightKey, String joinType) {
    List joinedData = new ArrayList();

    // Build index for right dataset
    Map rightIndex = new HashMap();
    for (Integer i = 0; i < rightData.size(); i++) {
        Map rightRecord = (Map) rightData.get(i);
        Object keyValue = rightRecord.get(rightKey);

        if (!rightIndex.containsKey(keyValue)) {
            rightIndex.put(keyValue, new ArrayList());
        }

        List rightRecords = (List) rightIndex.get(keyValue);
        rightRecords.add(rightRecord);
    }

    // Perform join
    for (Integer i = 0; i < leftData.size(); i++) {
        Map leftRecord = (Map) leftData.get(i);
        Object keyValue = leftRecord.get(leftKey);

        if (rightIndex.containsKey(keyValue)) {
            List matchingRightRecords = (List) rightIndex.get(keyValue);

            for (Integer j = 0; j < matchingRightRecords.size(); j++) {
                Map rightRecord = (Map) matchingRightRecords.get(j);
                Map joinedRecord = new HashMap();

                // Add left record fields
                for (String field : leftRecord.keySet()) {
                    joinedRecord.put("left_" + field, leftRecord.get(field));
                }

                // Add right record fields
                for (String field : rightRecord.keySet()) {
                    joinedRecord.put("right_" + field, rightRecord.get(field));
                }

                joinedData.add(joinedRecord);
            }
        } else if ("left".equals(joinType) || "outer".equals(joinType)) {
            Map joinedRecord = new HashMap();

            // Add left record fields
            for (String field : leftRecord.keySet()) {
                joinedRecord.put("left_" + field, leftRecord.get(field));
            }

            joinedData.add(joinedRecord);
        }
    }

    return joinedData;
}

/**
 * Sort data by multiple fields
 */
function sortData(List data, List sortFields, List sortOrders) {
    // Simple bubble sort implementation for demonstration
    List sortedData = new ArrayList();
    for (Integer i = 0; i < data.size(); i++) {
        sortedData.add(data.get(i));
    }

    for (Integer i = 0; i < sortedData.size() - 1; i++) {
        for (Integer j = 0; j < sortedData.size() - i - 1; j++) {
            Map record1 = (Map) sortedData.get(j);
            Map record2 = (Map) sortedData.get(j + 1);

            Boolean shouldSwap = false;

            for (Integer k = 0; k < sortFields.size(); k++) {
                String field = (String) sortFields.get(k);
                String order = (String) sortOrders.get(k);

                Object value1 = record1.get(field);
                Object value2 = record2.get(field);

                if (value1 == null && value2 != null) {
                    shouldSwap = "asc".equals(order);
                    break;
                } else if (value1 != null && value2 == null) {
                    shouldSwap = "desc".equals(order);
                    break;
                } else if (value1 != null && value2 != null) {
                    if (value1 instanceof Number && value2 instanceof Number) {
                        Double num1 = ((Number) value1).doubleValue();
                        Double num2 = ((Number) value2).doubleValue();

                        if (num1 < num2) {
                            shouldSwap = "desc".equals(order);
                            break;
                        } else if (num1 > num2) {
                            shouldSwap = "asc".equals(order);
                            break;
                        }
                    } else if (value1 instanceof String && value2 instanceof String) {
                        Integer comparison = ((String) value1).compareTo((String) value2);

                        if (comparison < 0) {
                            shouldSwap = "desc".equals(order);
                            break;
                        } else if (comparison > 0) {
                            shouldSwap = "asc".equals(order);
                            break;
                        }
                    }
                }
            }

            if (shouldSwap) {
                Object temp = sortedData.get(j);
                sortedData.set(j, sortedData.get(j + 1));
                sortedData.set(j + 1, temp);
            }
        }
    }

    return sortedData;
}

// Main processing logic
Map processingConfig = new HashMap();
processingConfig.put("enableValidation", true);
processingConfig.put("enableTransformation", true);
processingConfig.put("enableAggregation", true);
processingConfig.put("enableFiltering", true);

List sampleData = new ArrayList();
Map weights = new HashMap();
weights.put("quality", 0.3);
weights.put("speed", 0.5);
weights.put("cost", 0.2);

// Create sample validation schema
Map schema = new HashMap();
Map field1 = new HashMap();
field1.put("required", true);
field1.put("type", "string");
schema.put("id", field1);

Map field2 = new HashMap();
field2.put("required", true);
field2.put("type", "number");
schema.put("value", field2);

// Process data in batches
Integer totalProcessed = 0;
Integer totalErrors = 0;
Double avgScore = 0.0;

String status = "initialized";
Long startTime = System.currentTimeMillis();

if (processingConfig.get("enableValidation") != null && (Boolean) processingConfig.get("enableValidation")) {
    status = "validating";
}

if (processingConfig.get("enableTransformation") != null && (Boolean) processingConfig.get("enableTransformation")) {
    status = "transforming";
}

Long endTime = System.currentTimeMillis();
Long duration = endTime - startTime;

Map finalResult = new HashMap();
finalResult.put("status", status);
finalResult.put("totalProcessed", totalProcessed);
finalResult.put("totalErrors", totalErrors);
finalResult.put("avgScore", avgScore);
finalResult.put("duration", duration);
finalResult.put("timestamp", endTime);

return finalResult;
