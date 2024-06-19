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
@Table(name = "group")
public class Group {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private String title;
}
