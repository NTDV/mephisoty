import fetchApi from '@/main';

export class ParticipantsService {
  getAllForSelect(lazyParams) {
    return fetchApi('/admin/user/select/participant', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }
}
