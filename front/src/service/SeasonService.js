import fetchApi from '@/main';

export class SeasonService {
    getAll(lazyParams) {
        return fetchApi('/admin/season/', {
            method: 'POST',
            body: JSON.stringify(lazyParams)
        }).then((resp) => resp.json());
    }

    getAllForSelect(lazyParams) {
        return fetchApi('/admin/season/select',{
            method: 'POST',
            body: JSON.stringify(lazyParams)
        }).then((resp) => resp.json());
    }

    get(id) {
        return fetchApi('/admin/season/' + id).then((resp) => resp.json());
    }

    create(seasonDto) {
        return fetchApi('/admin/season', {
            method: 'POST',
            body: JSON.stringify(seasonDto)
        }).then((resp) => resp.json());
    }

    edit(id, seasonDto) {
        return fetchApi('/admin/season/' + id, {
            method: 'PUT',
            body: JSON.stringify(seasonDto)
        }).then((resp) => resp.json());
    }

    delete(id) {
        return fetchApi('/admin/season/' + id, {
            method: 'DELETE'
        }).then((resp) => resp.json());
    }

    getStages(seasonId) {
        return fetchApi('/admin/season/' + seasonId + '/stages').then((resp) => resp.json());
    }

    addStage(seasonId, stageDto) {
        return fetchApi('/admin/season/' + seasonId + '/stages', {
            method: 'POST',
            body: JSON.stringify(stageDto)
        }).then((resp) => resp.json());
    }

    getScores(seasonId) {
        return fetchApi('/admin/season/' + seasonId + '/scores').then((resp) => resp.json());
    }
}
