import fetchApi from '@/main';

export class SeasonStagesScoreService {
  getAll(lazyParams, parentId) {
    return fetchApi('/admin/seasonstagescore/' + (parentId ? parentId : '0'), {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  setScore(stageId, participantId, scoreDto) {
    return fetchApi('/admin/seasonstagescore/' + stageId + '/' + participantId, {
      method: 'POST',
      body: JSON.stringify(scoreDto)
    })
      .then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }

  delete(stageId, participantId) {
    return fetchApi('/admin/seasonstagescore/' + stageId + '/' + participantId, {
      method: 'DELETE'
    }).then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }
}
