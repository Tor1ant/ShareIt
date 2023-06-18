package ru.practicum.shareit;

import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class ShareItServer {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ShareItServer.class);
        springApplication.setDefaultProperties(Collections.singletonMap("server.port", "9090"));
        springApplication.run(args);
    }
}
