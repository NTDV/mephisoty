package ru.valkovets.mephisoty.db.model.userdata;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.valkovets.mephisoty.db.dto.userdata.CredentialsDto;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.security.credentials.PasswordManager;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

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
@NotBlank
@Email
@Column(name = "email", nullable = false, unique = true, length = 100)
private String email;

@Length(max = 1024)
@NotBlank
@Column(name = "password", nullable = false, length = 1024) // todo set length
private String password;

@NotNull
@OneToOne(fetch = FetchType.EAGER, mappedBy = "credentials", optional = false, cascade = CascadeType.PERSIST)
private User user;

@NotNull
@Enumerated
@Column(name = "role", nullable = false)
private UserRole role;

@Override
@Transient
public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(role);
}

@Override
@Transient
public String getUsername() {
    return email;
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
    return ((Credentials) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
}

public static Credentials from(final CredentialsDto dto, final PasswordEncoder passwordEncoder) {
    return Credentials.builder()
                      .comment(dto.comment())
                      .email(dto.email())
                      .password(passwordEncoder.encode(dto.password()))
                      .user(User.from(dto.user()))
                      .role(dto.role())
                      .build();
}
}
