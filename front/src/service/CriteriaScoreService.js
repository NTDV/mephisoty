import fetchApi from '@/main';

export class CriteriaScoreService {
  getAll(lazyParams, parentId) {
    return fetchApi('/admin/criteriascore/' + (parentId ? parentId : '0'), {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  setScore(criteriaId, expertId, participantId, score) {
    return fetchApi('/admin/criteriascore/' + criteriaId + '/' + expertId + '/' + participantId + '?score=' + score,)
      .then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }

  delete(criteriaId, expertId, participantId) {
    return fetchApi('/admin/criteriascore/' + criteriaId + '/' + expertId + '/' + participantId, {
      method: 'DELETE'
    }).then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }
}
