package ru.valkovets.mephisoty.db.model.superclass;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TdrseEntity extends TdrEntity {
@NotNull
@Column(name = "start_at")
private OffsetDateTime start;

@NotNull
@Column(name = "end_at")
private OffsetDateTime end;
}
