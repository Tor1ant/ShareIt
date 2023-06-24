package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestData;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceIntegrationTest {

    private final ItemRequestServiceImpl itemRequestService;


    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private User user;
    private User user2;
    private ItemRequestData itemRequestData;
    private ItemRequest itemRequest;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        user.setId(1L);

        user2 = new User();
        user2.setName("testUser2");
        user2.setEmail("test@email.ru2");

        itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setDescription("тестовое описание запрашиваемого предмета");
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestInputDto itemRequestInputDto = new ItemRequestInputDto("Тестовый предмет",
                LocalDateTime.now());
        itemRequestData = new ItemRequestData(itemRequestInputDto, user.getId());
    }

    @DisplayName("Тестирование создания запроса предмета  пользователем")
    @Test
    void postItemRequestTest() {
        userRepository.save(user);
        String itemDescription = itemRequestService.postItemRequest(itemRequestData).getDescription();
        Assertions.assertEquals("Тестовый предмет", itemDescription);
    }

    @DisplayName("Проверка получения запросов предметов владельцем")
    @Test
    void getItemRequestsTest() {
        userRepository.save(user);
        requestRepository.save(itemRequest);
        Assertions.assertEquals("тестовое описание запрашиваемого предмета",
                itemRequestService.getItemRequests(1L).get(0).getDescription());
    }

    @DisplayName("Проверка получения запросов не владельцем")
    @Test
    void getItemRequestsInPagesTest() {
        userRepository.save(user);
        userRepository.save(user2);
        requestRepository.save(itemRequest);
        Assertions.assertEquals("тестовое описание запрашиваемого предмета",
                itemRequestService.getItemRequestsInPages(2L,0L,4L).get(0).getDescription());
    }

    @DisplayName("Проверка получения одного запроса предмета пользователем")
    @Test
    void getItemRequestTest() {
        userRepository.save(user);
        requestRepository.save(itemRequest);
        Assertions.assertEquals("тестовое описание запрашиваемого предмета",
                itemRequestService.getItemRequest(1L, 1L).getDescription());
    }
}