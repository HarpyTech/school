package com.school.management.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory fixed-window rate limiter.
 * Note: This is per-instance and not distributed across replicas.
 * For distributed rate limiting, integrate a proper distributed cache or API gateway solution.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // Key: "client_addr:minute_slot", Value: request count
    private static final ConcurrentHashMap<String, AtomicLong> REQUEST_COUNTS = new ConcurrentHashMap<>();

    @Value("${app.rate-limit.enabled:true}")
    private boolean enabled;

    @Value("${app.rate-limit.requests-per-minute:60}")
    private long maxRequestsPerMinute;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!enabled || request.getRequestURI().startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        String client = request.getRemoteAddr();
        long minuteSlot = System.currentTimeMillis() / 60000;
        String key = client + ":" + minuteSlot;

        // Increment the request count for this client in this minute
        AtomicLong count = REQUEST_COUNTS.computeIfAbsent(key, k -> new AtomicLong(0));
        long currentCount = count.incrementAndGet();

        // Check if limit exceeded
        if (currentCount > maxRequestsPerMinute) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"success\":false,\"message\":\"Rate limit exceeded\"}");
            return;
        }

        // Periodic cleanup of old entries (every 1000 requests to avoid overhead)
        if (REQUEST_COUNTS.size() > 1000) {
            cleanupOldEntries(minuteSlot);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Remove entries from the previous minute to prevent unlimited growth.
     */
    private void cleanupOldEntries(long currentMinuteSlot) {
        REQUEST_COUNTS.entrySet().removeIf(entry -> {
            // Extract minute slot from key format: "client_addr:minute_slot"
            String[] parts = entry.getKey().split(":");
            if (parts.length >= 2) {
                try {
                    long entryMinuteSlot = Long.parseLong(parts[parts.length - 1]);
                    // Remove entries older than 2 minutes
                    return entryMinuteSlot < currentMinuteSlot - 2;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return false;
        });
    }
}
