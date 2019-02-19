package com.deere.isg.worktracker.spring;

import com.deere.isg.worktracker.MetricEngine;
import com.deere.isg.worktracker.MetricEngine.Metric;
import com.deere.isg.worktracker.MetricEngine.MetricCollection;
import com.deere.isg.worktracker.MetricEngine.MetricList;
import com.deere.isg.worktracker.MetricEngine.NumberMetric;
import com.deere.isg.worktracker.MetricEngine.Tag;

import java.util.function.Function;
import java.util.stream.Collectors;

public class MetricJSONBuilder {
    public String marshall(MetricEngine.Bucket bucket) {
        return "{ "+bucket.getMetrics().stream().map(this::toJSON).collect(Collectors.joining(",\n"))+" }";
    }

    private String toJSON(Metric metric) {
        String start = "\""+metric.getKey() + "\": ";

        return start + getValue(metric);
    }

    private String getValue(Metric metric) {
        if(metric instanceof MetricEngine.MetricList) {
            MetricList list = (MetricList) metric;
            return "[{" + (list.stream().map(toJSON()).collect(Collectors.joining("},\n{")) + "}]");
        } else if(metric instanceof MetricCollection) {
            MetricCollection metricSet = (MetricCollection) metric;
            return "{ " + metricSet.getMetrics().stream().map(this::toJSON).collect(Collectors.joining(",\n")) + " }";
        } else if(metric instanceof NumberMetric) {
            NumberMetric countMetric = (NumberMetric)metric;
            return String.valueOf(countMetric.getValue());
        } else if(metric instanceof Tag) {
            return "\""+metric.getValue()+"\"";
        }
        return "";
    }

    private Function<MetricEngine.MetricSet, String> toJSON() {
        return set->set.getMetrics().stream().map(this::toJSON).collect(Collectors.joining(",\n"));
    }
}