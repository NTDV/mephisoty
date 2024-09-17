package ru.valkovets.mephisoty.db.model.season.qa;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.superclass.TdrEntity;
import ru.valkovets.mephisoty.settings.AllowState;
import ru.valkovets.mephisoty.settings.FileType;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "question")
public class Question extends TdrEntity {

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "stage_id", nullable = false)
private @NotNull Stage stage;

@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "short_answer_visibility", nullable = false)
private @NotNull AllowState shortAnswerVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "rich_answer_visibility", nullable = false)
private @NotNull AllowState richAnswerVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@Builder.Default
@ElementCollection(fetch = FetchType.EAGER)
@Column(name = "allow_files_answer", nullable = false)
@CollectionTable(name = "question_allowed_file_types", joinColumns = @JoinColumn(name = "owner_id"))
private @NotNull Set<FileType> allowedFileTypes = new LinkedHashSet<>();

@Builder.Default
@Column(name = "files_max")
private @Max(10)
@PositiveOrZero Integer filesMax = 0;

@Builder.Default
@Column(name = "max", nullable = false)
private @NotNull
@Positive Float maxScore = 10f;

@Builder.Default
@Column(name = "min", nullable = false)
private @NotNull
@PositiveOrZero Float minScore = 0f;

@Builder.Default
@OneToMany(mappedBy = "question", orphanRemoval = true)
private @NotNull Set<Answer> answers = new LinkedHashSet<>();

}
