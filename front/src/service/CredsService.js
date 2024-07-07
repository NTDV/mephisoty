import fetchApi from '@/main';

export class CredsService {
    async getFullName(id) {
        if (!id) return new Promise(() => {
            return {id: 0, name: "Administrator"};
        });
        return await fetchApi('/creds/' + id + '/name').then((resp) => resp.json());
    }
}
