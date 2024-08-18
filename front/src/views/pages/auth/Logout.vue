<script setup>
import { onBeforeMount } from 'vue';

onBeforeMount(() => {
  delete localStorage.jwt;
});
</script>

<template>
  <div class="surface-ground flex align-items-center justify-content-center min-h-screen min-w-screen overflow-hidden">
    <div class="flex flex-column align-items-center justify-content-center">
      <div
        style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
        <div class="w-full surface-card py-6 px-5 sm:px-8" style="border-radius: 53px">
          <div class="text-center mb-5">
            <div class="display-grid mb-3">
              <span class="space-font text-900 text-3xl font-medium text-nowrap">Студент года 3.0</span>
              <span class="space-font text-900 text-3xl font-medium text-nowrap">НИЯУ МИФИ</span>
            </div>
          </div>

          <div>
            <label class="block text-900 text-xl font-medium mb-2" for="email">Вы успешно вышли из аккаунта
              приложения.</label>
            <label>Чтобы выйти из аккаунта НИЯУ МИФИ,
              <a class="font-medium no-underline text-right cursor-pointer"
                 href="https://auth.mephi.ru/logout" style="color: var(--primary-color)">перейдите по ссылке</a>.
            </label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
