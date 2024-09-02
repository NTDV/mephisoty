package ru.valkovets.mephisoty.db.model.season.qa;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.userdata.User;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "answer")
public class Answer extends BasicEntity {
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "question_id", nullable = false)
@NotNull
private Question question;

@Length(max = 120)
@Column(name = "short", length = 120)
@Builder.Default
private String shortAnswer = "";

@Length(max = 4000)
@Column(name = "rich", length = 4000)
@Builder.Default
private String richAnswer = "";

@NotNull
@Builder.Default
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(name = "answer_files",
           joinColumns = @JoinColumn(name = "answer_id"),
           inverseJoinColumns = @JoinColumn(name = "files_id"))
private Set<File> files = new LinkedHashSet<>();

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "participant_id", nullable = false)
@NotNull
private User participant;

@ManyToOne(fetch = FetchType.LAZY, optional = true)
@JoinColumn(name = "expert_id", nullable = true)
private User expert;

@Column(name = "score")
@PositiveOrZero
@Builder.Default
private Float score = 0f;
}
