package ru.practicum.shareit.request;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder rest) {
        super(rest
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> postItemRequest(ItemRequestInputDto itemRequestData, Long creatorId) {
        return post("", creatorId, itemRequestData);
    }

    public ResponseEntity<Object> getItemRequests(Long creatorId) {
        return get("", creatorId);
    }

    public ResponseEntity<Object> getItemRequestsInPages(Long userId, Long from, Long size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemRequest(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}