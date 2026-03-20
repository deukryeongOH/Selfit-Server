package selfit.selfit.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import selfit.selfit.global.security.jwt.JwtAuthenticationFilter;
import selfit.selfit.global.security.jwt.TokenProvider;
import selfit.selfit.global.security.oauth.CustomOAuth2UserService;
import selfit.selfit.global.security.oauth.OAuth2LoginFailureHandler;
import selfit.selfit.global.security.oauth.OAuth2LoginSuccessHandler;
import selfit.selfit.global.security.springsecurity.CustomUserDetailsService;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception{
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .userDetailsService(customUserDetailsService)
//                .passwordEncoder(passwordEncoder())
//                .build();
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .authorizeHttpRequests(auth -> auth
                        // 인증, 인가 필요한 url 지정
                        // 카카오 로그인 시작 URI
                        .requestMatchers("/oauth2/authorization/kakao").permitAll()
                        // 카카오 콜백 URI
                        .requestMatchers("/oauth2/login/kakao").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll() // 회원가입, 로그인은 인증 없이 가능
                        .anyRequest().authenticated()   // 그외의 모든 url은 인증 필요
                )
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // OAuth2
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(ae -> ae
                                .baseUri("/oauth2/authorization/{registrationId}")
                        )
                        .redirectionEndpoint(re -> re
                                .baseUri("/oauth2/login/*")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                // jwt 필터
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
//                .formLogin(form -> form
//                        // form login 방식 적용
//                        .usernameParameter("accountId")   // 로그인할 때 사용되는 id를 적어줌(여기서는 accounId로 로그인 하기 때문에 따로 적어줌. userName으로 로그인 한다면 적어주지 않아도 됨)
//                        .passwordParameter("password")  // 로그인할 때 사용되는 password를 적어줌
//                        .loginPage("/api/auth/login") // (optional) 로그인 GET 페이지 url
//                        .loginProcessingUrl("/api/auth/login") // post시 이 URL을 가로챔
//                        .successHandler((req, res, auth) -> {
//                            // 성공 시 200 OK, 토큰·사용자 정보를 직접 body에 쓰려면 ApiController로 위임해도 됩니다.
//                            res.setStatus(HttpServletResponse.SC_OK);
//                        })
//                        .failureHandler((req, res, ex) -> {
//                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
//                        })
//                        .permitAll()
//                ) jwt 사용하므로 제외
//                .logout(logout -> logout
//                        // 로그아웃에 대한 정보
//                        .logoutUrl("/api/auth/logout") // logout 시 나올 페이지
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                )

        return http.build();
    }

    // 2) CORS 설정 소스 정의
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 허용할 프론트엔드 주소
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 허용할 헤더
        config.setAllowedHeaders(List.of("*"));
        // 자격증명(쿠키, Authorization 헤더) 허용
        config.setAllowCredentials(true);

        // 모든 경로에 위 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }



}
