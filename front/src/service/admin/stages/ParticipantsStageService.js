import fetchApi from '@/main';

export class ParticipantsStageService {
  getAll(lazyParams, stageId) {
    return fetchApi('/admin/stages/participants/' + (stageId ? stageId : '0'), {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }
}
