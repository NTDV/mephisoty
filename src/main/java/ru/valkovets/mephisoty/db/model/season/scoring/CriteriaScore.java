package ru.valkovets.mephisoty.db.model.season.scoring;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.userdata.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "criteria_score",
       uniqueConstraints = @UniqueConstraint(columnNames = { "criteria_id", "expert_id", "participant_id" }))
public class CriteriaScore extends BasicEntity {

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "criteria_id", nullable = false)
@NotNull
private Criteria criteria;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "expert_id", nullable = false)
@NotNull
private User expert;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "participant_id", nullable = false)
@NotNull
private User participant;

@NotNull
@PositiveOrZero
@Builder.Default
@Column(name = "score", nullable = false)
private Float score = 0f;
}
