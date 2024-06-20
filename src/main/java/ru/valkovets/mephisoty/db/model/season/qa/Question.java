package ru.valkovets.mephisoty.db.model.season.qa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.AllowState;
import ru.valkovets.mephisoty.settings.FileType;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "question")
public class Question {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private User createdBy;
private OffsetDateTime createdAt;
private User editedBy;
private OffsetDateTime editedAt;
private String comment;

private Stage stage;

private String title;
private String descriptions;
private String rules;

private AllowState allowShortAnswer;
private AllowState allowRichAnswer;
private Set<FileType> allowFilesAnswer;
private int maxFiles;

private float maxScore;
private float minScore;
}
