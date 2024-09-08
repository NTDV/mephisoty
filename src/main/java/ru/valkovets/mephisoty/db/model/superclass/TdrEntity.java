package ru.valkovets.mephisoty.db.model.superclass;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TdrEntity extends BasicEntity {

@Builder.Default
@Column(name = "title", nullable = false, length = 120)
private @NotNull
@Length(max = 120) String title = "";

@Builder.Default
@Column(name = "description", nullable = false, length = 50000)
private @NotNull
@Length(max = 50000) String description = "";

@Builder.Default
@Column(name = "rules", nullable = false, length = 50000)
private @NotNull
@Length(max = 50000) String rules = "";
}
