package book.todo.config;

import book.todo.security.JwtAuthenticationFilter;
import book.todo.security.OAuthUserServiceImpl;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuthUserServiceImpl oAuthUserService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/", "/auth/**","/oauth2/**").permitAll() //인증 없이 접근 허용
                                .anyRequest().authenticated()//나머지 요청은 인증이 필요
                )
                .oauth2Login(oauth2-> oauth2
                        .authorizationEndpoint(authorization -> authorization
                        .baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuthUserService))
                )
                .addFilterAfter(jwtAuthenticationFilter,
                        CorsFilter.class);//JwtAuthenticationFilter를 CorsFilter 다음에 실행

        return http.build();

    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://app.mayfifth99.store",
                "https://app.mayfifth99.store",
                "http://harvey-todo-ui.ap-northeast-2.elasticbeanstalk.com",
                "https://harvey-todo-ui.ap-northeast-2.elasticbeanstalk.com"
                ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
