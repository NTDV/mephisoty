package ru.valkovets.mephisoty.db.model.userdata;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@Builder.Default
@Column(name = "email", unique = true, length = 100)
private String email = "";

@Length(max = 1024)
@NotBlank
@Nullable
@Column(name = "password", length = 1024) // todo set length
private String password;

@Length(max = 200)
@Nullable
@Column(name = "mephi_login", unique = true, length = 200)
private String mephi_login;

@Nullable
@Column(name = "mephi_is_student")
private Boolean mephi_isStudent;

@NotNull
@OneToOne(fetch = FetchType.LAZY, mappedBy = "credentials", optional = false, cascade = CascadeType.PERSIST)
@LazyToOne(LazyToOneOption.NO_PROXY)
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

public static Credentials from(final SignUpRequest dto, final PasswordEncoder passwordEncoder) {
    return Credentials.builder()
                      .comment(dto.comment())
                      .email(dto.email())
                      .password(passwordEncoder.encode(dto.password()))
                      .user(User.from(dto.user()))
                      .role(dto.role())
                      .build();
}
}
