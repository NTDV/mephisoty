import fetchApi from '@/main';

export class StageScoreService {
  getAll(lazyParams, stageId) {
    return fetchApi('/admin/stagescore/' + (stageId ? stageId : '0'), {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  evaluate(stageId) {
    return fetchApi('/admin/stagescore/' + stageId)
      .then((resp) => resp.text() === '');
  }
}
