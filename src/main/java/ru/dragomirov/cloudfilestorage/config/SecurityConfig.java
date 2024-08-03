package ru.dragomirov.cloudfilestorage.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Настройка правил авторизации запросов
        http.authorizeRequests()
                .antMatchers("/sign-in").permitAll()
                .anyRequest().hasAnyRole("user")
                .and()
                .formLogin()
                // Страница, на которую пользователь будет перенаправлен для входа
                .loginPage("/sign-in")
                // URL, на который отправляются данные формы авторизации (исправьте на /process_login, если это ошибка)
                .loginProcessingUrl("/precess_login")
                .defaultSuccessUrl("/", true)
                // URL для перенаправления в случае ошибки авторизации
                .failureUrl("/auth/login?error")
                .and()
                .logout()
                // URL для выхода из системы
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login")
                .and()
                .headers().frameOptions().disable();
    }
}
