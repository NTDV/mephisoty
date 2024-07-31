export class AllowStateService {
    defaultState = 'DISALLOW_ALL_FOR_PARTICIPANTS';

    mapContent = {
        DISALLOW_ALL_FOR_PARTICIPANTS: 'Скрыто',
        ALLOW_READ_FOR_PARTICIPANTS: 'Видно',
        ALLOW_CREATE_READ_FOR_PARTICIPANTS: 'Разрешено создание',
        ALLOW_CREATE_EDIT_READ_FOR_PARTICIPANTS: 'Разрешено создание и изменение'
    };

    mapSeverity = {
        DISALLOW_ALL_FOR_PARTICIPANTS: 'success',
        ALLOW_READ_FOR_PARTICIPANTS: 'info',
        ALLOW_CREATE_READ_FOR_PARTICIPANTS: 'warning',
        ALLOW_CREATE_EDIT_READ_FOR_PARTICIPANTS: 'danger'
    };

    getBadgeConentAll() {
        return Object.entries(this.mapContent).map(([key, value]) => ({ label: value, value: key }));
    }

    getBadgeContentViewOnly() {
        return Object.entries(this.mapContent)
            .filter(([key, value]) => key === 'DISALLOW_ALL_FOR_PARTICIPANTS' || key === 'ALLOW_READ_FOR_PARTICIPANTS')
            .map(([key, value]) => ({ label: value, value: key }));
    }

    getBadgeSeverityFor(state) {
        return this.mapSeverity[state];
    }

    getBadgeContentFor(state) {
        return this.mapContent[state];
    }
}
