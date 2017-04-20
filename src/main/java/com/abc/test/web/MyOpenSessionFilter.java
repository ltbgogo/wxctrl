package com.abc.test.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;

//@WebFilter(filterName = "MyOpenSessionFilter", urlPatterns = "/*")
public class MyOpenSessionFilter implements Filter {
    
    private final OpenSessionInViewFilter filter;

    public MyOpenSessionFilter() {
        filter = new OpenSessionInViewFilter();
        filter.setSessionFactoryBeanName("sessionFactory_soc");
        
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        filter.init(filterConfig);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        filter.doFilter(request, response, chain);
    }

    @Override
    public void destroy() {
        filter.destroy();
    }
    
}