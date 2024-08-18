package ru.valkovets.mephisoty.db.model.userdata;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.valkovets.mephisoty.api.dto.userdata.SignUpRequest;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "credentials")
@EntityListeners(AuditingEntityListener.class)
public class Credentials extends BasicEntity implements UserDetails {

@Length(max = 100)
@Nullable
@Email
@Column(name = "email", length = 100)
private String email;

@Length(max = 200)
@NotNull
@Column(name = "mephi_login", unique = true, length = 200)
private String mephiLogin;

@Nullable
@Column(name = "mephi_is_student")
private Boolean mephiIsStudent;

@NotNull
@OneToOne(fetch = FetchType.EAGER, mappedBy = "credentials", optional = false, cascade = CascadeType.PERSIST)
private User user;

@NotNull
@Enumerated(EnumType.STRING)
@Column(name = "role", nullable = false)
private UserRole role;

@Override
@Transient
public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(role);
}

public static Credentials from(final SignUpRequest dto) {
    return Credentials.builder()
                      .comment(dto.comment())
                      .email(dto.email())
                      .user(User.from(dto.user()))
                      .role(dto.role())
                      .build();
}

@Override
public String getPassword() {
    throw new UnsupportedOperationException("Not supported");
}

@Override
@Transient
public boolean isAccountNonLocked() {
    return role != UserRole.BANNED;
}

@Override
@Transient
public boolean isEnabled() {
    return role != UserRole.DISABLED;
}

@Transient
public static Credentials getCurrent() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) return null;

    final Object principal = authentication.getPrincipal();
    if (principal instanceof final Credentials credentials) {
        return credentials;
    } else {
        return null;
    }
}

@Override
@Transient
public String getUsername() {
    return mephiLogin;
}
}
