package ru.valkovets.mephisoty.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.valkovets.mephisoty.application.config.StorageConfig;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.repository.files.FileRepository;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Collections;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {
private final FileRepository fileRepository;
private final Path rootLocation;

@Autowired
public FileSystemStorageService(final FileRepository fileRepository, final StorageConfig configuration) throws IOException {
    this.rootLocation = Paths.get(configuration.getLocation());
    this.fileRepository = fileRepository;
    init();
}

@Override
public void init() throws FileNotFoundException, NotDirectoryException {
    if (Files.isDirectory(rootLocation)) return;
    if (Files.exists(rootLocation)) throw new NotDirectoryException(rootLocation.toString());

    try {
        Files.createDirectory(rootLocation);
    } catch (final IOException e) {
        throw new FileNotFoundException("Could not initialize storage at " + rootLocation);
    }
}

@Override
public Path getRootPath() {
    return rootLocation;
}

@Override
public File save(final ByteArrayOutputStream stream, final String originalName, final User owner) throws IOException {
    final File fileEntity = fileRepository.save(File.builder().title(originalName).owner(owner).build());
    store(stream, fileEntity.getId().toString());
    return fileEntity;
}

@Override
public File save(final MultipartFile file, final User owner) throws FileSystemException {
    final File fileEntity = fileRepository.save(File.builder().title(file.getOriginalFilename()).owner(owner).build());
    store(file, fileEntity.getId().toString());
    return fileEntity;
}

public void store(final ByteArrayOutputStream stream, final String fileName) throws IOException {
    try (final FileOutputStream outputStream = new FileOutputStream(rootLocation.resolve(fileName).toFile())) {
        stream.writeTo(outputStream);
    }
}

@Override
public void store(final MultipartFile file, final String fileName) throws FileSystemException {
    try {
        if (file.isEmpty()) throw new FileSystemException("Failed to store empty file " + file.getOriginalFilename());
        Files.copy(file.getInputStream(), rootLocation.resolve(fileName));
    } catch (final IOException e) {
        throw new FileSystemException(file.getOriginalFilename(), e.getMessage(), "Failed to store file");
    }
}

@Override
public void store(final StringBuilder data, final String originalFileName, final String filename) throws FileSystemException {
    try {
        Files.write(rootLocation.resolve(filename), Collections.singleton(data));
    } catch (final IOException e) {
        throw new FileSystemException(originalFileName, e.getMessage(), "Failed to store file");
    }
}

@Override
public Path load(final String filename) {
    return rootLocation.resolve(filename);
}

@Override
public Resource loadAsResource(final String filename) throws FileNotFoundException {
    try {
        final Path file = load(filename);
        final Resource resource = new UrlResource(file.toUri());

        if (resource.exists() || resource.isReadable()) return resource;
        else throw new FileNotFoundException("Could not read file: " + filename);

    } catch (final MalformedURLException e) {
        throw new FileNotFoundException("Could not read file: " + filename);
    }
}

@Override
public Stream<Path> loadAll() throws FileSystemException {
    try (final Stream<Path> walk = Files.walk(rootLocation, 1)) {
        return walk.filter(path -> !path.equals(rootLocation))
                   .map(path -> rootLocation.relativize(path));
    } catch (final IOException e) {
        throw new FileSystemException("Failed to read stored files");
    }
}

@Override
public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
}
}
