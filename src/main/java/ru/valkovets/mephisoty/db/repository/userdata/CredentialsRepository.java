package ru.valkovets.mephisoty.db.repository.userdata;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends BasicRepository<Credentials> {
    boolean existsById(long id);

Optional<Credentials> findByMephiLogin(String mephiLogin);
}