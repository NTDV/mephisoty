<script setup>
import { nextTick, onBeforeMount, onMounted, ref } from 'vue';
import StagePublicBlock from '@/components/prefab/StagePublicBlock.vue';
import { StageService } from '@/service/admin/StageService';
import router from '@/router';

const stageService = new StageService();
const stageModels = ref([]);
const winnersNums = ref(Array.from({ length: 100 }, (_, i) => i + 1));

onBeforeMount(() => {
  stageService.getAllPublic()
    .then((json) => {
      stageModels.value = json;
    });
});

onMounted(() => {
  const section = router.currentRoute.value.hash.replace('#', '');
  if (section) {
    nextTick(() => window?.document?.getElementById(section)?.scrollIntoView());
  }
});

const particlesOption = ref({
  fpsLimit: 24,
  fullScreen: false,
  detectRetina: true,
  background: { color: '#000000', opacity: 0.65 },
  particles: {
    color: { value: '#ffffff' },
    move: { direction: 'none', enable: true, outModes: 'bounce', random: true, speed: 0.3, straight: false },
    number: {
      density: { enable: true, area: 800 },
      value: 1500
    },
    opacity: {
      animation: { enable: true, speed: 0.05, startValue: 'random', sync: false, count: 250, destroy: 'none' },
      value: { min: 0, max: 0.3 }
    },
    shape: { type: 'circle' },
    size: { value: { min: 0.2, max: 3 } }
  }
});


const smoothScroll = (id) => {
  document.querySelector(id).scrollIntoView({
    behavior: 'smooth'
  });
};
</script>

<template>
  <div class="bg-black-alpha-90 flex justify-content-center relative">
    <vue-particles id="tsparticles" :options="particlesOption" class="absolute h-full w-full" />

    <div id="landing" class="flex flex-column pt-0 px-0 xl:px-0 overflow-hidden">
      <div class="landing-wrapper overflow-hidden fullscreen"
           style="background-image: url('./assets/images/background_photos.png'); background-repeat: no-repeat;
                  background-position: left; background-size: cover;">
        <div
          class="py-4 px-3 mx-0 md:mx-3 xl:mx-6 xl:px-8 flex align-items-center justify-content-between relative xl:static">
          <a v-ripple
             v-styleclass="{ selector: '@next', enterClass: 'hidden', leaveToClass: 'hidden', hideOnOutsideClick: true }"
             class="cursor-pointer block xl:hidden text-700 p-ripple">
            <i class="pi pi-bars text-4xl"></i>
          </a>

          <div class="align-items-center flex-grow-1 justify-content-between hidden xl:flex absolute xl:static w-fit
                      left-0 px-6 xl:px-0 z-2 text-4xl bg-black xl:bg-transparent"
               style="top: 120px;">
            <ul class="list-none p-0 m-0 flex xl:align-items-center select-none flex-column xl:flex-row cursor-pointer">
              <li>
                <a v-ripple class="flex m-0 md:ml-5 px-0 py-3 text-900 line-height-3 p-ripple"
                   @click="smoothScroll('#about')">
                  <span class="space-font white-space-nowrap">О конкурсе</span>
                </a>
              </li>
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 p-ripple"
                   @click="smoothScroll('#stages')">
                  <span class="space-font ">Этапы</span>
                </a>
              </li>
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 p-ripple"
                   @click="smoothScroll('#winners2023')">
                  <span class="space-font white-space-nowrap">Победители</span>
                </a>
              </li>
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 p-ripple"
                   @click="smoothScroll('#contacts')">
                  <span class="space-font ">Контакты</span>
                </a>
              </li>
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 p-ripple"
                   @click="smoothScroll('#regulations')">
                  <span class="space-font ">Положение</span>
                </a>
              </li>
              <li>
                <a :href="'https://auth.mephi.ru/login?service=' + encodeURI(window.$frontHost)"
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 space-font text-primary">Войти</a>
              </li>
            </ul>

            <img alt="Mephi logo" class="ml-5" height="50" src="/assets/images/logo_mephi.png" />
          </div>

          <img alt="Mephi logo" class="relative xl:hidden" height="50" src="/assets/images/logo_mephi.png" />
        </div>

        <div class="h-full w-full relative">
          <AppTitle class="text-4xl sm:text-6xl md:text-7xl pl-5 md:pl-7 xl:pl-10 absolute top-30" />

          <div class="md:hidden absolute top-50 left-2 grid justify-items-stretch mt-5 text-center">
            <a :href="'https://auth.mephi.ru/login?service=' + encodeURI(window.$frontHost)"
               class="block border-round-2xl mb-2 py-2 px-4 bg-primary-gradient text-white text-2xl md:text-3xl">
              Участвовать
            </a>
            <a
              class="block border-round-2xl py-2 px-4 bg-primary-gradient text-white border-white-alpha-90 text-2xl md:text-3xl"
              href="https://t.me/student_of_year">
              <img class="-mb-2 mr-2" src="/assets/images/logo_tg_alt.svg" /> Telegram-канал
            </a>
          </div>

          <TextRunner class="text-6xl relative text-nowrap flex-nowrap bottom-1 xl:bottom-1" text="Ядерный лидер" />
        </div>
      </div>

      <div id="about" class="flex flex-column pt-2 overflow-hidden min-height-100 justify-content-center">
        <TextRunner class="text-6xl relative -rotate-90 left-1 xl:left-1 hidden md:block top-1" text="О конкурсе" />

        <div class="relative px-4 md:px-7 xl:pl-10 mt-4 md:mt-0">
          <h1 class="space-font text-primary text-4xl sm:text-5xl md:text-6xl lg:text-7xl">Студент года 3.0</h1>
          <h1 class="space-font text-primary text-4xl sm:text-5xl md:text-6xl lg:text-7xl -mt-5 mb-4 md:mb-6">НИЯУ
            МИФИ</h1>

          <div class="grid">
            <div class="col-12 md:col-8 md:pr-4">
              <p class="text-white font-light text-xl md:text-2xl lg:text-3xl mb-4 md:mb-6 line-height-2">
                <b>Конкурс «Студент Года НИЯУ МИФИ»</b> — конкурс среди обучающихся НИЯУ МИФИ, имеющих особые достижения
                в области науки, спорта, молодежной политики, студенческого лидерства и общественной деятельности.
              </p>

              <div class="flex align-items-baseline flex-wrap mb-2 md:mb-4 ">
                <span class="text-2xl md:text-3xl lg:text-4xl space-font inline-block mr-4 mb-2">
                  <a class="text-primary-4" href="#stages">Конкурсные<br>этапы</a>
                </span>
                <span class="text-2xl md:text-3xl lg:text-4xl space-font inline-block mr-4 mb-2">
                  <a class="text-primary-4" href="#portfolio">Конкурс<br>портфолио</a>
                </span>
                <span class="text-2xl md:text-3xl lg:text-4xl space-font inline-block mb-2">
                  <a class="text-primary-4" href="#portfolio">Суперфинал</a>
                </span>
              </div>

              <p class="text-white font-light text-xl md:text-2xl lg:text-3xl mb-4 md:mb-6">
                <b>Топ-100</b> лучших участников получат памятные призы и денежные премии!
              </p>

              <div class="grid mb-4 md:mb-6">
                <div class="col-12 md:col-6">
                  <span class="space-font text-primary text-xl md:text-2xl lg:text-3xl">1-10 место</span>
                  <div
                    class="border-round-2xl font-light text-xl md:text-2xl lg:text-3xl text-white border-1 border-primary py-3 px-2 text-center mt-2">
                    Денежный приз в 100 тыс. рублей
                  </div>
                </div>
                <div class="col-12 md:col-6">
                  <span class="space-font text-primary text-xl md:text-2xl lg:text-3xl">11-100 место</span>
                  <div
                    class="border-round-2xl font-light text-xl md:text-2xl lg:text-3xl text-white border-1 border-primary py-3 px-2 text-center mt-2">
                    Денежный приз в 60 тыс. рублей
                  </div>
                </div>
              </div>

              <p class="text-white font-light text-xl md:text-2xl lg:text-3xl mb-4">
                <b>Топ-10</b> участников по каждому курсу, не вошедших в список Суперфиналистов, становятся лауреатами
                Конкурса и получают призы.
              </p>
            </div>

            <div class="col-12 md:col-4 md:pl-4 text-center">
              <div
                class="border-round-2xl text-xl md:text-2xl lg:text-3xl mb-4 md:mb-6 text-primary-4 border-1 border-white-alpha-90 py-3 px-2 text-center">
                1 сентября — 1 декабря 2024
              </div>

              <span class="space-font text-primary-4 text-2xl md:text-3xl lg:text-4xl">Кто может участвовать?</span>
              <div
                class="border-round-2xl font-light text-lg md:text-xl lg:text-2xl mb-4 md:mb-6 text-white border-1 border-white-alpha-90 py-3 px-2 text-center mt-2 mb-4 border-dashed">
                В конкурсе могут принять участие студенты, обучающиеся по программам подготовки бакалавриата,
                специалитета, магистратуры НИЯУ МИФИ (г. Москва) на очном отделении не имеющие академическую
                задолженность и не имеющие не снятые дисциплинарные взыскания по состоянию на 1 сентября 2023 года.
              </div>

              <div
                class="border-round-2xl space-font border-1 border-white-alpha-90 py-3 px-2 text-center mb-4 md:mb-6">
                <a class="text-primary-4 text-2xl md:text-3xl lg:text-4xl" href="#polozhenie">Положение о конкурсе</a>
              </div>

              <div
                class="border-round-2xl space-font border-1 border-white-alpha-90 py-3 px-2 text-center mb-4 md:mb-6">
                <a class="text-primary-4 text-2xl md:text-3xl lg:text-4xl" href="#winners">Победители предыдущего
                  сезона</a>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div id="stages" class="flex flex-column pt-2 overflow-hidden min-height-100 justify-content-center">
        <TextRunner class="text-6xl relative -rotate-90 left-1 xl:left-1 hidden md:block top-1"
                    text="Конкурсные этапы" />

        <div class="relative px-4 md:px-7 xl:pl-10 mt-4 md:mt-0">
          <h1 class="space-font text-primary text-4xl sm:text-5xl md:text-6xl lg:text-7xl mb-4 md:mb-6">Конкурсные
            этапы</h1>

          <p class="text-white font-light text-xl md:text-2xl lg:text-3xl mb-4 md:mb-8 line-height-2">
            Для участия в Конкурсе «Студент года НИЯУ МИФИ 2024» необходимо принять участие <b>минимум в одном</b> из
            <b>9</b> этапов Конкурса
          </p>

          <div class="grid">
            <template v-for="stageModel in stageModels">
              <StagePublicBlock :model-value="stageModel" />
            </template>
          </div>
        </div>
      </div>

      <div id="winners2023" class="flex flex-column pt-2 overflow-hidden min-height-100 justify-content-center">
        <TextRunner class="text-6xl relative -rotate-90 left-1 xl:left-1 hidden md:block top-1"
                    text="Победители предыдущего этапа" />

        <div class="relative px-4 md:px-7 xl:pl-10 mt-4 md:mt-0">
          <h1 class="space-font text-primary text-4xl sm:text-5xl md:text-6xl lg:text-7xl mb-4 md:mb-6">Победители
            предыдущего года</h1>

          <div class="grid">
            <template v-for="winnerNum in winnersNums">
              <div class="col-10-5 sm:col-10-2 xl:col-10-1">
                <img :src="'/assets/images/W' + winnerNum + '.png'" class="w-full" />
              </div>
            </template>
          </div>
        </div>
      </div>

      <div id="contacts" class="flex flex-column pt-2 overflow-hidden justify-content-center">
        <TextRunner class="text-6xl relative -rotate-90 left-1 xl:left-1 hidden md:block top-1" text="Контакты" />

        <div class="relative px-4 md:px-7 xl:pl-10 mt-4 md:mt-0">
          <h1 class="space-font text-primary text-4xl sm:text-5xl md:text-6xl lg:text-7xl mb-4 md:mb-6">Контакты</h1>

          <div class="field mb-5">
            <a
              class="border-round-2xl py-2 px-4 bg-primary-gradient text-white border-white-alpha-90 text-2xl md:text-3xl"
              href="https://t.me/student_of_year">
              <img class="-mb-2 mr-2" src="/assets/images/logo_tg_alt.svg" /> Канал «Студент Года МИФИ»
            </a>
          </div>

          <div class="field mb-5">
            <a
              class="border-round-2xl py-2 px-4 bg-primary-gradient text-white border-white-alpha-90 text-2xl md:text-3xl"
              href="https://t.me/Best_MEPHI_student_bot">
              <i class="pi pi-telegram text-2xl -mb-2 mr-2" /> Телеграм-бот конкурса
            </a>
          </div>

          <div class="field mb-8">
            <a
              class="border-round-2xl py-2 px-4 bg-primary-gradient text-white border-white-alpha-90 text-2xl md:text-3xl"
              href="mailto:DIValkovets@mephi.ru?subject=Студент Года: Тех. поддержка">
              <i class="pi pi-envelope text-2xl -mb-2 mr-2" /> Тех. поддержка
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>

  <Toast />
</template>

<style lang="scss">
.fullscreen {
  height: 100vh;
  height: 100dvh;
}

.min-height-100 {
  min-height: 100vh;
  background-image: url("/assets/images/figure.svg");
  background-repeat: no-repeat;
  background-position: right bottom;
  background-size: auto 40%;
}

.font-light {
  font-weight: 100 !important;

  b {
    font-weight: 500 !important;
  }
}

body {
  background: black;
}

.landing-wrapper {
  * {
    color: white;
  }
}

.top-1 {
  top: 100%;
}

.left-1 {
  left: calc(-50% + 1rem);
}

.left-2 {
  text-wrap: nowrap;
  left: 10%;
  right: 45%;
}

.bottom-1 {
  top: calc(100% - 9.5rem);
}

.text-primary-4 {
  color: var(--primary-colors-4) !important;
}

.text-primary-3 {
  color: var(--primary-colors-3) !important;
}

.col-10-5 {
  flex: 0 0 auto;
  padding: 0.5rem;
  width: 33.333%;
}

@media screen and (min-width: 576px) {
  .sm\:col-10-2 {
    flex: 0 0 auto;
    padding: 0.5rem;
    width: 20%;
  }
}

@media screen and (min-width: 992px) {
  .md\:max-w-70 {
    max-width: 70%;
  }
}

@media screen and (min-width: 1200px) {
  .xl\:left-1 {
    left: calc(-50% + 3rem);
  }

  .xl\:bottom-1 {
    top: calc(100% - 11rem) !important;
  }

  .xl\:pl-10 {
    padding-left: 10rem !important;
  }

  .xl\:px-10 {
    padding-left: 10rem !important;
    padding-right: 10rem !important;
  }

  .xl\:col-10-1 {
    flex: 0 0 auto;
    padding: 0.5rem;
    width: 10%;
  }
}
</style>