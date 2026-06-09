package com.karuna.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private final Map<String, List<Long>> requestLogs = new ConcurrentHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String ip = httpRequest.getRemoteAddr();
            long now = System.currentTimeMillis();

            List<Long> timestamps = requestLogs.computeIfAbsent(ip, k -> Collections.synchronizedList(new ArrayList<>()));

            synchronized (timestamps) {
                // Remove timestamps older than 1 minute
                timestamps.removeIf(time -> now - time > 60000);

                if (timestamps.size() >= MAX_REQUESTS_PER_MINUTE) {
                    httpResponse.setStatus(429); // Too Many Requests
                    httpResponse.setContentType("application/json");
                    httpResponse.getWriter().write("{\"message\":\"Too many requests. Please try again in a minute.\"}");
                    return;
                }
                timestamps.add(now);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
