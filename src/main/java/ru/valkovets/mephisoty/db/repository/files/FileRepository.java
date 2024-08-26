package ru.valkovets.mephisoty.db.repository.files;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Optional;

@Repository
public interface FileRepository extends BasicRepository<File> {
@EntityGraph("file_with_creator")
Optional<File> findById(Long id);
}