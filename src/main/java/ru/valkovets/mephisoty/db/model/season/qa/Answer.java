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
private @NotNull Question question;

@Column(name = "short", length = 120)
@Builder.Default
private @Length(max = 120) String shortAnswer = "";

@Column(name = "rich", length = 4000)
@Builder.Default
private @Length(max = 4000) String richAnswer = "";

@Builder.Default
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(name = "answer_files",
           joinColumns = @JoinColumn(name = "answer_id"),
           inverseJoinColumns = @JoinColumn(name = "files_id"))
private @NotNull Set<File> files = new LinkedHashSet<>();

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "participant_id", nullable = false)
private @NotNull User participant;

@ManyToOne(fetch = FetchType.LAZY, optional = true)
@JoinColumn(name = "expert_id", nullable = true)
private User expert;

@Column(name = "score")
@Builder.Default
private @PositiveOrZero Float score = 0f;
}
