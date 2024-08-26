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

const particlesOption = ref({
  fpsLimit: 24,
  fullScreen: false,
  detectRetina: true,
  particles: {
    color: { value: '#ffffff' },
    move: { direction: 'none', enable: true, outModes: 'bounce', random: true, speed: 0.3, straight: false },
    number: {
      density: { enable: true, area: 200 },
      value: 8000
    },
    opacity: {
      animation: { enable: true, speed: 0.05, startValue: 'random', sync: false, count: 250, destroy: 'none' },
      value: { min: 0, max: 0.3 }
    },
    shape: { type: 'circle' },
    size: { value: { min: 0.2, max: 3 } }
  }
});
</script>

<template>
  <div class="layout-topbar bg-black">
    <vue-particles id="tsparticles2" :options="particlesOption" class="absolute h-full w-full -ml-5" />

    <div class="flex mr-5">
      <AppTitle class="text-white text-2xl sm:text-3xl md:text-4xl text-900 layout-topbar-logo" />
    </div>

    <div v-if="user" class="text-overflow-ellipsis overflow-hidden align-items-center flex relative">
      <span class="text-white text-900 space-font md:inline-block text-xl md:text-2xl lg:text-3xl xl:text-4xl font-medium
                 text-nowrap text-overflow-ellipsis overflow-hidden">{{ user.fullName }}</span>
      <div class="ml-2">
        <button class="p-link layout-topbar-menu-button layout-topbar-button layout-topbar-button-black"
                @click="onTopBarMenuButton()">
          <i class="pi pi-angle-down"></i>
        </button>
      </div>
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

<style lang="scss" scoped></style>
