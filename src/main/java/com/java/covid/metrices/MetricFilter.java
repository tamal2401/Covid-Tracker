package com.java.covid.metrices;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
@Order(2)
public class MetricFilter implements Filter {

    public static final String TOTAL_COUNTER = "totalCounter";
    public static final String WELCOME_URI_PREFIX = "/welcome";
    private MetricServiceImpl metricService;

    public MetricFilter(MetricServiceImpl metricService) {
        this.metricService = metricService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        String contextUri = httpRequest.getRequestURI();
        String req = httpRequest.getMethod() + " " + contextUri;

        chain.doFilter(request, response);

        int status = ((HttpServletResponse) response).getStatus();
        if(!StringUtils.isBlank(contextUri) && (contextUri.startsWith("/welcome") || StringUtils.equals(contextUri, "/"))){
            metricService.increaseCount(TOTAL_COUNTER, status);
        }
        metricService.increaseCount(req, status);
    }
}
