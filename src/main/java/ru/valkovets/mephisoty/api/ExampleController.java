package ru.valkovets.mephisoty.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.valkovets.mephisoty.db.service.userdata.CredentialsService;

@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
//@Tag(name = "Аутентификация")
public class ExampleController {
private final CredentialsService service;

@GetMapping
//@Operation(summary = "Доступен только авторизованным пользователям")
public String example() {
    return "Hello, world!";
}

@GetMapping("/admin")
//@Operation(summary = "Доступен только авторизованным пользователям с ролью ADMIN")
@PreAuthorize("hasRole('ADMIN')")
public String exampleAdmin() {
    return "Hello, admin!";
}
}