import fetchApi from '@/main';

export class WwwStageService {
  getAll(lazyParams) {
    return fetchApi('/admin/stages/www/', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }
}
