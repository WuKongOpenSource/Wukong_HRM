package com.kakarote.core.config;

import cn.hutool.extra.servlet.ServletUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 请求体重复读取过滤器
 * 解决请求体只能读取一次的问题
 * @author zhangzhiwei
 */
@Component
@WebFilter(filterName = "bodyReaderFilter", urlPatterns = "/*")
@ConditionalOnClass(value = {ServletRequest.class, HandlerMapping.class})
public class BodyReaderFilter implements Filter, Ordered {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = ((HttpServletRequest) servletRequest);
            if (request.getHeader("Content-Type") != null && request.getHeader("Content-Type").startsWith("application/json")) {
                requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
            }
        }
        if (requestWrapper == null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(requestWrapper, servletResponse);
        }
    }

    @Override
    public int getOrder() {
        return -9;
    }

    public static class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private byte[] body;

        public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            body = ServletUtil.getBodyBytes(request);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {

            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);

            return new ServletInputStream() {

                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }
            };
        }
    }
}
