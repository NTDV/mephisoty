package ru.valkovets.mephisoty.db.repository.userdata;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.userdata.Group;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

@Repository
public interface GroupRepository extends BasicRepository<Group> {

}