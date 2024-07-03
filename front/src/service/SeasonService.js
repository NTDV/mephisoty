import fetchApi from '@/main';

export class SeasonService {
    async getSeason(id) {
        return await fetchApi('/admin/season/' + id).then((resp) => resp.json());
    }

    async createSeason(seasonDto) {
        return fetchApi('/admin/season', {
            method: 'POST',
            body: JSON.stringify(seasonDto)
        }).then((resp) => resp.json());
    }

    getSeasons(lazyParams) {
        return fetchApi('/admin/season/', {
            method: 'POST',
            body: JSON.stringify(lazyParams)
        }).then((resp) => resp.json());
    }

    async editSeason(id, seasonDto) {
        return await fetchApi('/admin/season/' + id, {
            method: 'PUT',
            body: JSON.stringify(seasonDto)
        }).then((resp) => resp.json());
    }

    async deleteSeason(id) {
        return await fetchApi('/admin/season/' + id, {
            method: 'DELETE'
        }).then((resp) => resp.json());
    }

    getProductsSmall() {
        return fetch('/demo/data/products-small.json', { headers: { 'Cache-Control': 'no-cache' } })
            .then((res) => res.json())
            .then((d) => d.data);
    }

    getProducts() {
        return fetch('/demo/data/products.json', { headers: { 'Cache-Control': 'no-cache' } })
            .then((res) => res.json())
            .then((d) => d.data);
    }

    getProductsMixed() {
        return fetch('/demo/data/products-mixed.json', { headers: { 'Cache-Control': 'no-cache' } })
            .then((res) => res.json())
            .then((d) => d.data);
    }

    getProductsWithOrdersSmall() {
        return fetch('/demo/data/products-orders-small.json', { headers: { 'Cache-Control': 'no-cache' } })
            .then((res) => res.json())
            .then((d) => d.data);
    }

    getProductsWithOrdersLarge() {
        return fetch('/demo/data/products-orders.json', { headers: { 'Cache-Control': 'no-cache' } })
            .then((res) => res.json())
            .then((d) => d.data);
    }
}
