<script setup>
import AppTitle from '@/components/prefab/AppTitle.vue';
</script>

<template>
  <div class="surface-ground flex align-items-center justify-content-center min-h-screen min-w-screen overflow-hidden">
    <div class="flex flex-column align-items-center justify-content-center">
      <AppTitle class="mb-3 text-4xl text-900" />

      <div
        style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, rgba(233, 30, 99, 0.4) 10%, rgba(33, 150, 243, 0) 30%)">
        <div class="w-full surface-card py-8 px-5 sm:px-8 flex flex-column align-items-center"
             style="border-radius: 53px">
          <div class="grid flex flex-column align-items-center">
            <div class="flex justify-content-center align-items-center bg-pink-500 border-circle"
                 style="height: 3.2rem; width: 3.2rem">
              <i class="pi pi-fw pi-exclamation-circle text-2xl text-white"></i>
            </div>
            <h1 class="text-900 font-bold text-5xl mb-2">Произошла ошибка</h1>
            <span class="text-600 mb-5">Попробуйте <a href="/logout">выйти из аккаунта</a> или обратитесь к организаторам или администратору.</span>
            <img alt="Error" class="mb-5" src="/demo/images/error/asset-error.svg" width="80%" />
            <div class="col-12 mt-5 text-center">
              <i class="pi pi-fw pi-arrow-left text-blue-500 mr-2" style="vertical-align: center"></i>
              <router-link class="text-blue-500" to="/">Вернуться на главную</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
