package ru.valkovets.mephisoty.db.repository.userdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.special.NamesProj;
import ru.valkovets.mephisoty.db.repository.BasicRepository;
import ru.valkovets.mephisoty.settings.ParticipantState;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends BasicRepository<User> {
@Query("select new ru.valkovets.mephisoty.db.projection.special.NamesProj(u.firstName, u.secondName, u.thirdName) from User u where u.credentials.id = ?1")
Optional<NamesProj> getFullNameByCredentialsId(Long credentialsId);

<T> Page<T> findByCredentials_RoleInOrderBySecondNameAscFirstNameAscThirdNameAsc(Collection<UserRole> roles, Pageable pageable,
                                                                                 Class<T> type);

<T> Page<T> findByCredentials_RoleInAndStateInOrderBySecondNameAscFirstNameAscThirdNameAsc(Collection<UserRole> roles,
                                                                                           Collection<ParticipantState> states,
                                                                                           Pageable pageable, Class<T> type);
}