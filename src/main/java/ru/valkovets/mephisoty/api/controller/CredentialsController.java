package ru.valkovets.mephisoty.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.valkovets.mephisoty.db.service.userdata.CredentialsService;

import java.util.Map;

@RestController
@RequestMapping("/api/creds")
@RequiredArgsConstructor
@Tag(name = "Аутентификация пользователей")
public class CredentialsController {
private final CredentialsService credentialsService;

@GetMapping("/{id}/name")
@Operation(summary = "Получить имя по служебному идентификатору")
public Map<String, String> getFullName(@PathVariable final Long id) {
    return Map.of("id", id.toString(), "name", credentialsService.getFullNameByCredentialsId(id));
}

}
