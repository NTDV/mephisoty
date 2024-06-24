package ru.valkovets.mephisoty.db.service.files;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.repository.files.FileRepository;

import java.util.UUID;

@Service
public class FileService {
private final FileRepository fileRepository;

public FileService(final FileRepository fileRepository) {this.fileRepository = fileRepository;}

public File getById(final UUID id) throws EntityNotFoundException {
    return fileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("File not found."));
}

public File save(final File file) {
    return fileRepository.save(file);
}
}
