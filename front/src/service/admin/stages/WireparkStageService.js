import fetchApi from '@/main';

export class WireparkStageService {

  getAll(lazyParams) {
    return fetchApi('/admin/stages/wirepark/', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  update(userId, dateId) {
    return fetchApi('/admin/stages/wirepark/' + userId + '/' + dateId)
      .then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }
}
