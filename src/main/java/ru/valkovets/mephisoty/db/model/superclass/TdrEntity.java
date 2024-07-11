package ru.valkovets.mephisoty.db.model.superclass;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TdrEntity extends BasicEntity {

@NotBlank
@Length(max = 120)
@Column(name = "title", nullable = false, length = 120)
private String title;

@NotNull
@Length(max = 50000)
@Builder.Default
@Column(name = "description", nullable = false, length = 50000)
private String description = "";

@NotNull
@Length(max = 50000)
@Builder.Default
@Column(name = "rules", nullable = false, length = 50000)
private String rules = "";
}
