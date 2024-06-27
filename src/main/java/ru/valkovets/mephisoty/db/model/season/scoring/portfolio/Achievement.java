package ru.valkovets.mephisoty.db.model.season.scoring.portfolio;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.userdata.User;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "achievement")
public class Achievement extends BasicEntity {
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "owner_id", nullable = false)
private User owner;

@Column(name = "recived_at", nullable = false)
@PastOrPresent
@NotNull
private LocalDate receivedAt;

@Length(max = 120)
@NotBlank
@Column(name = "title", nullable = false, length = 120)
private String title;

@NotNull
@Length(max = 2000)
@Builder.Default
@Column(name = "description", nullable = false, length = 2000)
private String description = "";

@NotNull
@PositiveOrZero
@Builder.Default
@Column(name = "score", nullable = false)
private Float pgasScore = 0f;

@ManyToOne(fetch = FetchType.EAGER, optional = false)
@JoinColumn(name = "achievement_type_id", nullable = false)
@NotNull
private AchievementType achievementType;
}
