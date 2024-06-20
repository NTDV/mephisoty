package ru.valkovets.mephisoty.db.model.files;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ru.valkovets.mephisoty.db.model.userdata.User;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file")
public class File {
@Id
@GeneratedValue(generator = "org.hibernate.id.UUIDGenerator")
@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
@Column(name = "id", updatable = false, nullable = false)
private UUID id;

private User createdBy;
private OffsetDateTime createdAt;

private String originalName;

@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
private User owner;

//@ElementCollection
//private Set<FileAccessPolicy> accessPolicy;

// todo сделать норм
public File(final String originalName, final User owner) {
    this.originalName = originalName;
    this.owner = owner;
}
}
