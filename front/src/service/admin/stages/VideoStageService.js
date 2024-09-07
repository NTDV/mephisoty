import fetchApi from '@/main';

export class VideoStageService {

  getAll(lazyParams) {
    return fetchApi('/admin/stages/video/', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  update(answerId, dto) {
    return fetchApi('/admin/stages/video/' + answerId, {
      method: 'POST',
      body: JSON.stringify(dto)
    })
      .then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }
}
