package ru.valkovets.mephisoty.db.model.season;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.model.superclass.TdrseEntity;
import ru.valkovets.mephisoty.settings.AllowState;
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
@Table(name = "season")
public class Season extends TdrseEntity {
@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "season", orphanRemoval = true)
private Set<Stage> stages = new LinkedHashSet<>();

@Length(max = 1000)
@Column(name = "formula", length = 1000)
@Pattern(regexp = ValidationConst.FORMULA_PATTERN)
private String seasonResultFormula; // math + Season stages

@NotNull
@Enumerated
@Column(name = "stage_visibility", nullable = false)
private AllowState stageVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@NotNull
@Enumerated
@Column(name = "score_visibility", nullable = false)
private AllowState scoreVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@OneToMany(fetch = FetchType.LAZY, mappedBy = "season", orphanRemoval = true)
private Set<SeasonScore> seasonScores = new LinkedHashSet<>();
}