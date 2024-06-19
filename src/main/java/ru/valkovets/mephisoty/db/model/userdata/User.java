package ru.valkovets.mephisoty.db.model.userdata;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

// credentials
// photo
// state?

private String firstName;
private String secondName;
private String thirdName;

// Одна группа у одного человека
private Group group;
}
