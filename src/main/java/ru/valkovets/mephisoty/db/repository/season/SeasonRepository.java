package ru.valkovets.mephisoty.db.repository.season;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.List;
import java.util.Set;

@Repository
public interface SeasonRepository extends BasicRepository<Season> {
@Query("select s from Stage s where s.season.id = ?1")
    //@Query("select season.stages from Season season where season.id = ?1")
<T> Set<T> getSeasonStagesById(Long id, final Class<T> type);

@Query("select s from SeasonScore s where s.season.id = ?1")
    //@Query("select season.seasonScores from Season season where season.id = ?1")
<T> Set<T> getSeasonScoresById(Long id, final Class<T> type);

@EntityGraph("season_full")
<T> T getById(Long id, final Class<T> type);

<T> Page<T> getAllByOrderByTitleAscIdAsc(Specification<Season> specification, Pageable pageable, Class<T> type);
}