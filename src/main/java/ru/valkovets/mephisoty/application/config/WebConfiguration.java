package ru.valkovets.mephisoty.application.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
//@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {
@Override
public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
  converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
  converters.removeIf(MappingJackson2XmlHttpMessageConverter.class::isInstance);

  converters.add(new MappingJackson2HttpMessageConverter(
      new Jackson2ObjectMapperBuilder()
          .featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .modulesToInstall(new Hibernate6Module(),
                            new com.fasterxml.jackson.datatype.jdk8.Jdk8Module(),
                            new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule(),
                            new org.springframework.boot.jackson.JsonMixinModule(),
                            new org.springframework.boot.jackson.JsonComponentModule(),
                            //(new org.springframework.data.geo.GeoModule()),
                            new SpringDataJacksonConfiguration.PageModule(null))
          .build()));
}
}
