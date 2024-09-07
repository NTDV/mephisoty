export class ParticipantStateService {
  defaultState = 'PARTICIPANT';

  mapContent = {
    NOT_PARTICIPANT: 'Не участник',
    BANNED: 'Заблокирован',
    DISQUALIFIED: 'Дисквалифицирован',

    PARTICIPANT: 'Участник конкурса',
    FINAL_MEMBER: 'Участник суперфинала',
    PRIZE_WINNER: 'Лауреат конкурса',
    WINNER: 'Победитель конкурса'
  };

  values = Object.entries(this.mapContent).map(([key, value]) => ({ label: value, value: key }));

  getContentAll() {
    return this.values;
  }

  getContentFor(state) {
    return this.mapContent[state];
  }
}
