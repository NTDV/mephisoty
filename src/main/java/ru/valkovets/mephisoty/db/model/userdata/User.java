package ru.valkovets.mephisoty.db.model.userdata;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.settings.UserState;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {
@Id
@SequenceGenerator(name = "user_id_generator", sequenceName = "user_id_seq", initialValue = 2000, allocationSize = 1)
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
private Long id;

private User createdBy;
private OffsetDateTime createdAt;
private User editedBy;
private OffsetDateTime editedAt;
private String comment;

// todo credentials
private File avatar;
private UserState state;

private String firstName;
private String secondName;
private String thirdName;

// todo Одна группа у одного человека и вообще везде проставить аннотации + валидации
private Group group;
}
