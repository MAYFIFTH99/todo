package book.todo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /**
     * HttpFilter와 OncePerRequestFilter의 차이점
     * OncePerRequestFilter는 FilterChain.doFilter()를 한 번만 호출하도록 보장하는 필터다.
     * 여러 API 서버들을 호출할 때, 각 서버마다 인증이 요청되지 않도록 하여 성능 보장
     */


    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try{
            String token = parseBearerToken(request);
            log.info("Filter is running");

            if(token != null && !token.equalsIgnoreCase("null")){
                String userId = tokenProvider.validateAndGetUserId(token);
                log.info("User id: {}", userId);

                //인증 완료; SecurityContextHolder에 등록해야 인증된 사용자라고 가정
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, //AuthenticationPrincipal
                        null,
                        AuthorityUtils.NO_AUTHORITIES);
                /**
                 * userId : 인증된 사용자의 정보로, 문자열이 아닌 아무거나 넣을 수 있다.
                 * 보통 UserDetails라는 오브젝트를 넣는데, 아직 만들지 않았다.
                 */

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);
            }
        }catch(Exception e){
            log.error("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request){
        // Http 요청의 헤더를 Parsing 하여 Bearer Token을 추출 (Bearer Token : 토큰에서 문자열만 추출한 값)
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7); // begin Index 인점 유의
        }
        return null;
    }
}
