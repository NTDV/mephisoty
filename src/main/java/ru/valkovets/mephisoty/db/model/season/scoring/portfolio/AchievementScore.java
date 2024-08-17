package ru.valkovets.mephisoty.db.model.season.scoring.portfolio;

import io.hypersistence.utils.hibernate.type.array.FloatArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.AchievementType;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "achievement_score",
       uniqueConstraints = @UniqueConstraint(columnNames = { "participant_id", "stage_id" }))
public class AchievementScore extends BasicEntity {
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "participant_id", nullable = false)
@NotNull
private User participant;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "stage_id", nullable = false)
private Stage stage;

@NotNull
@Builder.Default
@Type(FloatArrayType.class)
@Column(name = "sum_data", nullable = false, columnDefinition = "float[]")
private float[] sum = new float[AchievementType._VALUES.length];

@NotNull
@Builder.Default
@Type(FloatArrayType.class)
@Column(name = "mean_data", nullable = false, columnDefinition = "float[]")
private float[] mean = new float[AchievementType._VALUES.length];

@NotNull
@Builder.Default
@Type(FloatArrayType.class)
@Column(name = "min_data", nullable = false, columnDefinition = "float[]")
private float[] min = new float[AchievementType._VALUES.length];

@NotNull
@Builder.Default
@Type(FloatArrayType.class)
@Column(name = "max_data", nullable = false, columnDefinition = "float[]")
private float[] max = new float[AchievementType._VALUES.length];

@NotNull
@Builder.Default
@Type(FloatArrayType.class)
@Column(name = "formula_data", nullable = false, columnDefinition = "float[]")
private float[] formula = new float[AchievementType._VALUES.length];

@NotNull
@Builder.Default
@Type(FloatArrayType.class)
@Column(name = "expert_data", nullable = false, columnDefinition = "float[]")
private float[] expert = new float[AchievementType._VALUES.length];
}
