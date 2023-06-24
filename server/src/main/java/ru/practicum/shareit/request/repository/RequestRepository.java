package ru.practicum.shareit.request.repository;

import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Long>, PagingAndSortingRepository<ItemRequest, Long> {
    Long countAllByRequesterId(Long requesterId);

    List<ItemRequest> findAllByRequesterIdOrderByCreatedAsc(Long requesterId, Pageable pageable);

    List<ItemRequest> findAllByRequesterIdNot(Long userID, Pageable pageable);

    boolean existsById(@NonNull Long requestId);
}
