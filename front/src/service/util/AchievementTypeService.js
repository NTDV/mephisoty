export class AchievementTypeService {
  content = ['Итог', 'Учеба', 'Наука', 'Общество', 'Спорт', 'Творчество'];
  severity = ['contrast', 'success', 'danger', 'warning', 'secondary', 'info'];

  getBadgeContentAll() {
    return this.content
      .map((value, index) => ({ label: value, value: index }));
  }

  getBadgeContentWithoutAll() {
    return this.content
      .map((v, i) => ({ label: v, value: i }))
      .filter((v, i) => i !== 0);
  }

  getBadgeSeverityFor(state) {
    return this.severity[state];
  }

  getBadgeContentFor(state) {
    return this.content[state];
  }
}
