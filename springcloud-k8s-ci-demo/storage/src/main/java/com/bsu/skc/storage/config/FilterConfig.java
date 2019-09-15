package com.bsu.skc.storage.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import java.io.IOException;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean jsonMessageBean(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new Filter(){
            public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {}
            public void destroy() {}
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//                System.out.println(servletResponse.getWriter().toString());
                filterChain.doFilter(servletRequest,servletResponse);
            }
        });
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
