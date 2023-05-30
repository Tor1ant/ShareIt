package ru.practicum.shareit.item.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

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
}