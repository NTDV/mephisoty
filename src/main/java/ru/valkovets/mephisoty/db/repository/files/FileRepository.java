package ru.valkovets.mephisoty.db.repository.files;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.List;

@Repository
public interface FileRepository extends BasicRepository<File> {
List<File> findAllByTitle(final String title);
List<File> findAllByOwner(final User owner);
}