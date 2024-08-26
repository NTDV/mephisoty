<script setup>
import { computed, ref } from 'vue';
import ParticipantTopbar from './ParticipantTopbar.vue';
import AppFooter from './../AppFooter.vue';
import { useLayout } from '@/layout/composables/layout';

const { layoutConfig, layoutState } = useLayout();

const containerClass = computed(() => {
  return {
    'layout-theme-light': layoutConfig.darkTheme.value === 'light',
    'layout-theme-dark': layoutConfig.darkTheme.value === 'dark',
    'layout-overlay': layoutConfig.menuMode.value === 'overlay',
    'layout-static': layoutConfig.menuMode.value === 'static',
    'layout-static-inactive': layoutState.staticMenuDesktopInactive.value && layoutConfig.menuMode.value === 'static',
    'layout-overlay-active': layoutState.overlayMenuActive.value,
    'layout-mobile-active': layoutState.staticMenuMobileActive.value,
    'p-ripple-disabled': layoutConfig.ripple.value === false
  };
});


const particlesOption = ref({
  fpsLimit: 24,
  fullScreen: false,
  detectRetina: true,
  particles: {
    color: { value: '#0000ff' },
    move: { direction: 'none', enable: true, outModes: 'bounce', random: true, speed: 0.3, straight: false },
    number: {
      density: { enable: true, area: 200 },
      value: 600
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
  <div :class="containerClass" class="layout-wrapper">
    <participant-topbar></participant-topbar>
    <div class="layout-main-container">
      <div class="layout-main">
        <vue-particles id="tsparticles1" :options="particlesOption" class="absolute h-full w-full -ml-5"
                       style="margin-top: -7.5rem;" />

        <router-view class="relative"></router-view>
      </div>
    </div>
    <div class="layout-mask"></div>
  </div>
  <Toast />
</template>

<style lang="scss" scoped></style>
