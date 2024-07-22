package ru.valkovets.mephisoty.db.model.season.scoring;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.api.dto.season.CriteriaDto;
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
@NamedEntityGraph(name = "criteria_full", attributeNodes = {
    @NamedAttributeNode("createdAt"),
    @NamedAttributeNode("modifiedAt"),
    @NamedAttributeNode("createdBy"),
    @NamedAttributeNode("modifiedBy"),
    @NamedAttributeNode("stage"),
    @NamedAttributeNode("criteriaScores")
})
public class Criteria extends TdrEntity {

@JsonBackReference
@NotNull
@ManyToOne(fetch = FetchType.LAZY, optional = false)
private Stage stage;

//@NotBlank
@Nullable
@Length(max = 100)
@Pattern(regexp = ValidationConst.LITERAL_PATTERN)
@Column(name = "literal", unique = true, length = 100)
private String literal;

@NotNull
@PositiveOrZero
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

public static Criteria from(final CriteriaDto criteriaDto, final Stage stage) {
    return Criteria.builder()
                   .stage(stage)
                   .comment(criteriaDto.comment())
                   .title(criteriaDto.title())
                   .description(criteriaDto.description())
                   .rules(criteriaDto.rules())
                   .literal(criteriaDto.literal())
                   .max(criteriaDto.max())
                   .min(criteriaDto.min())
                   .build();
}

public Criteria editFrom(final CriteriaDto dto) {
    setComment(dto.comment());
    setTitle(dto.title());
    setDescription(dto.description());
    setRules(dto.rules());
    literal = dto.literal();
    max = dto.max();
    min = dto.min();
    return this;
}
}
