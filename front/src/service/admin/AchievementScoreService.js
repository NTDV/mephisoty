import fetchApi from '@/main';

export class AchievementScoreService {
  getFor(stageId, participantId) {
    return fetchApi('/admin/achievementscore/' + stageId + '/' + participantId)
      .then((resp) => resp.json());
  }

  setExpertScoreFor(stageId, participantId, scoreDto) {
    return fetchApi('/admin/achievementscore/' + stageId + '/' + participantId, {
      method: 'POST',
      body: JSON.stringify(scoreDto)
    })
      .then((resp) => resp.text())
      .then((text) => text == '' ? true : JSON.parse(text));
  }
}
