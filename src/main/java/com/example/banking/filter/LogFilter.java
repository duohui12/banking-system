package com.example.banking.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        try{
            MDC.put("REQUEST_ID",uuid);
            log.info("REQUEST [{}]", requestURI);
            chain.doFilter(request,response);
        }finally{
            log.info("RESPONSE [{}]", requestURI);
            MDC.clear();
        }
    }

}
