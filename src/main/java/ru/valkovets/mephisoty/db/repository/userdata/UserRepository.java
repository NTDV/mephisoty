package ru.valkovets.mephisoty.db.repository.userdata;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.NamesProj;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends BasicRepository<User> {
@Query("select new ru.valkovets.mephisoty.db.projection.NamesProj(u.firstName, u.secondName, u.thirdName) from User u where u.credentials.id = ?1")
Optional<NamesProj> getFullNameByCredentialsId(Long credentialsId);
}