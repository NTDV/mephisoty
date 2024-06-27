package ru.valkovets.mephisoty.db.model.season.scoring;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.superclass.TdrEntity;
import ru.valkovets.mephisoty.settings.ValidationConst;

import java.util.LinkedHashSet;
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

@NotBlank
@Length(max = 100)
@Pattern(regexp = ValidationConst.LITERAL_PATTERN)
@Column(name = "literal", nullable = false, unique = true, length = 100)
private String literal;

@NotNull
@Positive
@Builder.Default
@Column(name = "max", nullable = false)
private Float max = 10f;

@NotNull
@PositiveOrZero
@Builder.Default
@Column(name = "min", nullable = false)
private Float min = 0f;

@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "criteria", orphanRemoval = true)
private Set<CriteriaScore> criteriaScores = new LinkedHashSet<>();
}
