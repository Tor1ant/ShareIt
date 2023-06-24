package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerIdOrderByIdAsc(Long userId);

    @Query("select i "
            + "from Item as i "
            + "join fetch i.owner as o where i.id = :itemId and o.id = :ownerId")
    Item findItemByIdAndOwnerId(@Param("itemId") Long itemId, @Param("ownerId") Long ownerId);

    @Query("select i "
            + "from Item as i "
            + "where lower(i.name) like lower(concat('%' , :search, '%')) "
            + "or lower(i.description) like  lower(concat('%', :search,'%')) and i.available != false ")
    Optional<List<Item>> search(@Param("search") String searchString);

    Optional<Item> getItemByIdAndOwnerId(Long itemId, Long userId);

    void deleteByIdAndOwnerId(Long itemId, Long userId);

    @Query("select new ru.practicum.shareit.item.dto.ItemForRequestDto(i.id, i.name, i.description, i.available" +
            ", i.request.id) " +
            "from Item as i " +
            "where i.request.id IN :requestsId")
    List<ItemForRequestDto> findAllByRequests(@Param("requestsId") List<Long> requestsId);
}