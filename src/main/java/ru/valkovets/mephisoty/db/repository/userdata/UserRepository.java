package ru.valkovets.mephisoty.db.repository.userdata;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

@Repository
public interface UserRepository extends BasicRepository<User> {

}