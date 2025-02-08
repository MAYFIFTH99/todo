package book.todo.security;

import book.todo.entity.UserEntity;
import book.todo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OAuthUserServiceImpl extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    public OAuthUserServiceImpl(){
        super();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2User: {}", oAuth2User);

        try{
            log.info("OAuth2User attributes: {}", new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String username = (String)oAuth2User.getAttribute("login");
        String authProvider = userRequest.getClientRegistration().getClientName();

        if(!userRepository.existsByUsername(username)){
            UserEntity user = UserEntity.builder()
                    .username(username)
                    .authProvider(authProvider)
                    .build();
            userRepository.save(user);
        }
        log.info("username {} authProvider {}", username, authProvider);
        return oAuth2User;
    }
}
