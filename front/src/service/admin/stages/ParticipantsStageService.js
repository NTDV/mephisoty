import fetchApi from '@/main';

export class ParticipantsStageService {
  getAll(lazyParams) {
    return fetchApi('/admin/stages/participants/', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }
}
