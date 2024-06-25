package ru.valkovets.mephisoty.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.valkovets.mephisoty.db.dto.userdata.CredentialsDto;
import ru.valkovets.mephisoty.db.service.userdata.CredentialsService;

@RestController
@RequestMapping("/registration")
public class RegistrationController {
private final CredentialsService credentialsService;

public RegistrationController(final CredentialsService credentialsService) {
    this.credentialsService = credentialsService;
}

@PostMapping
public String createUser(@RequestBody final CredentialsDto credentials) {
    return credentialsService.create(credentials).getUser().getId().toString();
}

}
