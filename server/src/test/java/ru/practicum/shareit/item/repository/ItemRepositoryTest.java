package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
})
@ExtendWith(SpringExtension.class)
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private Item item2;

    private User user;
    private User user2;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        user2 = new User();
        user2.setName("TestUser2");
        user2.setEmail("TestUser2@email.ru");

        itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(user2);
        itemRequest.setDescription("Пожалуйста дайте мне testItem2 ");
        item = new Item(null, user, "testItem1", "testDescription", true,
                null);
        item2 = new Item(null, user, "testItem2", "testDescription2", true,
                itemRequest);
        Item item3 = new Item(null, user, "testItem3", "testDescription3", false,
                null);

        Booking booking2 = new Booking();
        Booking booking3 = new Booking();

        booking2.setId(2L);
        booking2.setItem(item2);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(booking2.getStart().plusHours(10));

        booking3.setId(3L);
        booking3.setItem(item3);
        booking3.setStart(LocalDateTime.now());
        booking3.setEnd(booking3.getStart().plusHours(10));
    }

    @DisplayName("Проверка загрузки контекста")
    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @DisplayName("Проверка получения предмета в БД владельцем")
    @Test
    void findItemByIdAndOwnerId() {
        em.persist(user);
        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());
    }

    @DisplayName("Проверка поиска предмета в базе данных")
    @Test
    void search() {
        em.persist(user);
        em.persist(user2);
        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());
        Optional<List<Item>> itemInList = itemRepository.search("Item1");
        Assertions.assertTrue(itemInList.isPresent());
        Assertions.assertEquals(item, itemInList.get().get(0));
    }

    @Test
    void findAllByRequests() {
        em.persist(user);
        em.persist(user2);
        em.persist(itemRequest);
        em.persist(item2);
        TypedQuery<ItemForRequestDto> query = em.getEntityManager()
                .createQuery("select new ru.practicum.shareit.item.dto" +
                        ".ItemForRequestDto(i.id, i.name, i.description, i.available , i.request.id)" +
                        " from Item as i " +
                        "where i.request.id IN :requestsId", ItemForRequestDto.class);
        ItemForRequestDto itemForRequest = query.setParameter("requestsId", List.of(itemRequest.getId()))
                .getSingleResult();
        Assertions.assertEquals(item2.getDescription(), itemForRequest.getDescription());
        Assertions.assertEquals(true, itemForRequest.getAvailable());
    }
}