<script setup>
import { nextTick, onBeforeMount, onMounted, ref } from 'vue';
import StagePublicBlock from '@/components/prefab/StagePublicBlock.vue';
import { StageService } from '@/service/admin/StageService';
import router from '@/router';
import { isMobileOrTablet } from '@/service/util/UtilsService';

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
  fpsLimit: 30,
  fullScreen: true,
  detectRetina: true,
  particles: {
    color: { value: '#ffffff' },
    move: { direction: 'none', enable: true, outModes: 'bounce', random: true, speed: 0.3, straight: false },
    number: {
      density: { enable: true, area: 800 },
      value: 1500
    },
    opacity: {
      animation: { enable: true, speed: 0.05, startValue: 'random', sync: false, count: 250, destroy: 'none' },
      value: { min: 0, max: 0.35 }
    },
    shape: { type: 'circle' },
    size: { value: { min: 0.2, max: 2 } }
  }
});


const smoothScroll = (id) => {
  document.querySelector(id).scrollIntoView({
    behavior: 'smooth'
  });
};

const isMobile = ref(isMobileOrTablet);
</script>

<template>
  <div class="bg-dark flex justify-content-center relative">
    <div v-if="isMobile" class="fullscreen w-full fixed"
         style="background-image: url('/assets/images/background_particles.svg'); background-repeat: repeat-y;" />
    <vue-particles v-else id="tsparticles" :options="particlesOption" class="absolute h-full w-full" />

    <div id="landing" class="flex flex-column pt-0 px-0 xxl:px-0 overflow-hidden">
      <div class="landing-wrapper overflow-hidden fullscreen"
           style="background: linear-gradient( rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6) ), url('./assets/images/background_photos.png');
                  background-repeat: no-repeat; background-position: left; background-size: cover;">
        <div
          class="py-4 px-3 mx-0 md:mx-3 xxl:mx-6 xxl:px-8 flex align-items-center justify-content-between relative z-4 xxl:static">

          <a v-ripple
             v-styleclass="{ selector: '@next', enterClass: 'hidden', leaveToClass: 'hidden', hideOnOutsideClick: true }"
             class="cursor-pointer block xxl:hidden text-700 p-ripple mr-2">
            <i class="pi pi-bars text-4xl"></i>
          </a>

          <div class="align-items-center flex-grow-1 justify-content-between hidden xxl:flex absolute xxl:static w-fit
                      left-0 px-6 xxl:px-0 z-2 text-4xl bg-black xxl:bg-transparent"
               style="top: 120px;">
            <ul
              class="list-none p-0 m-0 flex xxl:align-items-center select-none flex-column xxl:flex-row cursor-pointer">
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 line-height-3 p-ripple overflow-visible undersquare-3-alt-2 xxl:no-before"
                   @click="smoothScroll('#about')">
                  <span class="space-font white-space-nowrap">О конкурсе</span>
                </a>
              </li>
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 p-ripple overflow-visible undersquare-3-alt-2 xxl:no-before"
                   @click="smoothScroll('#stages')">
                  <span class="space-font ">Этапы</span>
                </a>
              </li>
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 p-ripple overflow-visible undersquare-3-alt-2 xxl:no-before"
                   @click="smoothScroll('#winners2023')">
                  <span class="space-font white-space-nowrap">Победители</span>
                </a>
              </li>
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 p-ripple overflow-visible undersquare-3-alt-2 xxl:no-before"
                   @click="smoothScroll('#contacts')">
                  <span class="space-font ">Контакты</span>
                </a>
              </li>
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 p-ripple overflow-visible undersquare-3-alt-2 xxl:no-before"
                   href="/assets/docs/Положение Студент года 2024.pdf">
                  <span class="space-font ">Положение</span>
                </a>
              </li>
              <li>
                <a :href="'https://auth.mephi.ru/login?service=' + encodeURI(window.$frontHost)"
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 space-font text-primary">Войти</a>
              </li>
            </ul>

            <div class="hidden ml-5 mr-2 xxl:flex">
              <img alt="Mephi logo" class="mr-4" height="50" src="/assets/images/logo_mephi.png" />
              <img alt="Prioritet 2030 logo" height="50" src="/assets/images/logo_prioritet.png" />
            </div>
          </div>

          <div class="relative xxl:hidden flex">
            <img alt="Mephi logo" class="mr-2" height="45" src="/assets/images/logo_mephi.png" />
            <img alt="Prioritet 2030 logo" height="45" src="/assets/images/logo_prioritet.png" />
          </div>
        </div>

        <div class="h-full w-full relative">
          <span
            class="text-white-alpha-40 text-xl text-right line-height-1 space-font absolute bottom-2 xxl:bottom-2 right-0 mr-4">фотоклуб <br> НИЯУ МИФИ</span>

          <AppTitle class=" md:text-7xl pl-5 md:pl-7 xl:pl-10 absolute top-30" style="font-size: 8vw;" />

          <div class="md:hidden absolute top-50 text-nowrap mt-5 w-full flex justify-content-center">
            <div class="grid justify-items-stretch w-min">
              <a :href="'https://auth.mephi.ru/login?service=' + encodeURI(window.$frontHost)"
                 class="w-full block text-xl border-round-2xl py-1 px-3 bg-primary-gradient text-white border-white-alpha-90 flex justify-content-center align-items-center mb-2">
                <i class="mr-2 p-0" style="height: 2em;" />Участвовать
              </a>
              <a
                class="w-full block text-xl border-round-2xl border-1 py-1 px-3 bg-primary-gradient text-white border-white-alpha-90 flex justify-content-center align-items-center"
                href="https://t.me/student_of_year">
                <img class="mr-2 p-0" src="/assets/images/logo_tg_alt.svg" style="height: 2em;" /> Telegram-канал
              </a>
            </div>
          </div>

          <TextRunner always-run class="text-6xl relative text-nowrap flex-nowrap bottom-1 xxl:bottom-1"
                      text="Ядерный лидер" />
        </div>
      </div>

      <div id="about" class="flex flex-column pt-2 overflow-hidden bg-figure-sphere justify-content-center">
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

              <div class="flex align-items-baseline flex-wrap justify-content-between">
                <span class="text-xl sm:text-2xl md:text-3xl lg:text-4xl space-font inline-block mr-4 mb-4 md:mb-6">
                  <a class="text-primary-4" href="#stages">Конкурсные<br>этапы</a>
                </span>
                <span class="text-xl sm:text-2xl md:text-3xl lg:text-4xl space-font inline-block mr-4 mb-4 md:mb-6">
                  <a class="text-primary-4" href="#portfolio">Конкурс<br>портфолио</a>
                </span>
                <span class="text-xl sm:text-2xl md:text-3xl lg:text-4xl space-font inline-block mb-4 md:mb-6">
                  <a class="text-primary-4" href="#superfinal">Суперфинал</a>
                </span>
              </div>

              <p class="text-white font-light text-xl md:text-2xl lg:text-3xl mb-4 md:mb-6">
                <b>Топ-100</b> лучших участников получат памятные призы и денежные премии!
              </p>

              <div class="flex mb-4 md:mb-6">
                <div class="mr-2 md:mr-4 xl:mr-6">
                  <span class="space-font text-primary text-xl md:text-2xl lg:text-3xl">1-10 место</span>
                  <div
                    class="text-nowrap w-fit border-round-2xl font-light text-lg sm:text-xl md:text-2xl lg:text-3xl text-white border-2 border-primary py-2 px-3 lg:px-4 text-left mt-2">
                    Денежный приз <br> в 100 тыс. рублей
                  </div>
                </div>
                <div class="">
                  <span class="space-font text-primary text-xl md:text-2xl lg:text-3xl">11-100 место</span>
                  <div
                    class="text-nowrap w-fit border-round-2xl font-light text-lg sm:text-xl md:text-2xl lg:text-3xl text-white border-2 border-primary py-2 px-3 lg:px-4 text-left mt-2">
                    Денежный приз <br> в 45 тыс. рублей
                  </div>
                </div>
              </div>

              <p class="text-white font-light text-xl md:text-2xl lg:text-3xl">
                <b>Топ-10</b> участников по каждому курсу, не вошедших в список Суперфиналистов, становятся лауреатами
                Конкурса и получают призы.
              </p>
            </div>

            <div class="col-12 md:col-4 text-center">
              <div
                class="hidden xl:block border-round-2xl text-xl md:text-2xl lg:text-3xl mb-4 md:mb-6 text-primary-4 border-1 border-white-alpha-90 py-3 px-2 text-center">
                1 сентября — 1 декабря 2024
              </div>

              <span class="space-font text-primary-4 text-2xl md:text-3xl lg:text-4xl">Кто может участвовать?</span>
              <div
                class="border-round-2xl font-light text-lg md:text-xl lg:text-2xl mb-6 text-white border-1 border-white-alpha-60 py-3 px-2 text-center mt-2 mb-4 border-dashed">
                В конкурсе могут принять участие студенты, обучающиеся по программам подготовки бакалавриата,
                специалитета, магистратуры НИЯУ МИФИ (г. Москва) на очном отделении не имеющие академическую
                задолженность и не имеющие не снятые дисциплинарные взыскания по состоянию на 1 сентября 2024 года.
              </div>

              <div class="grid mx-0">
                <a class="col-6 md:col-12 p-0 pr-2 md:pr-0" href="/assets/docs/Положение Студент года 2024.pdf">
                  <div
                    class="border-round-2xl space-font border-1 border-white-alpha-90 text-center py-2 px-4 mb-4 text-primary-4 text-xl md:text-2xl lg:text-4xl align-content-center"
                    style="min-height: 5.2rem;">
                    Положение о конкурсе
                  </div>
                </a>
                <a class="col-6 md:col-12 p-0 pl-2 md:pl-0" href="#winners2023">
                  <div
                    class="border-round-2xl space-font border-1 border-white-alpha-90 text-center py-2 px-4 mb-4 text-primary-4 text-xl md:text-2xl lg:text-4xl align-content-center"
                    style="min-height: 5.2rem;">
                    Победители предыдущего сезона
                  </div>
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div id="stages" class="flex flex-column pt-2 overflow-hidden bg-figure justify-content-center">
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

      <div id="winners2023"
           class="flex flex-column pt-2 overflow-hidden min-height-100 bg-figure justify-content-center">
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

          <div class="field mb-5 text-nowrap mt-5 flex flex-wrap">
            <a
              class="border-round-3xl border-1 mr-2 md:mr-3 lg:mr-4 py-1 px-3 bg-primary-gradient text-white border-white-alpha-90 flex justify-content-center align-items-center mb-3 sm:mb-4 lg:mb-5 text-xl md:text-2xl lg:text-3xl"
              href="https://t.me/student_of_year">
              <img class="mr-2 p-0" src="/assets/images/logo_tg_alt.svg" style="height: 2em;" />Telegram-канал
            </a>

            <a class="border-round-3xl border-1 mr-2 md:mr-3 lg:mr-4 py-1 px-3 bg-primary-gradient text-white border-white-alpha-90 flex justify-content-center align-items-center mb-3 sm:mb-4 lg:mb-5 text-xl md:text-2xl lg:text-3xl"
               href="https://t.me/student_of_year">
              <img class="mr-2 p-0" src="/assets/images/logo_tg.svg" style="height: 2em;" />Telegram-бот
            </a>

            <a
              class="border-round-3xl border-1 py-1 px-3 bg-primary-gradient text-white border-white-alpha-90 flex justify-content-center align-items-center mb-3 sm:mb-4 lg:mb-5 text-xl md:text-2xl lg:text-3xl"
              href="mailto:DIValkovets@mephi.ru?subject=Студент Года: Тех. поддержка">
              <img class="mr-2 p-0" src="/assets/images/logo_email.png" style="height: 2em;" />Тех. поддержка
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>

  <Toast />
</template>

<style lang="scss">
* {
  line-height: 1.1em;
}

.bg-dark {
  background-color: #161616 !important;
}

.fullscreen {
  height: 100vh;
  height: 100dvh;
}

.bg-figure {
  background-image: url("/assets/images/figure.svg");
  background-repeat: no-repeat;
  background-position: right center;
  background-size: auto max(40%, 50em);
}

.bg-figure-sphere {
  background-image: url("/assets/images/figure_sphere.svg");
  background-repeat: no-repeat;
  background-position: center bottom;
  background-size: auto max(40%, 50em);
}

.min-height-100 {
  min-height: 100vh;
}

.font-light {
  font-weight: 300 !important;

  b {
    font-weight: 600 !important;
  }
}

body {
  background-color: #161616 !important;
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
  left: 35%;
}

.bottom-1 {
  top: calc(100% - 9.5rem);
}

.bottom-2 {
  top: calc(100% - 9.5rem - 3rem);
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
  width: 25%;
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

@media screen and (min-width: 1600px) {
  .xxl\:bottom-1 {
    top: calc(100% - 11rem) !important;
  }

  .xxl\:bottom-2 {
    top: calc(100% - 11rem - 3rem) !important;
  }

  .xxl\:hidden {
    display: none !important;
  }

  .xxl\:flex {
    display: flex !important;
  }

  .xxl\:flex-row {
    flex-direction: row !important;
  }

  .xxl\:align-items-center {
    align-items: center !important;
  }

  .xxl\:static {
    position: static !important;
  }

  .xxl\:px-0 {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }

  .xxl\:pl-8 {
    padding-left: 5rem !important;
  }

  .xxl\:mx-6 {
    margin-left: 3rem !important;
    margin-right: 3rem !important;
  }

  .xxl\:bg-transparent {
    background-color: transparent !important;
  }

  .xxl\:no-before::before {
    display: none !important;
  }
}
</style>