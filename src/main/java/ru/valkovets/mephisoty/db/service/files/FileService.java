package ru.valkovets.mephisoty.db.service.files;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.projection.special.file.FileFullProj;
import ru.valkovets.mephisoty.db.repository.files.FileRepository;

@Service
@RequiredArgsConstructor
public class FileService {
private final FileRepository fileRepository;

public File get(final Long fileId) {
  return fileRepository.findById(fileId).orElseThrow(NoResultException::new);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Page<FileFullProj> getAll(final int page, final int size, final Specification<File> specification, final Sort sort) {
  return fileRepository.findBy(Specification.where(specification),
                               q -> q.sortBy(sort == null ? Sort.unsorted() : sort)
                                     .as(FileFullProj.class)
                                     .page(PageRequest.of(page, size)));
}
}
