package ru.valkovets.mephisoty.db.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;

@Component
public class UtilsRepository {
@PersistenceContext
private EntityManager entityManager;

@Transactional
public <Entity> List<Entity> findAllByNaturalIdIn(final SequencedCollection<?> ids, final Class<Entity> type) {
  if (ids instanceof final List<?> list) {
    return entityManager
        .unwrap(Session.class)
        .byMultipleNaturalId(type)
        .enableOrderedReturn(true)
        .multiLoad(list);
  }

  return findAllByNaturalIdIn(ids.toArray(), type, true);
}

@Transactional
public <Entity> List<Entity> findAllByNaturalIdIn(final Object[] ids, final Class<Entity> type, final boolean ordered) {
  return entityManager
      .unwrap(Session.class)
      .byMultipleNaturalId(type)
      .enableOrderedReturn(ordered)
      .multiLoad(ids);
}

@Transactional
public <Entity> List<Entity> findAllByNaturalIdIn(final Collection<?> ids, final Class<Entity> type) {
  return findAllByNaturalIdIn(ids.toArray(), type, false);
}

@Transactional
public <Entity> List<Entity> findAllByIdIn(final SequencedCollection<Long> ids, final Class<Entity> type) {
  return findAllByIdIn(ids.toArray(Long[]::new), type, true);
}

@Transactional
public <Entity> List<Entity> findAllByIdIn(final Long[] ids, final Class<Entity> type, final boolean ordered) {
  return entityManager
      .unwrap(Session.class)
      .byMultipleIds(type)
      .enableOrderedReturn(ordered)
      .multiLoad(ids);
}

@Transactional
public <Entity> List<Entity> findAllByIdIn(final Collection<Long> ids, final Class<Entity> type) {
  return findAllByIdIn(ids.toArray(Long[]::new), type, false);
}
}
