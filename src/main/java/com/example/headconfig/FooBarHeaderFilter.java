package com.example.headconfig;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


//Для более сложного фильтра нужно сделать Bean в CustomCongig
public class FooBarHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setHeader("Foo", "Bar");
        }
        chain.doFilter(request, response);
    }

    // Инициализация фильтра
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    // Очистка ресурсов фильтра
    @Override
    public void destroy() {}
}