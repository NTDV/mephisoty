package ru.valkovets.mephisoty.db.model.files;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.access.AccessDeniedException;
import ru.valkovets.mephisoty.application.services.FileSystemStorageService;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.FileAccessPolicy;
import ru.valkovets.mephisoty.settings.UserRole;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "file")
@NamedEntityGraph(
    name = "file_with_creator",
    attributeNodes = { @NamedAttributeNode("createdBy") }
)
public class File extends BasicEntity {
@Length(max = 200)
@Column(name = "original_name", length = 200)
@Builder.Default
@NotBlank
private String originalName = "untitled";

@Length(max = 200)
@Column(name = "code", length = 200)
@Builder.Default
@NotNull
private String code = "";

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "avatar", orphanRemoval = true)
private Set<User> usersWithSuchAvatar = new LinkedHashSet<>();

@NotNull
@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "access_policy", nullable = false)
private FileAccessPolicy accessPolicy = FileAccessPolicy.CREATOR_ADMIN;

@NotNull
@Builder.Default
@ManyToMany(fetch = FetchType.LAZY, mappedBy = "files")
private Set<Answer> answers = new LinkedHashSet<>();

public Path tryGetByCurrentUser(final FileSystemStorageService fileSystemStorageService)
throws NoResultException, AccessDeniedException {
  return tryGetBy(this, Credentials.getCurrent(), fileSystemStorageService);
}

public static Path tryGetBy(@NotNull final File file, final Credentials credentials,
                            final FileSystemStorageService fileSystemStorageService) throws AccessDeniedException {
  if (canGetBy(file, credentials)) {
    return fileSystemStorageService.load(String.valueOf(file.getId()));
  } else {
    return null;
  }
}

public static boolean canGetBy(final File file, final Credentials credentials) {
  return
      file != null && (
          credentials == null && file.accessPolicy == FileAccessPolicy.ALL ||

          credentials != null && (
              credentials.getRole() == UserRole.ADMIN ||
              file.accessPolicy == FileAccessPolicy.ALL ||
              file.accessPolicy == FileAccessPolicy.REGISTERED ||

              file.accessPolicy == FileAccessPolicy.CREATOR_EXPERT_ADMIN
              && (credentials.getRole() == UserRole.EXPERT ||
                  credentials.getId().equals(file.getCreatedBy())) ||

              file.accessPolicy == FileAccessPolicy.CREATOR_ADMIN
              && credentials.getId().equals(file.getCreatedBy())));
}

public boolean canEditByCurrentUser() {
  return canEditBy(this, Credentials.getCurrent());
}

public static boolean canEditBy(final File file, final Credentials credentials) {
  return
      file != null && credentials != null && (
          credentials.getRole() == UserRole.ADMIN ||
          credentials.getId().equals(file.getCreatedBy()));
}

public boolean canGetByCurrentUser() {
  return canGetBy(this, Credentials.getCurrent());
}
}
