package ru.valkovets.mephisoty.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;

import java.util.Set;

@NoRepositoryBean
public interface BasicRepository<T extends BasicEntity> extends JpaRepository<T, Long> {
}