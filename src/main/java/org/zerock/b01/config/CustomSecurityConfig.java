package org.zerock.b01.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.zerock.b01.security.CustomUserDetailsService;
import org.zerock.b01.security.handler.Custom403Handler;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
// 어노테이션으로 권한 설정 @PreAuthorize, @PostAuthorize 이용해서 사후, 사전 권한 설정
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {


    // 자동 로그인 remember-me 설정 주입 필요
    private final DataSource dataSource;
    private final CustomUserDetailsService userDetailsService;

    // 로그인 없이 일단 사용할 수 있도록 처리
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("========================config====================================");

        // 커스텀 로그인 페이지 설정
        http.formLogin().loginPage("/member/login");

        // csrf 토큰 비활성화
        http.csrf().disable();

        // remember-me 설정
        http.rememberMe()
                .key("12345678")
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(userDetailsService)
                .tokenValiditySeconds(60 * 60 * 24 * 30);

        // 403 권한 아닌 경우 발생하는 403 에러 처리
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler()); // 403


        return http.build();

    }

    // 정적 파일 시큐리티 적용 제외
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        log.info("---------------------------web configure------------------------------");


        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


/*
    // PasswordEncoder 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
*/

    // remember-me를 위한 토큰 레포지토리 빈
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    // Custom403Handler 등록
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }

}
