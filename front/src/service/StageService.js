import fetchApi from '@/main';

export class StageService {
    getAll(lazyParams, seasonId) {
        return fetchApi('/admin/stage/' + (seasonId ? seasonId : '0'), {
            method: 'POST',
            body: JSON.stringify(lazyParams)
        }).then((resp) => resp.json());
    }

    get(id) {
        return fetchApi('/admin/stage/' + id).then((resp) => resp.json());
    }

    edit(id, stageDto) {
        return fetchApi('/admin/stage/' + id, {
            method: 'PUT',
            body: JSON.stringify(stageDto)
        }).then((resp) => resp.json());
    }

    delete(id) {
        return fetchApi('/admin/stage/' + id, {
            method: 'DELETE'
        }).then((resp) => resp.text())
          .then((text) => text == '' ? true : JSON.parse(text));
    }

    bindStage(seasonId, stageId) {
        return fetchApi('/admin/stage/' + stageId + '/season/' + seasonId, {
            method: 'PUT'
        }).then((resp) => resp.json());
    }

    getCriterias(id) {
        return fetchApi('/admin/stage/' + id + '/criterias').then((resp) => resp.json());
    }

    addCriteria(id, criteria) {
        return fetchApi('/admin/stage/' + id + '/criterias', {
            method: 'POST',
            body: JSON.stringify(criteria)
        }).then((resp) => resp.json());
    }

    getScores(id) {
        return fetchApi('/admin/stage/' + id + '/scores').then((resp) => resp.json());
    }

    getSchedules(id) {
        return fetchApi('/admin/stage/' + id + '/schedules').then((resp) => resp.json());
    }

    getQuestions(id) {
        return fetchApi('/admin/stage/' + id + '/questions').then((resp) => resp.json());
    }
}
