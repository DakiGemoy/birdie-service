package com.priv.birdie.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

//@Slf4j
//@Component
public class CustomFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 1024 * 50;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper =
                new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);

        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper(response);

        String trxId = UUID.randomUUID().toString().replace("-", "");
        long startTime = System.currentTimeMillis();

//        ThreadContext.put("TRX_ID", trxId);
        responseWrapper.setHeader("trxId", trxId);

        try {
//            log.info("Incoming request [{}] : Origin [{}], Target [{} {}]",
//                    trxId,
//                    request.getHeader("Origin"),
//                    request.getMethod(),
//                    request.getRequestURI()
//            );

            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (Exception ex) {
//            log.error("Unhandled exception [{}]", trxId, ex);
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

//            logRequestBody(requestWrapper);
//            logResponseBody(responseWrapper);

//            log.info("Request [{}] completed in {} ms", trxId, duration);

            responseWrapper.copyBodyToResponse();
            ThreadContext.clearAll();
        }
    }
}
