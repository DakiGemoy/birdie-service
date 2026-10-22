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
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class GeneralFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 1024 * 50;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String trxId = UUID.randomUUID().toString().replace("-", "").substring(0,9);
        long startTime = System.currentTimeMillis();

        ThreadContext.put("TRX_ID", trxId);
        responseWrapper.setHeader("trxId", trxId);

        try {
            log.info("Incoming request : Origin [{}], Target [{} {}], Params [{}]",
                    request.getHeader("Origin"),
                    request.getMethod(),
                    request.getRequestURI(),
                    flattenParamMap(request.getParameterMap())
            );
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (Exception ex) {
            log.error("Unhandled exception", ex);
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            logRequestBody(requestWrapper);
            logResponseBody(responseWrapper);

            log.info("Request completed in {} ms", duration);

            responseWrapper.copyBodyToResponse();
            ThreadContext.clearAll();
        }
    }

    private void logRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            String body = new String(buf, StandardCharsets.UTF_8);
            try {
                String jsonbody = maskingRequestJson(body);

                log.info("[Request Body] : {}", body.isBlank() ? "-" : jsonbody);
            } catch (Exception e){
                log.info("[Request Body] : {}", body.isBlank() ? "-" : body);
            }
        }
    }

    private void logResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length > 0) {
            String contentType = response.getContentType();

            if (contentType != null && contentType.contains("application/json")) {
                String body = new String(buf, StandardCharsets.UTF_8);

                if (body.length()>1000)
                    log.info("[Response] : {}", body.isBlank() ? "-" : (body.substring(0,1000))+"-------");
                else
                    log.info("[Response] : {}", body.isBlank() ? "-" : body);
                return;
            }

            log.info("[Response] : -");
        }
    }

    private String flattenParamMap(Map<String,String[]> params){
        StringBuilder build= new StringBuilder();
        if (params.isEmpty())
            return "";
        else{
            int counter=0;
            for (var m : params.entrySet()){
                build.append(m.getKey()).append("->");
                build.append(m.getValue()[0]);

                if (counter<(params.size()-1)){
                    build.append(", ");
                }

                counter++;
            }
        }
        return build.toString();
    }

    protected void maskNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            List<Map.Entry<String,JsonNode>> fields = new ArrayList<>(objectNode.properties());

            for (Map.Entry<String,JsonNode> entry : fields){
                String fieldName = entry.getKey();

                if (fieldName.toLowerCase().contains("password")) {
                    objectNode.put(fieldName, "*************");
                } else {
                    maskNode(entry.getValue());
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                maskNode(child);
            }
        }
    }

    private String maskingRequestJson(String jsonBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(jsonBody);
            maskNode(node);
            return mapper.writeValueAsString(node);
        } catch (Exception e){
            return jsonBody;
        }
    }
}
