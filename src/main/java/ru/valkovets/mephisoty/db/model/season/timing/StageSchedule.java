package ru.valkovets.mephisoty.db.model.season.timing;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.AllowState;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stage_schedule")
public class StageSchedule {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private User createdBy;
private OffsetDateTime createdAt;
private User editedBy;
private OffsetDateTime editedAt;
private String comment;

private Stage stage;
private OffsetDateTime start;
private OffsetDateTime end;
private int participantsMax;
private Set<User> experts;
private AllowState state;
}
