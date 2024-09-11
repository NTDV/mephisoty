<script setup>

import { useRoute, useRouter } from 'vue-router';
import { FileService } from '@/service/admin/FileService';
import { onMounted } from 'vue';

const route = useRoute();
const router = useRouter();

const fileService = new FileService();

onMounted(() => {
  fileService.download(route.params.id)
    .then(() => {
      router.back();
    })
    .catch(e => {
      console.error(e);
      router.push({ name: 'notfound' });
    });
});

</script>

<template>
  <div class="text-center">
    <ProgressSpinner animationDuration=".5s" class="mt-4" strokeWidth="8"
                     style="display: block; width: 33px; height: 33px" />
    <h1 class="space-font">Скачивание файла...</h1>
  </div>
</template>

<style lang="scss" scoped>

</style>