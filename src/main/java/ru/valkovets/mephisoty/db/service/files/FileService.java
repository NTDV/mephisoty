package ru.valkovets.mephisoty.db.service.files;

import jakarta.persistence.NoResultException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.repository.files.FileRepository;

@Service
public class FileService {
private final FileRepository fileRepository;

public FileService(final FileRepository fileRepository) {
    this.fileRepository = fileRepository;
}

public File getById(final Long id) throws NoResultException, AccessDeniedException {
    return File.tryGetByCurrentUser(fileRepository.findById(id));
}

public File save(final File file) {
    return fileRepository.save(file);
}
}
