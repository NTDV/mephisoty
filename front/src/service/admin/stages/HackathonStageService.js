import fetchApi from '@/main';

export class HackathonStageService {
  getAll(lazyParams) {
    return fetchApi('/admin/stages/hackathon/', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }
}
