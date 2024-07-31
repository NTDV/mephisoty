import fetchApi from '@/main';

export class StageExpertService {
  getAllForSelect(lazyParams) {
    return fetchApi('/expert/stage/select', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }
}
