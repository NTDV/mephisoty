package ru.valkovets.mephisoty.db.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;

@NoRepositoryBean
public interface BasicRepository<T extends BasicEntity>
    extends EntityGraphJpaSpecificationExecutor<T>,
            JpaRepository<T, Long> {
}