import fetchApi from '@/main';

export class DictantStageService {

  getAll(lazyParams) {
    return fetchApi('/admin/stages/dictant/', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  update(userId, dateId) {
    return fetchApi('/admin/stages/dictant/' + userId + '/' + dateId)
      .then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }
}
