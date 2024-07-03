package ru.valkovets.mephisoty.db.repository.season;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Set;

@Repository
public interface SeasonRepository extends BasicRepository<Season>, JpaSpecificationExecutor<Season> {
    @Query("select season.stages from Season season where season.id = ?1")
    <T> Set<T> getSeasonStagesById(Long id, final Class<T> type);

    @Query("select season.seasonScores from Season season where season.id = ?1")
    <T> Set<T> getSeasonScoresById(Long id, final Class<T> type);

    @EntityGraph("season_with_stages")
    Season getById(Long id);
}