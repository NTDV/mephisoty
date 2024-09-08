import fetchApi from '@/main';

export class StageService {
  getAll(lazyParams, seasonId) {
    return fetchApi('/admin/stage/' + (seasonId ? seasonId : '0'), {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  get(id) {
    return fetchApi('/admin/stage/' + id).then((resp) => resp.json());
  }

  edit(id, stageDto) {
    return fetchApi('/admin/stage/' + id, {
      method: 'PUT',
      body: JSON.stringify(stageDto)
    }).then((resp) => resp.json());
  }

  delete(id) {
    return fetchApi('/admin/stage/' + id, {
      method: 'DELETE'
    }).then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }

  bindStage(seasonId, stageId) {
    return fetchApi('/admin/stage/' + stageId + '/season/' + seasonId, {
      method: 'PUT'
    }).then((resp) => resp.json());
  }

  getAllForSelect(lazyParams) {
    return fetchApi('/admin/stage/select', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  addCriteria(id, criteria) {
    return fetchApi('/admin/stage/' + id + '/criterias', {
      method: 'POST',
      body: JSON.stringify(criteria)
    }).then((resp) => resp.json());
  }

  addFile(stageId, fileId) {
    return fetchApi('/admin/stage/' + stageId + '/files/' + fileId, {
      method: 'PUT'
    }).then((resp) => resp.json());
  }

  deleteFile(stageId, fileId) {
    return fetchApi('/admin/stage/' + stageId + '/files/' + fileId, {
      method: 'DELETE'
    }).then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }

  getFiles(stageId) {
    return fetchApi('/public/stages/' + stageId + '/files')
      .then((resp) => resp.json());
  }

  getAllPublic() {
    return fetchApi('/public/stages')
      .then((resp) => resp.json());
  }

  addSchedule(id, schedule) {
    return fetchApi('/admin/stage/' + id + '/schedule', {
      method: 'POST',
      body: JSON.stringify(schedule)
    }).then((resp) => resp.json());
  }
}
