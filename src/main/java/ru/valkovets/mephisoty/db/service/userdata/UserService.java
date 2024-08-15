package ru.valkovets.mephisoty.db.service.userdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.lazydata.OffsetBasedPageRequest;
import ru.valkovets.mephisoty.db.model.userdata.Group_;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.model.userdata.User_;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.simple.UserSelectProj;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
private final UserRepository userRepository;

public Page<IdTitleProj> getAllForSelect(final Specification<User> specification, final long page, final long size) {
  return userRepository
      .findBy(Specification.where(specification),
              q -> q.sortBy(Sort.by(Sort.Direction.ASC, User_.GROUP + "." + Group_.TITLE, User_.SECOND_NAME, User_.FIRST_NAME,
                                    User_.THIRD_NAME))
                    .as(UserSelectProj.class)
                    .page(new OffsetBasedPageRequest(page, size)))
      .map(IdTitleFromUser::new);
}

@SuppressWarnings("LombokGetterMayBeUsed")
private record IdTitleFromUser(@Getter Long id, @Getter String title) implements IdTitleProj {
  private static final IdTitleProj EMPTY = new IdTitleFromUser(0L, "");

  public IdTitleFromUser(final UserSelectProj user) {
    this(user.getId(),
         (Objects.requireNonNullElse(user.getGroup(), EMPTY).getTitle() + " " +
          user.getSecondName() + " " + user.getFirstName() + " " + user.getThirdName()).trim());
  }
}
}
