package ru.valkovets.mephisoty.db.model.season.qa;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Column(name = "short_answer_visibility", nullable = false)
private AllowState shortAnswerVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@NotNull
@Enumerated
@Column(name = "rich_answer_visibility", nullable = false)
private AllowState richAnswerVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@ElementCollection(fetch = FetchType.EAGER)
@Column(name = "allow_files_answer", nullable = false)
@CollectionTable(name = "question_allowed_file_types", joinColumns = @JoinColumn(name = "owner_id"))
@NotNull
private Set<FileType> allowedFileTypes = new LinkedHashSet<>();

@Max(10)
@Column(name = "files_max")
@PositiveOrZero
private Integer filesMax = 0;

@NotNull
@Column(name = "max", nullable = false)
@Positive
private Float maxScore = 10f;

@NotNull
@Column(name = "min", nullable = false)
@PositiveOrZero
private Float minScore = 0f;

@OneToMany(mappedBy = "question", orphanRemoval = true)
@NotNull
private Set<Answer> answers = new LinkedHashSet<>();

}
