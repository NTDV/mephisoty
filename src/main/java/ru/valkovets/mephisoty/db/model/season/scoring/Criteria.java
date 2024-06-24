package ru.valkovets.mephisoty.db.model.season.scoring;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.superclass.TdrEntity;
import ru.valkovets.mephisoty.settings.ValidationConst;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "criteria")
public class Criteria extends TdrEntity {

@NotNull
@ManyToOne(fetch = FetchType.LAZY, optional = false)
private Stage stage;

@Length(max = 100)
@Column(name = "literal", nullable = false, unique = true, length = 100)
@Pattern(regexp = ValidationConst.LITERAL_PATTERN)
@NotBlank
private String literal;

@NotNull
@Column(name = "max", nullable = false)
@Positive
private Float max = 10f;

@NotNull
@Column(name = "min", nullable = false)
@PositiveOrZero
private Float min = 0f;

@OneToMany(fetch = FetchType.LAZY, mappedBy = "criteria", orphanRemoval = true)
private Set<CriteriaScore> criteriaScores = new LinkedHashSet<>();
}
