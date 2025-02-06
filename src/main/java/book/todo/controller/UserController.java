package book.todo.controller;

import book.todo.dto.ResponseDto;
import book.todo.dto.UserDto;
import book.todo.entity.UserEntity;
import book.todo.security.TokenProvider;
import book.todo.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDto dto) {
        try {
            if (dto == null || dto.getPassword() == null) {
                log.error("Invalid password value");
                throw new RuntimeException("Invalid password value]");
            }

            UserEntity userEntity = UserEntity.builder()
                    .id(null)
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .build();

            UserEntity savedUser = userService.createUser(userEntity);

            UserDto responseDto = UserDto.builder().id(savedUser.getId())
                    .username(savedUser.getUsername())
                    .build();
            return ResponseEntity.ok().body(responseDto);
        } catch (Exception e) {
            ResponseDto<UserDto> responseDto = ResponseDto.<UserDto>builder().error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDto);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDto dto) {
        UserEntity user = userService.getByCredentials(dto.getUsername(),
                dto.getPassword(), passwordEncoder);

        if (user != null) {
            String token = tokenProvider.createToken(user);
            UserDto userDto = UserDto.builder()
                    .username(user.getUsername())
                    .id(user.getId())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(userDto);
        }else{
            ResponseDto responseDto = ResponseDto.builder().error("login error").build();
            return ResponseEntity.badRequest().body(responseDto);
        }
    }

}
