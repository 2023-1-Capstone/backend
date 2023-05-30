package com.capstone.carbonlive.config;

import com.capstone.carbonlive.security.jwt.JwtAccessDeniedHandler;
import com.capstone.carbonlive.security.jwt.JwtAuthenticationEntryPoint;
import com.capstone.carbonlive.security.jwt.JwtAuthenticationFilter;
import com.capstone.carbonlive.security.jwt.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity //SecurityConfig.class 를 Spring Filter Chain 에 등록
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic().disable()  // rest api 이므로 기본설정 미사용
                .csrf().disable()   //rest api 이므로 csrf 보안 미사용
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //jwt 로 인증하므로 세션 미사용

                .and()

                .cors()

                .and()

                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/api/user/join").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/user/joinConfirm").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/user/login").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/user/reissue").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/user/password").permitAll()
                .anyRequest().authenticated()

                .and()

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()

                .build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.addExposedHeader("Authorization");
        configuration.setAllowedMethods(List.of("GET", "PATCH", "POST", "OPTIONS"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
