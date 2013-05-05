/*
 *    Copyright (C) 2013 Codenvy.
 *
 */
package com.codenvy.analytics.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codenvy.analytics.client.QueryService;
import com.codenvy.analytics.metrics.Metric;
import com.codenvy.analytics.metrics.MetricFactory;
import com.codenvy.analytics.metrics.MetricParameter;
import com.codenvy.analytics.metrics.Utils;
import com.codenvy.analytics.server.vew.layout.LayoutReader;
import com.codenvy.analytics.server.vew.layout.MetricRowLayoutImpl;
import com.codenvy.analytics.server.vew.layout.RowLayout;
import com.codenvy.analytics.server.vew.layout.ViewLayout;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class QueryServiceImpl extends RemoteServiceServlet implements QueryService {

    private static final Logger     LOGGER       = LoggerFactory.getLogger(QueryServiceImpl.class);
    private static final String     VIEW_QUERY   = "view/query.xml";
    private static final String     SERVER_ERROR = "Server Error";

    /**
     * Query view layout.
     */
    private static final ViewLayout viewLayout;

    static {
        LayoutReader layout = new LayoutReader(VIEW_QUERY);
        try {
            viewLayout = layout.read();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    /** {@inheritedDoc} */
    public Map<String, String> getMetricTypes() {
        Map<String, String> types = new LinkedHashMap<String, String>();

        for (RowLayout row : viewLayout.getLayout().get(0)) {
            MetricRowLayoutImpl metricRow = (MetricRowLayoutImpl)row;
            Metric metric = metricRow.getMetric();

            types.put(metricRow.getTitle(), metric.getType().name());
        }

        return types;
    }

    /** {@inheritedDoc} */
    public Map<String, String> getMetricParameters(String metricTitle) throws IOException {
        Map<String, String> parameters = new HashMap<String, String>();

        Metric metric = MetricFactory.createMetric(metricTitle);

        for (MetricParameter sp : metric.getParams()) {
            parameters.put(sp.getName(), sp.getDefaultValue());
        }

        return parameters;
    }

    /**
     * Escape an html string. Escaping data received from the client helps to prevent cross-site script vulnerabilities.
     * 
     * @param html the html string to escape
     * @return the escaped string
     */
    private String escapeHtml(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    /** {@inheritedDoc} */
    public String calculateMetric(String metricTitle, Map<String, String> context) {
        Metric metric;
        try {
            metric = MetricFactory.createMetric(metricTitle);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return SERVER_ERROR;
        }

        try {
            Utils.validate(context, metric);
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return e.getMessage();
        }

        try {
            return getValue(context, metric);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return SERVER_ERROR;
        }
    }

    private String getValue(Map<String, String> context, Metric metric) throws Exception {
        for (RowLayout row : viewLayout.getLayout().get(0)) {
            MetricRowLayoutImpl metricRow = (MetricRowLayoutImpl)row;

            if (metricRow.getMetric().getType() == metric.getType()) {
                return metricRow.fill(context, 2).get(1);
            }
        }

        throw new IllegalStateException("Layout for metric " + metric.getType() + " not found");
    }

    /** {@inheritedDoc} */
    public List<ArrayList<String>> getMetricParametersList(String metricName) throws IOException {
        List<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();

        Metric metric = MetricFactory.createMetric(metricName);

        for (MetricParameter sp : metric.getParams()) {
            ArrayList<String> metricParameter = new ArrayList<String>();
            metricParameter.add(sp.getName());
            metricParameter.add(sp.getDefaultValue());
            metricParameter.add("");
            metricParameter.add(prepareParamName(sp));
            parameters.add(metricParameter);
        }

        return parameters;
    }

    private String prepareParamName(MetricParameter parameter) {
        String name = parameter.name();
        StringBuilder builder = new StringBuilder(name.length());

        String[] splitted = name.split("_");
        for (String str : splitted) {
            builder.append(Character.toUpperCase(str.charAt(0)));
            builder.append(str.substring(1).toLowerCase());
            builder.append(' ');
        }

        return builder.toString();
    }
}
