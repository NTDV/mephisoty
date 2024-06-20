package ru.valkovets.mephisoty.application.services;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.model.files.File;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

void init() throws FileNotFoundException, NotDirectoryException;
Path getRootPath();

File save(final ByteArrayOutputStream stream, final String originalName, final User owner) throws IOException;
File save(final MultipartFile file, final User owner) throws FileSystemException;

void store(final ByteArrayOutputStream stream, final String fileName) throws IOException;
void store(MultipartFile file, String fileName) throws FileSystemException;
void store(StringBuilder data, String originalFileName, String filename) throws FileSystemException;

Path load(String filename);
Resource loadAsResource(String filename) throws FileNotFoundException;

Stream<Path> loadAll() throws FileSystemException;

void deleteAll();

}
