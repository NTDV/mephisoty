package ru.valkovets.mephisoty.db.repository.userdata;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.userdata.Group;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface GroupRepository extends BasicRepository<Group> {
<T> Set<T> findByTitleIn(Collection<String> titles, Class<T> type);
}