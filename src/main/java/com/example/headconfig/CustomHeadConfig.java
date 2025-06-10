package com.example.headconfig;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import jakarta.servlet.Filter; // или javax.servlet.Filter

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(Filter.class)
@EnableConfigurationProperties(CustomHeaderProperties.class)
@ConditionalOnProperty(prefix = "custom.http.header", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CustomHeadConfig {

    private static final Logger logger = LoggerFactory.getLogger(CustomHeadConfig.class);

    private final CustomHeaderProperties properties;

    public CustomHeadConfig(CustomHeaderProperties properties) {
        this.properties = properties;
    }

    @Bean
    public FilterRegistrationBean<FooBarHeaderFilter> fooBarHeaderFilterRegistrationBean() {
        FilterRegistrationBean<FooBarHeaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FooBarHeaderFilter());
        registrationBean.addUrlPatterns("/*"); // Применить ко всем URL
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        logger.info("FooBarHeaderFilter has been registered. Enabled: {}", properties.isEnabled());
        return registrationBean;
    }

}