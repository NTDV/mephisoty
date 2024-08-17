import fetchApi from '@/main';

export class ParticipantsService {
  getAllForSelect(lazyParams) {
    return fetchApi('/admin/user/select/participant', {
      method: 'POST',
      body: JSON.stringify(lazyParams)
    }).then((resp) => resp.json());
  }

  createAchievement(participantId, stageId, achievementDto) {
    return fetchApi('/admin/user/' + participantId + '/stage/' + stageId + '/achievement', {
      method: 'POST',
      body: JSON.stringify(achievementDto)
    }).then((resp) => resp.json());
  }
}
