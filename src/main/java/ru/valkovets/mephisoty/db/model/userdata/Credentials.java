package ru.valkovets.mephisoty.db.model.userdata;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
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
@OneToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User user; // credentials is parent, user is child

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
}
