package ru.valkovets.mephisoty.db.model.files;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.access.AccessDeniedException;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.FileAccessPolicy;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "file")
public class File extends BasicEntity {
@Length(max = 200)
@Column(name = "original_name", length = 200)
@NotBlank
private String originalName;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "owner_id", nullable = false)
@NotNull
private User owner;

@NotNull
@Builder.Default
@OneToMany(mappedBy = "avatar", orphanRemoval = true)
private Set<User> usersWithSuchAvatar = new HashSet<>();

@NotNull
@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "access_policy", nullable = false)
private FileAccessPolicy accessPolicy = FileAccessPolicy.ADMIN;

@NotNull
@Builder.Default
@ManyToMany(fetch = FetchType.LAZY, mappedBy = "files")
private Set<Answer> answers = new LinkedHashSet<>();

public static File tryGetByCurrentUser(final Optional<? extends File> file) throws NoResultException, AccessDeniedException {
    if (file.isEmpty()) throw new NoResultException("No such file");
    return tryGetBy(file.get(), Credentials.getCurrent());
}

public static File tryGetBy(@NotNull final File file, final Credentials credentials) throws AccessDeniedException {
    if (credentials == null && file.accessPolicy != FileAccessPolicy.ALL) throw new AccessDeniedException("User can not access the file.");

    if (file.accessPolicy == FileAccessPolicy.ALL ||
        file.accessPolicy == FileAccessPolicy.ALL_REGISTERED && credentials.getId() != null ||
        credentials.getRole() == UserRole.ADMIN ||
        file.accessPolicy == FileAccessPolicy.AUTHOR_AND_ADMIN && file.owner.equals(credentials.getUser()) ||
        file.accessPolicy == FileAccessPolicy.EXPERT_AND_ADMIN && credentials.getRole() == UserRole.EXPERT) {
        return file;
    } else throw new AccessDeniedException("User can not access the file.");
}
}
