package book.todo.service;

import book.todo.entity.UserEntity;
import book.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserEntity createUser(UserEntity userEntity) {
        if(userEntity == null || userEntity.getUsername() == null){
            throw new RuntimeException("Invalid arguments");
        }
        String username = userEntity.getUsername();
        if(userRepository.existsByUsername(username)){
            log.warn("Username already exists");
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(userEntity);
    }


    public UserEntity getByCredentials(String username, String password, PasswordEncoder encoder) {
        UserEntity originalUser = userRepository.findByUsername(username);

        if (originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }
        return null;
    }
}
