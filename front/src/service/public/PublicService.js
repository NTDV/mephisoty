import fetchApi from '@/main';

export class PublicService {
  applyHackathon(request) {
    return fetchApi('/public/applyhackathon', {
      method: 'POST',
      body: JSON.stringify(request)
    }).then((resp) => resp.text())
      .then(data => data === '')
      .catch(e => false);
  }
}
