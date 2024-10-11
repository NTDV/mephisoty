package ru.valkovets.mephisoty.db.model.season.scoring;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.userdata.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "stage_score",
       uniqueConstraints = @UniqueConstraint(columnNames = { "stage_id", "participant_id" }))
@NamedEntityGraphs({
    @NamedEntityGraph(name = "stageScoreWithStage", attributeNodes = @NamedAttributeNode("stage")),
    @NamedEntityGraph(name = "stageScoreWithParticipant", attributeNodes = @NamedAttributeNode("participant"))
})
public class StageScore extends BasicEntity {

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "stage_id", nullable = false)
private @NotNull Stage stage;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "participant_id", nullable = false)
private @NotNull User participant;

@Column(name = "initial_score")
@Builder.Default
private @PositiveOrZero Float initialScore = 0f;

@Column(name = "formula_score")
@Builder.Default
private @PositiveOrZero Float scoreByStageFormula = 0f;

@Column(name = "place", nullable = false)
private Long place;
}
