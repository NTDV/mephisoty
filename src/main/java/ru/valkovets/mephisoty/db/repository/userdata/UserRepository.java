package ru.valkovets.mephisoty.db.repository.userdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.special.user.NamesProj;
import ru.valkovets.mephisoty.db.repository.BasicRepository;
import ru.valkovets.mephisoty.settings.ParticipantState;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends BasicRepository<User> {
Long countAllByState(String state);

@Query(
    "select new ru.valkovets.mephisoty.db.projection.special.user.NamesProj(u.firstName, u.secondName, u.thirdName) from User u where u.credentials.id = ?1")
Optional<NamesProj> getFullNameByCredentialsId(Long credentialsId);

<T> Page<T> findByCredentials_RoleInOrderBySecondNameAscFirstNameAscThirdNameAsc(
    Collection<UserRole> roles, Pageable pageable, Class<T> type);

@Query(
    "select u from User u where (u.group is null or u.group.title in ?2) and trim(concat(u.secondName, ' ', u.firstName, ' ', u.thirdName)) in ?1")
<T> Set<T> findAllByFullNameAndGroup(Collection<String> fullName, Collection<String> groupTitle, Class<T> type);

Set<User> findAllByFullNameAndGroup_TitleAndCredentialsIsNull(String fullName, String groupTitle);

default <T> Page<T> findAllByStateOrderBySecondNameAscFirstNameAscThirdNameAsc(final ParticipantState state,
                                                                               final Pageable pageable, final Class<T> type) {
  return findAllByStateOrderBySecondNameAscFirstNameAscThirdNameAsc(state.name(), pageable, type);
}

<T> Page<T> findAllByStateOrderBySecondNameAscFirstNameAscThirdNameAsc(String state, Pageable pageable, Class<T> type);

default <T> Page<T> findAllByStateOrderByGroup_TitleAscSecondNameAscFirstNameAscThirdNameAsc(final ParticipantState state,
                                                                                             final Pageable pageable,
                                                                                             final Class<T> type) {
  return findAllByStateOrderByGroup_TitleAscSecondNameAscFirstNameAscThirdNameAsc(state.name(), pageable, type);
}

<T> Page<T> findAllByStateOrderByGroup_TitleAscSecondNameAscFirstNameAscThirdNameAsc(String state, Pageable pageable,
                                                                                     Class<T> type);

@Query("select u from User u where u.fullName = ?1 and u.credentials is null")
Set<User> findAllByFullNameAndWithoutCredentials(String fullName);
}