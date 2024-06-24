package ru.valkovets.mephisoty.db.model.superclass;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

@Length(max = 120)
@NotBlank
@Column(name = "title", nullable = false, length = 120)
private String title;

@Length(max = 2000)
@Column(name = "description", nullable = false, length = 2000)
@NotNull
private String description = "";

@Length(max = 2000)
@NotNull
@Column(name = "rules", nullable = false, length = 2000)
private String rules = "";
}
