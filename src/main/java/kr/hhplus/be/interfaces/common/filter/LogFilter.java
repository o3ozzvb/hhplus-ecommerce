package kr.hhplus.be.interfaces.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(httpResponse);

        // Trace ID 생성 및 추가
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        try {
            chain.doFilter(cachingRequest, cachingResponse);
        } finally {
            MDC.clear();  // MDC 내용 제거
        }

        // request
        String uri = cachingRequest.getRequestURI();
        String requestBody = new String(cachingRequest.getContentAsByteArray());
        log.info("Request -> TraceId: {}, URI: {}, Method: {}, Body: {}",
                traceId,
                uri,
                httpRequest.getMethod(),
                requestBody.isEmpty() ? "No Content" : requestBody);

        // response
        int status = cachingResponse.getStatus();
        String responseBody = new String(cachingResponse.getContentAsByteArray());
        log.info("Response -> TraceId: {}, Status: {}, Body: {}",
                traceId,
                status,
                responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody); // 500자 이상이면 생략

        // 응답 본문 복사
        cachingResponse.copyBodyToResponse();
    }
}
