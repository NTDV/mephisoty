import fetchApi from '@/main';

export class StageCriteriasScoreService {
  getAll(lazyParams, parentId) {
    return fetchApi('/expert/stagecriteriascore/' + (parentId ? parentId : '0'), {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  setScore(criteriaId, participantId, scoreDto) {
    return fetchApi('/expert/stagecriteriascore/' + criteriaId + '/' + participantId, {
      method: 'POST',
      body: JSON.stringify(scoreDto)
    })
      .then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }

  delete(criteriaId, participantId) {
    return fetchApi('/expert/stagecriteriascore/' + criteriaId + '/' + participantId, {
      method: 'DELETE'
    }).then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }
}
