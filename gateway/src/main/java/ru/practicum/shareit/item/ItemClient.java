package ru.practicum.shareit.item;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDTO;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder rest) {
        super(rest
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getUserItems(long userId) {

        return get("", userId);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> addItem(Long userId, ItemDTO itemDTO) {
        return post("", userId, itemDTO);
    }

    public ResponseEntity<Object> update(long userId, ItemDTO itemDTO, long itemId) {
        return patch("/" + itemId, userId, itemDTO);
    }

    public void deleteItem(long userId, long itemId) {
        delete("/" + userId, itemId);
    }

    public ResponseEntity<Object> searchItem(String text, long userId) {
        return get("/search?text={text}", userId, Map.of("text", text));
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, InputCommentDto text) {
        return post("/" + itemId + "/comment", userId, text);
    }
}
