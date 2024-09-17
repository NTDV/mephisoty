<script setup>
import { computed, ref } from 'vue';
import { MeService } from '@/service/participant/MeService';
import { useToast } from 'primevue/usetoast';

const stageModel = defineModel();
const title = computed(() => {
  return stageModel.value.title.replace(/ *\| */g, '\n');
});
const aboutPanel = ref(null);
const canNotApplyPanel = ref(null);
const reglament = computed(() => {
  return stageModel.value.files.find(file => file.code === 'reglament');
});

const meService = new MeService();
const toast = useToast();

const apply = () => {
  meService.applyto(stageModel.value.id)
    .then(bool => {
      if (bool) {
        toast.add({ severity: 'success', summary: 'Успешно', detail: 'Вы зарегистрировались на этап', life: 3000 });
      } else {
        toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Не удалось отправить заявку', life: 3000 });
      }
    });
};

//todo check description for bad html

</script>

<template>
  <div class="col-12 md:col-6 xl:col-4 text-center xl:text-left">
    <span
      :class="'space-font text-2xl md:text-3xl xl:text-4xl pre ' + (stageModel.id > 3 && stageModel.id < 7 ? 'text-white' : 'text-primary-4')">
      {{ title }}</span>

    <div class="flex justify-content-center xl:justify-content-start mt-2 xl:mt-4">
      <div class="mr-4 sm:mr-2">
        <div
          class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 xl:px-3 xxl:px-4 text-center cursor-pointer"
             @click="aboutPanel.toggle">
          <a class="text-white text-lg md:text-xl lg:text-2xl">Подробнее</a>
        </div>
      </div>

      <a v-if="stageModel.id === 2 && stageModel.applyVisibility !== 'DISALLOW_ALL_FOR_PARTICIPANTS'" class="block"
         href="/hackathon">
        <div
          class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 xl:px-3 xxl:px-4 text-center cursor-pointer">
          <span class="text-white text-lg md:text-xl lg:text-2xl">Участвовать</span>
        </div>
      </a>

      <div v-else-if="stageModel.applyVisibility !== 'DISALLOW_ALL_FOR_PARTICIPANTS'" @click="apply">
        <div
          class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 xl:px-3 xxl:px-4 text-center cursor-pointer">
          <a class="text-white text-lg md:text-xl lg:text-2xl">Участвовать</a>
        </div>
      </div>

      <div v-else class="">
        <div
          class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 xl:px-3 xxl:px-4 text-center cursor-pointer"
             @click="canNotApplyPanel.toggle">
          <a class="text-white text-lg md:text-xl lg:text-2xl">Участвовать</a>
        </div>
      </div>
    </div>
  </div>

  <OverlayPanel ref="aboutPanel" appendTo="body" class="bg-black border-round-2xl max-w-full md:max-w-30rem"
                style="max-width: 50%">
    <div class="font-light md:text-lg lg:text-xl text-white p-3 pre mb-2" v-html="stageModel.description">
    </div>

    <div class="flex justify-content-center">
      <a v-if="reglament" :href="window.$apiHost + '/file/public/' + reglament.id"
         class="block w-fit text-white text-lg md:text-xl lg:text-2xl border-round-2xl border-1 border-white-alpha-90 py-3 px-2 text-center cursor-pointer">
        Регламент этапа
      </a>
    </div>
  </OverlayPanel>

  <OverlayPanel ref="canNotApplyPanel" appendTo="body" class="bg-black border-round-2xl max-w-full md:max-w-30rem"
                style="max-width: 50%">
    <div class="font-light md:text-lg lg:text-xl text-white p-3 pre mb-2">
      Регистрация на этап закрыта.
    </div>
  </OverlayPanel>

</template>

<style lang="scss">
.pre {
  white-space: pre-line;
}
</style>