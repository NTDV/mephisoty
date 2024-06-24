package ru.valkovets.mephisoty.db.repository.userdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.userdata.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

}