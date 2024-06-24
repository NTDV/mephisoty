package ru.valkovets.mephisoty.db.model.files;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.FileAccessPolicy;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
@OneToMany(mappedBy = "avatar", orphanRemoval = true)
private Set<User> usersWithSuchAvatar = new HashSet<>();

@Enumerated
@NotNull
@Column(name = "access_policy")
private FileAccessPolicy accessPolicy = FileAccessPolicy.ADMIN;

@ManyToMany(fetch = FetchType.LAZY, mappedBy = "files")
@NotNull
private Set<Answer> answers = new LinkedHashSet<>();

}
