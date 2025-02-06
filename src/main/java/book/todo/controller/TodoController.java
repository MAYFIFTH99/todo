package book.todo.controller;

import book.todo.dto.ResponseDto;
import book.todo.dto.TodoDto;
import book.todo.entity.TodoEntity;
import book.todo.service.TodoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todo")
    public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId,
            @RequestBody TodoDto dto) {

        try {
            TodoEntity entity = TodoEntity.builder()
                    .id(null)
                    .userId(userId)
                    .title(dto.getTitle())
                    .done(false)
                    .build();

            List<TodoDto> dtos = todoService.createTodo(entity);
            ResponseDto<TodoDto> response = ResponseDto.<TodoDto>builder().data(dtos).build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseDto.builder().error(e.getMessage()).build());
        }
    }

    @GetMapping("/todo")
    public ResponseEntity<?> getTodoList(@AuthenticationPrincipal String userId) {

        List<TodoEntity> todoList = todoService.getTodoList(userId);

        List<TodoDto> dtos = todoList.stream().map(TodoDto::new)
                .toList();
        ResponseDto<TodoDto> response = ResponseDto.<TodoDto>builder().data(dtos).build();

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/todo")
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId,
            @RequestBody TodoDto dto) {

        TodoEntity entity = TodoDto.toEntity(dto);
        entity.setUserId(userId);

        List<TodoEntity> todoEntities = todoService.updateTodo(entity);
        List<TodoDto> response = todoEntities.stream().map(TodoDto::new).toList();

        return ResponseEntity.ok().body(ResponseDto.<TodoDto>builder().data(response).build());

    }

    @DeleteMapping("/todo")
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId,
            @RequestBody TodoDto dto) {
        TodoEntity entity = TodoDto.toEntity(dto);
        entity.setUserId(userId);

        List<TodoEntity> todoEntities = todoService.deleteTodo(entity);

        List<TodoDto> response = todoEntities.stream().map(TodoDto::new).toList();

        return ResponseEntity.ok().body(ResponseDto.<TodoDto>builder().data(response).build());
    }
}
