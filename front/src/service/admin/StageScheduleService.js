import fetchApi from '@/main';

export class StageScheduleService {
  getAll(lazyParams, stageId) {
    return fetchApi('/admin/stageschedule/' + (stageId ? stageId : '0'), {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  get(id) {
    return fetchApi('/admin/stageschedule/' + id).then((resp) => resp.json());
  }

  edit(id, stagescheduleDto) {
    return fetchApi('/admin/stageschedule/' + id, {
      method: 'PUT',
      body: JSON.stringify(stagescheduleDto)
    }).then((resp) => resp.json());
  }

  delete(id) {
    return fetchApi('/admin/stageschedule/' + id, {
      method: 'DELETE'
    }).then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }

  bind(stageId, stagescheduleId) {
    return fetchApi('/admin/stageschedule/' + stagescheduleId + '/stage/' + stageId, {
      method: 'PUT'
    }).then((resp) => resp.json());
  }
}
