package ru.valkovets.mephisoty.db.model.season.qa;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
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
@NotNull
private Stage stage;

@NotNull
@Enumerated
@Builder.Default
@Column(name = "short_answer_visibility", nullable = false)
private AllowState shortAnswerVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@NotNull
@Enumerated
@Builder.Default
@Column(name = "rich_answer_visibility", nullable = false)
private AllowState richAnswerVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@NotNull
@Builder.Default
@ElementCollection(fetch = FetchType.EAGER)
@Column(name = "allow_files_answer", nullable = false)
@CollectionTable(name = "question_allowed_file_types", joinColumns = @JoinColumn(name = "owner_id"))
private Set<FileType> allowedFileTypes = new LinkedHashSet<>();

@Max(10)
@PositiveOrZero
@Builder.Default
@Column(name = "files_max")
private Integer filesMax = 0;

@NotNull
@Positive
@Builder.Default
@Column(name = "max", nullable = false)
private Float maxScore = 10f;

@NotNull
@PositiveOrZero
@Builder.Default
@Column(name = "min", nullable = false)
private Float minScore = 0f;

@NotNull
@Builder.Default
@OneToMany(mappedBy = "question", orphanRemoval = true)
private Set<Answer> answers = new LinkedHashSet<>();

}
