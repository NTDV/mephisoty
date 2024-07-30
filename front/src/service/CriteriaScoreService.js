import fetchApi from '@/main';

export class CriteriaScoreService {
  getAll(lazyParams, parentId) {
    return fetchApi('/admin/criteriascore/' + (parentId ? parentId : '0'), {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  setScore(criteriaId, expertId, participantId, scoreDto) {
    return fetchApi('/admin/criteriascore/' + criteriaId + '/' + expertId + '/' + participantId, {
      method: 'POST',
      body: JSON.stringify(scoreDto)
    })
      .then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }

  delete(criteriaId, expertId, participantId) {
    return fetchApi('/admin/criteriascore/' + criteriaId + '/' + expertId + '/' + participantId, {
      method: 'DELETE'
    }).then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }

  deleteById(scoreId) {
    return fetchApi('/admin/criteriascore/' + scoreId, {
      method: 'DELETE'
    }).then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }
}
