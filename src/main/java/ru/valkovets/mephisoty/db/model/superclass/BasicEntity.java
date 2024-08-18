package ru.valkovets.mephisoty.db.model.superclass;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BasicEntity implements Serializable {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
@Column(name = "id", nullable = false)
@Positive
private Long id;

@Basic(fetch = FetchType.LAZY)
@PastOrPresent
@NotNull
@Builder.Default
@Column(name = "created_at", nullable = false, updatable = false)
@CreatedDate
private Instant createdAt = Instant.now();

@Basic(fetch = FetchType.LAZY)
@PastOrPresent
@Nullable
@Column(name = "modified_at")
//@LastModifiedDate
private Instant modifiedAt;

@Basic(fetch = FetchType.LAZY)
//@NotNull
@Builder.Default
@Column(name = "created_by", updatable = false)
@CreatedBy
private Long createdBy = 0L;

@Basic(fetch = FetchType.LAZY)
@Column(name = "modified_by")
@Nullable
//@LastModifiedBy
private Long modifiedBy;

@NotNull
@Length(max = 200)
@Builder.Default
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

@PreUpdate
public void updateModified() {
    final Credentials user = Credentials.getCurrent();

    if (user != null) modifiedBy = user.getId();
    else {
        modifiedBy = 0L;
    }
    modifiedAt = Instant.now();
}
}
