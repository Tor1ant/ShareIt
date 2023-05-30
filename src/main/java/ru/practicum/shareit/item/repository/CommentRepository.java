package ru.practicum.shareit.item.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c "
            + "from Comment as c "
            + "where c.item.id = ?1")
    List<Comment> findAllByItemId(Long itemId);
}
