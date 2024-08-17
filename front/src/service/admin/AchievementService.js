import fetchApi from '@/main';

export class AchievementService {
  getAll(lazyParams, stageId, participantId) {
    return fetchApi('/admin/achievement/' + stageId + '/' + participantId, {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  importNew(file, delimiter, hasHeader, stageId) {
    const form = new FormData();
    form.append('file', file);
    form.append('delimiter', delimiter);
    form.append('hasHeader', hasHeader);

    return fetchApi('/admin/achievement/import/' + stageId, form, true)
      .then((resp) => resp.data);
  }

  edit(id, achievementDto) {
    return fetchApi('/admin/achievement/' + id, {
      method: 'PUT',
      body: JSON.stringify(achievementDto)
    }).then((resp) => resp.json());
  }

  delete(id) {
    return fetchApi('/admin/achievement/' + id, {
      method: 'DELETE'
    }).then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }
}
