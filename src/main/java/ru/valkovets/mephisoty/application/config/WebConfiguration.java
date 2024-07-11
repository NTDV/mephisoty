package ru.valkovets.mephisoty.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

@Override
public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
    for (final HttpMessageConverter<?> converter : converters) {
        if (converter instanceof org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) {
            final ObjectMapper mapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
            mapper.registerModule(new Hibernate6Module())
                  .registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module())
                  .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                  .registerModule(new org.springframework.boot.jackson.JsonMixinModule())
                  .registerModule(new org.springframework.boot.jackson.JsonComponentModule())
                  //.registerModule(new org.springframework.data.geo.GeoModule())
                  .registerModule(new SpringDataJacksonConfiguration.PageModule(null));
        }
    }
}
}
