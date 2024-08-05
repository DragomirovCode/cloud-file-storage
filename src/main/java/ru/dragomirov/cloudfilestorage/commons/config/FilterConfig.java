package ru.dragomirov.cloudfilestorage.commons.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.dragomirov.cloudfilestorage.commons.SessionTimeoutFilter;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<SessionTimeoutFilter> sessionTimeoutFilter() {
        FilterRegistrationBean<SessionTimeoutFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SessionTimeoutFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
