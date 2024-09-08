import fetchApi from '@/main';

export class MeService {
  getMe() {
    return fetchApi('/participant/me')
      .then((resp) => resp.json());
  }

  applyto(stageId) {
    return fetchApi('/participant/applyto/' + stageId)
      .then((resp) => resp.text())
      .then(data => data === '')
      .catch(e => false);
  }

  updateMe(participantMeDto) {
    return fetchApi('/participant/me', {
      method: 'PUT',
      body: JSON.stringify(participantMeDto)
    }).then((resp) => resp.text())
      .then(data => data === '')
      .catch(e => false);
  }

  uploadVideo(videoUploadDto) {
    return fetchApi('/participant/video/upload', {
      method: 'POST',
      body: JSON.stringify(videoUploadDto)
    }).then((resp) => resp.text())
      .then(data => data === '')
      .catch(e => false);
  }

  chooseDictantDate(dateId) {
    return fetchApi('/participant/dictant/' + dateId)
      .then((resp) => resp.text())
      .then(data => data === '')
      .catch(e => false);
  }

  chooseWireparkDate(dateId) {
    return fetchApi('/participant/wirepark/' + dateId)
      .then((resp) => resp.text())
      .then(data => data === '')
      .catch(e => false);
  }

  getWireparkDates() {
    return fetchApi('/participant/wirepark/dates')
      .then((resp) => resp.json());
  }
}
