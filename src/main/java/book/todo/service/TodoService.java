package book.todo.service;

import book.todo.dto.TodoDto;
import book.todo.entity.TodoEntity;
import book.todo.repository.TodoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public List<TodoDto> createTodo(TodoEntity todoEntity) {

        validate(todoEntity);

        todoRepository.save(todoEntity);

        log.info("Entity Id : {} is saved", todoEntity.getId());
        return todoRepository.findByUserId(todoEntity.getUserId()).stream().map(TodoDto::new)
                .toList();
    }

    public List<TodoEntity> getTodoList(String userId) {
        return todoRepository.findByUserId(userId);
    }

    public List<TodoEntity> updateTodo(TodoEntity entity) {
        validate(entity);

        //Optional 로 반환받지 않고 한 번에 처리하는 최적화 코드 구상
        todoRepository.findById(entity.getId()).ifPresent(todo -> {
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());
//            todoRepository.save(todo);
        });

        return getTodoList(entity.getUserId());
    }

    public List<TodoEntity> deleteTodo(TodoEntity entity) {
        validate(entity);

        try{
            todoRepository.deleteById(entity.getId());
        }catch (Exception e){
            log.error("deleteTodo error : {}", e.getMessage());

            throw new RuntimeException("deleteTodo error");
        }
        return getTodoList(entity.getUserId());
    }


    private void validate(TodoEntity todoEntity) {
        if (todoEntity == null) {
            log.warn("todoEntity is null");
            throw new RuntimeException("todoEntity is null");
        }

        if (todoEntity.getUserId() == null) {
            log.warn("userId is null");
            throw new RuntimeException("userId is null");
        }
    }

}
