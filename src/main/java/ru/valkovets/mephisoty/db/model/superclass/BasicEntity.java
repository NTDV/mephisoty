package ru.valkovets.mephisoty.db.model.superclass;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BasicEntity {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
@Column(name = "id", nullable = false)
@Positive
private Long id;

@Basic(fetch = FetchType.LAZY)
@PastOrPresent
@NotNull
@Column(name = "created_at", nullable = false, updatable = false)
@CreatedDate
private Instant createdAt;

@Basic(fetch = FetchType.LAZY)
@PastOrPresent
@Column(name = "modified_at")
@LastModifiedDate
private Instant modifiedAt;

@Basic(fetch = FetchType.LAZY)
@Positive
//@NotNull
@Column(name = "created_by", updatable = false)
@CreatedBy
private Long createdBy;

@Basic(fetch = FetchType.LAZY)
@Positive
@Column(name = "modified_by")
@LastModifiedBy
private Long modifiedBy;

@Basic(fetch = FetchType.LAZY)
@NotNull
@Length(max = 200)
@Column(name = "comment", nullable = false, length = 200)
private String comment = "";

@Override
public final boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null) return false;
    
    final Class<?> oEffectiveClass = o instanceof HibernateProxy
                                     ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                                     : o.getClass();
    final Class<?> thisEffectiveClass = this instanceof HibernateProxy
                                        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                                        : getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;

    final BasicEntity that = (BasicEntity) o;
    return id != null && Objects.equals(id, that.id);
}

@Override
public final int hashCode() {
    return this instanceof HibernateProxy ?
           ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
           getClass().hashCode();
}
}
