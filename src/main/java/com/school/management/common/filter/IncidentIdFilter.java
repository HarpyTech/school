package com.school.management.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter that extracts incident_id from HTTP requests and adds it to MDC for
 * logging.
 * Looks for incident_id in:
 * 1. X-Incident-ID header (primary method)
 * 2. incident_id query parameter (fallback)
 * 
 * Runs after CorrelationIdFilter (Order 2) to ensure correlation ID is set
 * first.
 */
@Component
@Order(2)
@Slf4j
public class IncidentIdFilter implements Filter {

    public static final String INCIDENT_ID_HEADER = "X-Incident-ID";
    public static final String INCIDENT_ID_PARAM = "incident_id";
    public static final String MDC_KEY = "incidentId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String incidentId = httpRequest.getHeader(INCIDENT_ID_HEADER);
        if (incidentId == null || incidentId.isBlank()) {
            incidentId = httpRequest.getParameter(INCIDENT_ID_PARAM);
        }

        String resolvedIncidentId;
        if (incidentId == null || incidentId.isBlank()) {
            resolvedIncidentId = "-";
        } else {
            resolvedIncidentId = incidentId.trim();
        }
        MDC.put(MDC_KEY, resolvedIncidentId);
        log.debug("Incident ID added to MDC: {}", resolvedIncidentId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
