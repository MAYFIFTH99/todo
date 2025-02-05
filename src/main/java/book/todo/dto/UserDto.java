package book.todo.dto;

import book.todo.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String token;
    private String username;
    private String password;
    private String id;

    public static UserEntity toEntity(UserDto dto) {
        return UserEntity.builder()
            .id(dto.getId())
            .username(dto.getUsername())
            .password(dto.getPassword())
            .build();
    }
}
