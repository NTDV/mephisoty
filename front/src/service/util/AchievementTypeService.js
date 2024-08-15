export class AchievementTypeService {
  content = ['Интегральная', 'Учебная', 'Научная', 'Общественная', 'Спортивная', 'Творческая'];
  severity = ['contrast', 'success', 'danger', 'warning', 'secondary', 'info'];

  getBadgeContentAll() {
    return this.content
      .map((value, index) => ({ label: value, value: index }));
  }

  getBadgeContentWithoutAll() {
    return this.content
      .filter((v, i) => i !== 0)
      .map((v, i) => ({ label: v, value: i }));
  }

  getBadgeSeverityFor(state) {
    return this.severity[state];
  }

  getBadgeContentFor(state) {
    return this.content[state];
  }
}
