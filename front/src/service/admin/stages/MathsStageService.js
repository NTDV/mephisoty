import fetchApi from '@/main';

export class MathsStageService {
  getAll(lazyParams) {
    return fetchApi('/admin/stages/maths/', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }
}
