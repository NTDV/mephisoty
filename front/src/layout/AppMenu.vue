<script setup>
import { ref } from 'vue';

import AppMenuItem from './AppMenuItem.vue';

const model = ref([
  {
    label: 'Home',
    items: [{ label: 'Главная', icon: 'pi pi-fw pi-home', to: '/' }]
  },
  {
    label: 'Управление',
    items: [
      { label: 'Достижения', icon: 'pi pi-fw pi-hashtag', to: '/admin/achievements' },
      {label: 'Сезоны', icon: 'pi pi-fw pi-hashtag', to: '/admin/seasons'},
      { label: 'Этапы', icon: 'pi pi-fw pi-check-square', to: '/admin/stages' },
      {label: 'Критерии', icon: 'pi pi-fw pi-table', to: '/admin/criterias'},
      {label: 'Баллы критерия', icon: 'pi pi-fw pi-hashtag', to: '/admin/criteriascores'},
      {label: 'Баллы за этапы сезона', icon: 'pi pi-fw pi-hashtag', to: '/admin/seasonstagesscores'},
    ]
  },
  {
    label: 'Экспертиза',
    items: [
      {label: 'Баллы по критериям', icon: 'pi pi-fw pi-hashtag', to: '/expert/stagecriteriasscores'},
    ]
  }
]);
</script>

<template>
  <ul class="layout-menu">
    <template v-for="(item, i) in model" :key="item">
      <app-menu-item v-if="!item.separator" :index="i" :item="item"></app-menu-item>
      <li v-if="item.separator" class="menu-separator"></li>
    </template>
  </ul>
</template>

<style lang="scss" scoped></style>
