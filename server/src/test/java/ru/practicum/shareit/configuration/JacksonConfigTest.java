package ru.practicum.shareit.configuration;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JacksonConfigTest {

    @Test
    void hibernate5Module() {
        JacksonConfig jacksonConfig = new JacksonConfig();
        Hibernate5Module hibernate5Module = new Hibernate5Module();
        Assertions.assertEquals(hibernate5Module.getModuleName(), jacksonConfig.hibernate5Module().getModuleName());
    }
}