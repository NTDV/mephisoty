<script setup>
import { computed, onBeforeMount, onBeforeUnmount, onMounted, ref } from 'vue';
import { useLayout } from '@/layout/composables/layout';
import { useRouter } from 'vue-router';
import { getCurrentUser } from '@/service/util/UtilsService';
import AppTitle from '@/components/prefab/AppTitle.vue';

const { layoutConfig, onMenuToggle } = useLayout();

const outsideClickListener = ref(null);
const topbarMenuActive = ref(false);
const router = useRouter();
const user = ref(null);

onBeforeMount(() => {
  user.value = getCurrentUser();
});

onMounted(() => {
  bindOutsideClickListener();
});

onBeforeUnmount(() => {
  unbindOutsideClickListener();
});
computed(() => {
  return `/layout/images/${layoutConfig.darkTheme.value ? 'logo-white' : 'logo-dark'}.svg`;
});
const onTopBarMenuButton = () => {
  topbarMenuActive.value = !topbarMenuActive.value;
};
const onLogoutClick = () => {
  topbarMenuActive.value = false;
  router.push('/logout');
};
const topbarMenuClasses = computed(() => {
  return {
    'layout-topbar-menu-mobile-active': topbarMenuActive.value
  };
});

const bindOutsideClickListener = () => {
  if (!outsideClickListener.value) {
    outsideClickListener.value = (event) => {
      if (isOutsideClicked(event)) {
        topbarMenuActive.value = false;
      }
    };
    document.addEventListener('click', outsideClickListener.value);
  }
};
const unbindOutsideClickListener = () => {
  if (outsideClickListener.value) {
    document.removeEventListener('click', outsideClickListener);
    outsideClickListener.value = null;
  }
};
const isOutsideClicked = (event) => {
  if (!topbarMenuActive.value) return;

  const sidebarEl = document.querySelector('.layout-topbar-menu');
  const topbarEl = document.querySelector('.layout-topbar-menu-button');

  return !(sidebarEl.isSameNode(event.target) || sidebarEl.contains(event.target) || topbarEl.isSameNode(event.target) || topbarEl.contains(event.target));
};
</script>

<template>
  <div class="layout-topbar">
    <div class="flex">
      <AppTitle class="text-4xl text-900 layout-topbar-logo" />

      <button class="p-link layout-menu-button layout-topbar-button mt-2" @click="onMenuToggle()">
        <i class="pi pi-bars"></i>
      </button>
    </div>

    <div v-if="user" class="text-overflow-ellipsis overflow-hidden align-items-center flex">
      <span class="text-900 space-font md:inline-block text-xl md:text-2xl lg:text-3xl xl:text-4xl font-medium
                 text-nowrap text-overflow-ellipsis overflow-hidden">{{ user.fullName }}</span>
      <button class="p-link layout-topbar-menu-button layout-topbar-button" @click="onTopBarMenuButton()">
        <i class="pi pi-user"></i>
      </button>
    </div>
    <div v-else>
      <a :href="'https://auth.mephi.ru/login?service=' + encodeURI(window.$frontHost)"
         class="text-900 space-font text-4xl font-medium text-nowrap">Войти</a>
    </div>

    <div :class="topbarMenuClasses" class="layout-topbar-menu">
      <button class="p-link layout-topbar-button" @click="onLogoutClick()">
        <i class="pi pi-sign-out"></i>
        <span>Выйти</span>
      </button>
    </div>
  </div>
</template>

<style lang="scss"></style>
