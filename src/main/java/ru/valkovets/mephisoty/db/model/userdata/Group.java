package ru.valkovets.mephisoty.db.model.userdata;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "app_group", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
public class Group extends BasicEntity {

@NotBlank
@Length(min = 1, max = 10)
@NaturalId(mutable = false) // todo Подумать очень хорошо об имутабельности названий групп
@Column(name = "title", nullable = false, unique = true, length = 10)
private String title;

@NotNull
@Builder.Default
@OneToMany(mappedBy = "group")
private Set<User> users = new LinkedHashSet<>();
}
