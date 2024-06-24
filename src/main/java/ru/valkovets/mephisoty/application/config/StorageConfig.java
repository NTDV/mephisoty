package ru.valkovets.mephisoty.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties("storage")
@Setter
@Getter
public class StorageConfig {
private String location = "uploads";
}
