<template>
  <div class="text-left">
    <div class="relative">
      <img class="absolute pointer-events-none" src="/assets/images/atom.svg"
           style="height: 7.1em; left: -0.85em; top: -2.6em" />
      <router-link style="color: inherit" to="/">
        <span class="relative space-font font-medium text-nowrap">Студент года 3.0</span>
        <div class="relative space-font font-medium text-nowrap flex align-items-center">
          НИЯУ МИФИ
          <div style="height: 0.14em; margin-left: 0.5em; width: 1.3em; background: var(--primary-colors-3);"></div>
          <div style="height: 0.14em; margin-left: 0.2em; width: 1em; background: var(--primary-colors-2)"></div>
          <div style="height: 0.14em; margin-left: 0.2em; width: 0.5em; background: var(--primary-color)"></div>
        </div>
      </router-link>
    </div>
  </div>
</template>
<script lang="ts" setup>
</script>