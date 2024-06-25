package ru.valkovets.mephisoty.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;

@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class ExampleController {

@GetMapping
@Operation(summary = "Доступен только авторизованным пользователям")
public String example() {
    return "Hello, world!";
}

@GetMapping("/admin")
@Operation(summary = "Доступен только авторизованным пользователям с ролью ADMIN")
//@PreAuthorize("hasAuthority('ADMIN')") // good for services
public String exampleAdmin() {
    return "Hello, " + Credentials.getCurrent().getRole();
}
}