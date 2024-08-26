package ru.valkovets.mephisoty.db.model.season.scoring;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.userdata.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "total_score",
       uniqueConstraints = @UniqueConstraint(columnNames = { "season_id", "participant_id" }),
       indexes = @Index(columnList = "score"))
public class SeasonScore extends BasicEntity {
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "season_id", nullable = false)
@NotNull
private Season season;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "participant_id", nullable = false)
@NotNull
private User participant;

@Column(name = "place", nullable = false)
private Long place;

@Column(name = "score")
@PositiveOrZero
private Float scoreBySeasonFormula;
}
