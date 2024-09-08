package ru.valkovets.mephisoty.db.model.superclass;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TdrseEntity extends TdrEntity {
@Builder.Default
@Column(name = "start_at")
private @NotNull OffsetDateTime start = OffsetDateTime.now();

@Builder.Default
@Column(name = "end_at")
private @NotNull OffsetDateTime end = OffsetDateTime.now();
}
