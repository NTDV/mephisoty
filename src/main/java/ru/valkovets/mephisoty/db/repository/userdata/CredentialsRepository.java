package ru.valkovets.mephisoty.db.repository.userdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.model.userdata.Group;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends BasicRepository<Credentials> {
    Optional<Credentials> findByEmailAndPassword(String email, String hash);
    Optional<Credentials> findByEmail(String email);
}