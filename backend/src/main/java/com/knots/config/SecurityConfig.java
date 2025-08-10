package com.knots.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/admin/login").permitAll()
                .antMatchers("/admin/auth/**").permitAll()
                .antMatchers("/admin/**").authenticated()
                .anyRequest().permitAll()
            .and()
            .formLogin()
                .loginPage("/admin/login")
                .defaultSuccessUrl("/admin/dashboard")
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login")
                .permitAll()
            .and()
            .headers().frameOptions().disable(); // 允许H2控制台在iframe中显示
        
        return http.build();
    }
}
