<script setup>
import AppTitle from '@/components/prefab/AppTitle.vue';
</script>

<template>
  <div class="surface-ground flex align-items-center justify-content-center min-h-screen min-w-screen overflow-hidden">
    <div class="flex flex-column align-items-center justify-content-center">
      <AppTitle class="mb-3 text-4xl text-900" />

      <div
        style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, rgba(33, 150, 243, 0.4) 10%, rgba(33, 150, 243, 0) 30%)">
        <div class="w-full surface-card py-6 px-5 sm:px-8 flex flex-column align-items-center"
             style="border-radius: 53px">
          <div class="flex justify-content-center align-items-center" style="width: 3.2rem; height: 3.2rem">
            <span class="text-blue-500 font-bold text-3xl lg:text-5xl">404</span>
          </div>


          <h1 class="text-900 font-bold text-3xl lg:text-5xl mb-2">Не найдено</h1>
          <span class="text-600 mb-5">Запрошенного ресурса не существует.</span>

          <div class="col-12 text-center">
            <i class="pi pi-fw pi-arrow-left text-blue-500 mr-2" style="vertical-align: center"></i>
            <router-link class="text-blue-500" to="/">Вернуться на главную</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
