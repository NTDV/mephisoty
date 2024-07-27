import fetchApi from '@/main';

export class CriteriaService {
  getAll(lazyParams, stageId) {
    return fetchApi('/admin/criteria/' + (stageId ? stageId : '0'), {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  get(id) {
    return fetchApi('/admin/criteria/' + id).then((resp) => resp.json());
  }

  edit(id, criteriaDto) {
    return fetchApi('/admin/criteria/' + id, {
      method: 'PUT',
      body: JSON.stringify(criteriaDto)
    }).then((resp) => resp.json());
  }

  delete(id) {
    return fetchApi('/admin/criteria/' + id, {
      method: 'DELETE'
    }).then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }

  bind(stageId, criteriaId) {
    return fetchApi('/admin/criteria/' + criteriaId + '/stage/' + stageId, {
      method: 'PUT'
    }).then((resp) => resp.json());
  }

  getAllForSelect(lazyParams) {
    return fetchApi('/admin/criteria/select', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }
}
