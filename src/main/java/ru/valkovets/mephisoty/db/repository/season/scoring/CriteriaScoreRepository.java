package ru.valkovets.mephisoty.db.repository.season.scoring;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CriteriaScoreRepository extends BasicRepository<CriteriaScore> {
<T> Set<T> getAllByParticipant_IdInAndCriteria_Id(Collection<Long> participantIds, Long criteriaId, Class<T> type);

<T> Set<T> getAllByExpertIdAndParticipant_IdInAndCriteria_IdIn(Long expertId, Collection<Long> participantIds,
                                                               Collection<Long> criteriaId, Class<T> type);

Optional<CriteriaScore> findByCriteria_IdAndExpert_IdAndParticipant_Id(Long criteriaId, Long expertId, Long participantId);

void deleteByCriteria_IdAndExpert_IdAndParticipant_Id(Long criteriaId, Long expertId, Long participantId);
}