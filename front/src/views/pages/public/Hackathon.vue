<script setup>
import { nextTick, onMounted, ref } from 'vue';
import router from '@/router';
import { isMobileOrTablet } from '@/service/util/UtilsService';
import { MeService } from '@/service/participant/MeService';
import { PublicService } from '@/service/public/PublicService';
import { useToast } from 'primevue/usetoast';

const authed = ref(false);
const request = ref({});

const meService = new MeService();
const publicService = new PublicService();

const toast = useToast();
const showDialog = ref(false);

onMounted(() => {
  if (localStorage.jwt != null) {
    meService.getMe()
      .then(me => {
        if (me && !me.err) {
          authed.value = true;

          request.value.userId = JSON.parse(localStorage.jwt_payload).user.id;
          request.value.firstName = me.firstName;
          request.value.secondName = me.secondName;
          request.value.thirdName = me.thirdName;
          request.value.groupTitle = me.groupTitle;
          request.value.filial_ = { name: 'НИЯУ МИФИ, г. Москва' };
          request.value.tg = me.tg;
        }
      })
      .catch();
  }

  const section = router.currentRoute.value.hash.replace('#', '');
  if (section) {
    nextTick(() => window?.document?.getElementById(section)?.scrollIntoView());
  }
});

const trimHttp = (str) => {
  if (str == null) return '';

  return str.replace('http://', '').replace('https://', '');
};

const trimTg = (str) => {
  if (str == null) return '';

  const ret = trimHttp(str).replace('t.me/', '');
  if (ret[0] === '@') return ret.slice(1);
  return ret;
};

const validate = () => {
  if (!request.value.firstName ||
    !request.value.secondName ||
    //!request.value.thirdName ||
    !request.value.groupTitle ||
    !request.value.filial_ ||
    request.value.filial_?.name === 'Другой' && !request.value.filial ||
    !request.value.tg ||
    !request.value.email ||
    !request.value.task ||
    !request.value.commandTitle
  ) {
    toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Заполните все поля', life: 3000 });
    return false;
  }

  return true;
};

const submitRequest = () => {
  if (!validate()) return;

  publicService.applyHackathon({
    ...request.value,
    tg: trimTg(request.value.tg),
    task: request.value.task.name,
    filial: request.value.filial_.name === 'Другой' ? request.value.filial : request.value.filial_.name
  })
    .then((ok) => {
      if (!ok) {
        toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Не удалось отправить заявку', life: 3000 });
      } else {
        toast.add({ severity: 'success', summary: 'Успешно', detail: 'Заявка отправлена', life: 3000 });
      }
    }).catch(e => toast.add({
    severity: 'error',
    summary: 'Ошибка',
    detail: 'Не удалось отправить заявку',
    life: 3000
  }))
    .finally(() => showDialog.value = false);
};

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

const tasks = ref([
  { name: 'Прогноз нагрузки на дороги и метро | МосТрансПроект' },
  { name: 'Геймификация взаимодействия в IT-командах | Лоция' },
  { name: 'Table of Content | T1' },
  { name: 'Использование ИИ в продукте | МТС Линк' },
  { name: 'Определение лекарства | Кафедра Хемоинформатики НИЯУ МИФИ' }
]);

const filials = ref([
  { name: 'НИЯУ МИФИ, г. Москва' },
  { name: 'Другой' },
  { name: 'Обнинский институт атомной энергетики' },
  { name: 'Балаковский инженерно-технологический институт' },
  { name: 'Волгодонский инженерно-технический институт' },
  { name: 'Димитровградский инженерно-технологический институт' },
  { name: 'Новоуральский технологический институт' },
  { name: 'Озерский технологический институт' },
  { name: 'Саровский физико-технический институт' },
  { name: 'Северский технологический институт' },
  { name: 'Снежинский физико-технический институт' },
  { name: 'Технологический институт' },
  { name: 'Трехгорный технологический институт' },
  { name: 'Ташкентский филиал' },
  { name: 'Университетский лицей №1511' },
  { name: 'Университетский лицей №1523' }
]);

const smoothScroll = (id) => {
  document.querySelector(id).scrollIntoView({
    behavior: 'smooth'
  });
};

const isMobile = ref(isMobileOrTablet);

const aboutPanel_mostrans = ref(null);
const aboutPanel_locia = ref(null);
const aboutPanel_t1 = ref(null);
const aboutPanel_mephi = ref(null);
const aboutPanel_mts = ref(null);
</script>

<template>
  <div class="bg-dark flex justify-content-center relative">
    <div v-if="isMobile" class="fullscreen w-full fixed"
         style="background-image: url('/assets/images/background_particles.svg'); background-repeat: repeat-y;" />
    <vue-particles v-else id="tsparticles" :options="particlesOption" class="absolute h-full w-full" />

    <div id="landing" class="flex flex-column pt-0 px-0 lg:px-0 overflow-hidden">
      <div class="landing-wrapper overflow-hidden min-height-100">
        <div
          class="py-4 px-3 mx-0 lg:mx-6 lg:px-8 flex align-items-center justify-content-between w-full fixed z-4 lg:static bg-black lg:bg-transparent">

          <a v-ripple
             v-styleclass="{ selector: '@next', enterClass: 'hidden', leaveToClass: 'hidden', hideOnOutsideClick: true }"
             class="cursor-pointer block lg:hidden text-700 p-ripple mr-2">
            <i class="pi pi-bars text-4xl"></i>
          </a>

          <div class="align-items-center flex-grow-1 justify-content-end hidden lg:flex absolute lg:static w-fit
                      left-0 px-6 lg:px-0 z-2 text-4xl bg-black lg:bg-transparent"
               style="top: 70px;">
            <ul
              class="list-none p-0 m-0 flex lg:align-items-center select-none flex-column lg:flex-row cursor-pointer mr-4">
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 line-height-3 p-ripple overflow-visible undersquare-alt-2 lg:no-before"
                   @click="smoothScroll('#about')">
                  <span class="space-font white-space-nowrap">О хакатоне</span>
                </a>
              </li>
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 p-ripple overflow-visible undersquare-alt-2 lg:no-before"
                   @click="smoothScroll('#tasks')">
                  <span class="space-font ">Задачи</span>
                </a>
              </li>
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 p-ripple overflow-visible undersquare-alt-2 lg:no-before"
                   @click="smoothScroll('#faq')">
                  <span class="space-font white-space-nowrap">FAQ</span>
                </a>
              </li>
              <li>
                <a v-ripple
                   class="flex m-0 md:ml-5 px-0 py-3 text-900 font-medium line-height-3 p-ripple overflow-visible undersquare-alt-2 lg:no-before"
                   @click="smoothScroll('#contacts')">
                  <span class="space-font ">Контакты</span>
                </a>
              </li>
              <li>
                <a v-tooltip.bottom="'Только для студентов НИЯУ МИФИ г. Москва'"
                   :href="'https://auth.mephi.ru/login?service=' + encodeURI(window.$frontHost)"
                   class="flex m-0 md:ml-5 px-0 py-3 font-medium line-height-3 space-font text-primary-colors-4">Войти</a>
              </li>
            </ul>
          </div>

          <a class="lg:hidden text-2xl border-round-3xl space-font py-2 px-4 border-primary-colors-4 border-2 bg-black mr-4" href="#"
             @click="showDialog = true">
            Участвовать
          </a>
        </div>

        <div class="h-full w-full relative">
          <div class="justify-content-end hidden lg:flex mr-8">
            <a class="text-3xl border-round-3xl space-font py-2 px-4 border-primary-colors-4 border-2 mr-8" href="#"
               @click="showDialog = true">
              Участвовать
            </a>
          </div>

          <div class="sm:text-7xl xl:text-8xl xxl:text-9xl pl-5 md:pl-7 absolute top-1 text-left w-min max-w-max h-max"
               style="font-size: 8vw; min-width: 450px;">
            <div class="flex align-items-center space-font font-medium text-nowrap">Хакатон
              <div style="height: 0.07em; margin-left: 0.25em; width: 1em; background: var(--primary-colors-3);"></div>
              <div style="height: 0.07em; margin-left: 0.2em; width: 0.8em; background: var(--primary-colors-2)"></div>
              <div style="height: 0.07em; margin-left: 0.2em; width: 0.5em; background: var(--primary-color)"></div>
            </div>
            <span class="space-font font-medium text-nowrap block -mt-2 sm:-mt-4 -mb-2">
              Nuclear it hack
            </span>
            <p class="text-xl sm:text-3xl md:text-4xl lg:text-5xl font-light line-height-1">
              IT хакатон для студентов НИЯУ&nbsp;МИФИ
              <br>
              и студентов филиалов
            </p>


            <div class="flex flex-wrap -m-3 mt-2">
              <a href="https://mtp.mos.ru/"><img class="h-adaptive-2 p-1 m-2" src="/assets/images/logo_mostrans.svg" /></a>
              <a href="https://mts-link.ru/"><img class="h-adaptive-2 p-1 m-2" src="/assets/images/logo_mts.svg" /></a>
              <a href="https://www.loodsen.ru/"><img class="h-adaptive-2 p-1 m-2" src="/assets/images/logo_locia.png" /></a>
              <a href="https://t1.ru/"><img class="h-adaptive-2 p-1 m-2" src="/assets/images/logo_t1.svg" /></a>
              <a href="https://mephi.ru/"><img class="h-adaptive-2 p-1 m-2" src="/assets/images/logo_mephi.png" /></a>
              <a href="https://rosatom.ru/"><img class="h-adaptive-2 p-1 m-2"
                                                 src="/assets/images/logo_rosatom.svg" /></a>
              <a href="https://priority2030.ru/"><img class="h-adaptive-2 p-1 m-2"
                                                      src="/assets/images/logo_prioritet.png" /></a>
              <a href="https://ismc.mephi.ru/digital_department"><img class="h-adaptive-2 p-1 m-2"
                                                                      src="/assets/images/logo_digcaf.png" /></a>
            </div>
          </div>


          <PlanetRunner class="text-6xl sm:text-6xl md:text-6xl lg:text-7xl xl:text-7xl xxl:text-7_5xl absolute z-0 bottom-0"
                        planet="mars"
                        style="bottom: 30%; left: 95%" text="Ядерный лидер" />
        </div>
      </div>

      <div id="about" class="flex flex-column pt-2 overflow-hidden justify-content-center min-height-100">
        <TextRunner class="text-6xl relative -rotate-90 left-1 xl:left-1 hidden md:block top-1"
                    text="NUCLEAR IT HACK" />

        <div class="relative px-4 md:px-7 xl:pl-9 mt-4 md:mt-0 min-height-100">
          <h1 class="space-font text-primary text-4xl sm:text-5xl md:text-6xl lg:text-7xl mb-4 md:mb-6">О хакатоне</h1>

          <p class="text-white font-light text-3xl md:text-4xl lg:text-5xl mb-4 md:mb-8 line-height-2 font-light">
            В 2024 году хакатон пройдёт в <b>два</b> этапа:
            <br> &ensp; • Отборочный этап (онлайн)
            <br> &ensp; • Финал (очно)
            <br>
            <br>
            <b>Отборочный этап</b> пройдёт с 25 сентября до 2 октября. На этапе участникам будет предложено решить IT&nbsp;задачи
            от крупнейших индустриальных компаний МосТрансПроект, Лоция, МТС&nbsp;Линк, T1.
            <br>
            <br>
            <b>Финальный этап</b> пройдет 17-20 октября в очном формате на базе Образовательного центра «Сириус» (<i>транспортные
            расходы и проживание участникам финала оплачивает университет</i>). В финал выйдут <b>лучшие команды</b>
            отборочного этапа.
            <br>
            <br>
            В финале все команды решают одну задачу.
          </p>

          <div class="grid">
            <div class="col-12 md:col-6 xl:col-3">
              <span
                class="py-2 px-4 text-center text-3xl sm:text-4xl md:text-4xl xl:text-adaptive-1 space-font text-primary-4 block border-1 border-round-3xl border-white text-nowrap">
                2-4 человека
                <br>
                в команде
              </span>
            </div>

            <div class="col-12 md:col-6 xl:hidden">
              <span
                class="py-2 px-4 text-center text-3xl sm:text-4xl md:text-4xl xl:text-adaptive-1 space-font text-primary-4 block border-1 border-round-3xl border-white text-nowrap">
                Лучшие команды
                <br>
                 в финале в сочи
              </span>
            </div>

            <div class="col-12 xl:col">
              <span
                class="py-2 px-4 text-center text-3xl sm:text-4xl md:text-5xl xl:text-adaptive-1 space-font text-primary-4 block border-1 border-round-3xl border-white text-nowrap">
                5 задач на выбор
                <br>
                в отборочном этапе
              </span>
            </div>

            <div class="hidden xl:block xl:col">
              <span
                class="py-2 px-4 text-center text-3xl sm:text-4xl md:text-5xl xl:text-adaptive-1 space-font text-primary-4 block border-1 border-round-3xl border-white text-nowrap">
                Лучшие команды
                <br>
                в финале в сочи
              </span>
            </div>
          </div>
        </div>
      </div>

      <div id="tasks" class="flex flex-column pt-2 overflow-hidden justify-content-center">
        <div class="relative px-4 md:px-7 xl:pl-10 mt-4 md:mt-0 min-height-100 pb-8">
          <h1 class="space-font text-primary text-4xl sm:text-5xl md:text-6xl lg:text-7xl mb-4 md:mb-6">
            Задачи отборочного этапа</h1>

          <p class="text-white font-light text-3xl md:text-4xl lg:text-5xl mb-4 md:mb-8 line-height-2">
            <b>Отборочный этап</b> пройдёт с 25&nbsp;сентября по 2&nbsp;октября.
            <br>
            <br class="sm:hidden">
            Команда может выбрать только одну задачу.
          </p>

          <div class="grid xl:row-gap-4 relative z-2 mb-8">
            <div class="col-12 xl:col-6 xxl:col-4 text-center xl:text-left">
              <div class="xl:w-fit">
                <span class="space-font text-4xl md:text-5xl lg:text-6xl text-primary-4 text-nowrap">
                  Прогноз нагрузки
                  <br>
                  на дороги и метро
                </span>

                <div class="flex justify-content-center xl:justify-content-start mt-2 xl:mt-4">
                  <div class="flex">
                    <div
                      class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 xl:px-3 xxl:px-4 text-center cursor-pointer"
                      @click="aboutPanel_mostrans.toggle">
                      <a class="text-white text-lg md:text-xl lg:text-2xl text-nowrap">О задаче</a>
                    </div>

                    <a class="text-white block ml-4" href="https://mtp.mos.ru/"><img
                      src="/assets/images/logo_mostrans.svg" style="height: 3rem;" /></a>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-12 xl:col-6 xxl:col-4 text-center xl:text-left">
              <div class="xl:w-fit">
                <span class="space-font text-4xl md:text-5xl lg:text-6xl text-primary-4">
                  Геймификация<br>взаимодействия<br>в iT-командах
                </span>

                <div class="flex justify-content-center xl:justify-content-start mt-2 xl:mt-4">
                  <div class="flex">
                    <div
                      class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 xl:px-3 xxl:px-4 text-center cursor-pointer"
                      @click="aboutPanel_locia.toggle">
                      <a class="text-white text-lg md:text-xl lg:text-2xl text-nowrap">О задаче</a>
                    </div>

                    <a class="text-white block ml-4" href="https://www.loodsen.ru/"><img
                      src="/assets/images/logo_locia.png" style="height: 3rem;" /></a>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-12 xl:col-6 xxl:col-4 text-center xl:text-left">
              <div class="xl:w-fit">
                <span class="space-font text-4xl md:text-5xl lg:text-6xl text-primary-4">
                  Table of Content
                </span>

                <div class="flex justify-content-center xl:justify-content-start mt-2 xl:mt-4">
                  <div class="flex">
                    <div
                      class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 xl:px-3 xxl:px-4 text-center cursor-pointer"
                      @click="aboutPanel_t1.toggle">
                      <a class="text-white text-lg md:text-xl lg:text-2xl text-nowrap">О задаче</a>
                    </div>

                    <a class="text-white block ml-4" href="https://t1.ru/"><img src="/assets/images/logo_t1.svg"
                                                                                style="height: 3rem;" /></a>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-12 xl:col-6 xxl:col-4 hidden xxl:block"></div>
            <div class="col-12 xl:col-6 xxl:col-4 text-center xl:text-left">
              <div class="xl:w-fit">
                <span class="space-font text-4xl md:text-5xl lg:text-6xl text-primary-4">
                  Определение<br>лекарства
                </span>

                <div class="flex justify-content-center xl:justify-content-start mt-2 xl:mt-4">
                  <div class="flex">
                    <div
                      class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 xl:px-3 xxl:px-4 text-center cursor-pointer"
                      @click="aboutPanel_mephi.toggle">
                      <a class="text-white text-lg md:text-xl lg:text-2xl text-nowrap">О задаче</a>
                    </div>

                    <a class="text-white block ml-4" href="https://mephi.ru/"><img src="/assets/images/logo_mephi.png"
                                                                                   style="height: 3rem;" /></a>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-12 xl:col-6 xxl:col-4 hidden xl:block xxl:hidden"></div>
            <div class="col-12 xl:col-6 xxl:col-4 text-center xl:text-left">
              <div class="xl:w-fit">
                <span class="space-font text-4xl md:text-5xl lg:text-6xl text-primary-4">
                  Использование ИИ
                  <br>
                  в продукте
                </span>

                <div class="flex justify-content-center xl:justify-content-start mt-2 xl:mt-4">
                  <div class="flex">
                    <div
                      class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 xl:px-3 xxl:px-4 text-center cursor-pointer"
                      @click="aboutPanel_mts.toggle">
                      <a class="text-white text-lg md:text-xl lg:text-2xl text-nowrap">О задаче</a>
                    </div>

                    <a class="text-white block ml-4" href="https://mts-link.ru/">
                      <img src="/assets/images/logo_mts.svg" style="height: 3rem;" />
                    </a>
                  </div>
                </div>
              </div>
            </div>

            <OverlayPanel ref="aboutPanel_mostrans" appendTo="body"
                          class="bg-black border-round-2xl max-w-full md:max-w-30rem"
                          style="max-width: 50%">
              <div class="font-light md:text-lg lg:text-xl text-white p-3 pre mb-2">
                <p>
                  При вводе нового объекта застройки возрастает нагрузка на дорожную сеть и общественный транспорт, так
                  как возрастает количество людей. Имеющиеся станции метро или дороги не смогут пропустить всех и
                  появятся пробки.
                  <br><br>
                  В рамках задачи необходимо разработать продукт (алгоритм + UI), который будет моделировать изменения
                  нагрузки на дорогах и станциях метро при вводе новых объектов застройки, проводить оценку
                  дополнительного спроса и расчёт запаса/дефицита пропускной способности с его учетом.
                  <br><br>
                  Рекомендуемый стек: JavaScript, любой язык программирования для бекенда, база данных PostgreSQL etc.
                  <br><br>
                  <b>Держатель кейса: МосТрансПроект</b>
                </p>
              </div>
            </OverlayPanel>
            <OverlayPanel ref="aboutPanel_locia" appendTo="body"
                          class="bg-black border-round-2xl max-w-full md:max-w-30rem"
                          style="max-width: 50%">
              <div class="font-light md:text-lg lg:text-xl text-white p-3 pre mb-2">
                <p>
                  В современных IT-компаниях эффективная коммуникация и сотрудничество внутри продуктовых команд
                  является ключевым фактором успеха. Однако выполнение рутинных задач со временем может снижать
                  мотивацию и вовлечённость сотрудников. Геймификация способна стать мощным инструментом для повышения
                  вовлечённости, улучшения координации и выполнения задач.
                  <br>
                  <br>
                  В рамках задачи необходимо разработать прототип системы, которая внедрит игровые элементы в рабочие
                  процессы команды и создаст здоровую соревновательную атмосферу как среди отдельных сотрудников, так и
                  между командами (с применением инструментария ИИ).
                  <br>
                  <br>
                  <b>Держатель кейса: Лоция</b>
                </p>
              </div>
            </OverlayPanel>
            <OverlayPanel ref="aboutPanel_t1" appendTo="body"
                          class="bg-black border-round-2xl max-w-full md:max-w-30rem"
                          style="max-width: 50%">
              <div class="font-light md:text-lg lg:text-xl text-white p-3 pre mb-2">
                <p>
                  В рамках задачи предоставлен набор pdf файлов не имеющих table of content (TOC), работа с такими
                  файлами требует значительно больше ручного труда, гораздо удобнее пользоваться ссылками и сразу
                  переходить к странице с нужной темой.
                  <br><br>
                  Задача написать сервис по добавлению TOC в pdf файла. При этом необходимо рассмотреть три кейса:
                  <br>
                  1. TOC уже есть на страницах файла в виде раздела Содержание, но не добавлен в TOC pdf файла.
                  <br>
                  2. На страницах файла нет раздела Содержание, но есть четкое разделение файла на Главы\Разделы по
                  заголовкам
                  <br>
                  3. Файл не имеет выделенных Глав\Разделов, необходимо группировать страницы в разделы по понятному
                  принципу и считать первую страницу группы началом Главы\Раздела.
                  <br>
                  <br>
                  <b>Держатель кейса: IT-холдинг Т1</b>
                </p>
              </div>
            </OverlayPanel>
            <OverlayPanel ref="aboutPanel_mephi" appendTo="body"
                          class="bg-black border-round-2xl max-w-full md:max-w-30rem"
                          style="max-width: 50%">
              <div class="font-light md:text-lg lg:text-xl text-white p-3 pre mb-2">
                <p>
                  Предсказание противовирусной активности соединений - очевидная актуальная задача, которая позволит
                  ускорить создание лекарств, используя современные цифровые инструменты. В рамках задачи необходимо
                  собрать информацию о различных химических соединениях, для которых активность простив одного из
                  вирусов (A/H1N1, SARS-CoV-2, HIV-1) известна, а затем обучить модель для предсказания противовирусной
                  активности.

                  Для сбора <a href="https://www.ebi.ac.uk/chembl/">подходит, например, база.</a>
                  <br>
                  <br>
                  <b>Держатель кейса: Лаборатория Хемоинформатики НИЯУ МИФИ</b>
                </p>
              </div>
            </OverlayPanel>
            <OverlayPanel ref="aboutPanel_mts" appendTo="body"
                          class="bg-black border-round-2xl max-w-full md:max-w-30rem"
                          style="max-width: 50%">
              <div class="font-light md:text-lg lg:text-xl text-white p-3 pre mb-2">
                <p>
                  При проведении опросов важно не только собрать ответы, но и качественно проанализировать их, чтобы
                  понять реальные мотивы и предпочтения людей.
                  <br>
                  <br>
                  Представим, что сотрудники отвечают на вопрос: «Что мотивирует вас работать больше?» Ответы могут быть
                  самыми разными: «команда», «коллеги», «зарплата», «бабосики», «шеф», «атмосфера», «амбициозные задачи»
                  и т.д. Сырые данные зачастую избыточны и включают множество синонимов, просторечий или даже
                  нецензурной лексики.
                  <br>
                  <br>
                  Разработать систему на основе ИИ, которая анализирует список пользовательских ответов возвращает
                  понятное и интерпретируемое облако слов.
                  <br>
                  <br>
                  <b>Держатель кейса: МТС Линк</b>
                </p>
              </div>
            </OverlayPanel>
          </div>

          <PlanetRunner class="text-2xl sm:text-5xl md:text-6xl lg:text-7xl xl:text-7xl xxl:text-7_5xl absolute z-0 bottom-0"
                        planet="earth"
                        style="left: 3%" text="Больше чем ядерный" />
        </div>
      </div>

      <div id="faq"
           class="flex flex-column pt-2 overflow-hidden justify-content-center">
        <div class="relative px-4 md:px-7 xl:pl-8 mt-4 md:mt-0 min-height-100 pb-8">
          <h1 class="space-font text-primary text-4xl sm:text-5xl md:text-6xl lg:text-7xl mb-4">
            Ответы на вопросы</h1>

          <div class="sm:max-width-60 mb-5">
                <span class="space-font text-4xl md:text-5xl lg:text-6xl text-primary-4">
                  Как выбрать задачу?
                </span>

            <div class="flex text-center xl:text-left mt-2 xl:mt-3">
              <div class="flex">
                <div class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 text-center">
                  <p class="text-white text-lg md:text-xl lg:text-2xl text-left font-light ml-4 line-height-2">
                    • Для студентов НИЯУ МИФИ (г. Москва) необходимо авторизоваться через личный кабинет на
                    home.mephi.ru в личном кабинете конкурса “Студент Года” и нажать “Участвовать”.
                    <br>
                    • Для студентов филиалов НИЯУ МИФИ необходимо нажать кнопку “Участвовать” и заполнить анкету
                    участника.
                  </p>
                  <p class="text-white text-lg md:text-xl lg:text-2xl text-left font-light">
                    Регистрируется каждый участник команды, если команда уже сформирована.
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div class="sm:max-width-60 mb-5">
                <span class="space-font text-3xl lg:text-4xl text-white line-height-1">
                  Могут ли студенты московской площадки участвовать в одном составе со студентами из филиала?
                </span>

            <div class="flex text-center xl:text-left mt-2 xl:mt-3">
              <div class="flex">
                <div class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 text-center">
                  <p class="text-white text-lg md:text-xl lg:text-2xl text-left font-light line-height-2">
                    Да, могут
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div class="sm:max-width-60 mb-5">
                <span class="space-font text-3xl lg:text-4xl text-white line-height-1">
                  До какого числа можно регистрироваться?
                </span>

            <div class="flex text-center xl:text-left mt-2 xl:mt-3">
              <div class="flex">
                <div class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 text-center">
                  <p class="text-white text-lg md:text-xl lg:text-2xl text-left font-light line-height-2">
                    До 2 октября, т.е. до окончания отборочного этапа. Но чем позже зарегистрироваться, тем меньше
                    времени на решение останется.
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div class="sm:max-width-60 mb-5">
                <span class="space-font text-3xl lg:text-4xl text-white line-height-1">
                  Будет ли постановка задач от экспертов?
                </span>

            <div class="flex text-center xl:text-left mt-2 xl:mt-3">
              <div class="flex">
                <div class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 text-center">
                  <p class="text-white text-lg md:text-xl lg:text-2xl text-left font-light line-height-2">
                    Да, 25 сентября. Время и ссылка для подключения будут позже.
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div class="sm:max-width-60 mb-5">
                <span class="space-font text-3xl lg:text-4xl text-white line-height-1">
                  Могу я участвовать один?
                </span>

            <div class="flex text-center xl:text-left mt-2 xl:mt-3">
              <div class="flex">
                <div class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 text-center">
                  <p class="text-white text-lg md:text-xl lg:text-2xl text-left font-light line-height-2">
                    Нет, команды должны содержать от 2 до 4 участников
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div class="sm:max-width-60 mb-8 md:mb-0">
                <span class="space-font text-3xl lg:text-4xl text-white line-height-1">
                  Что делать, если у меня остались вопросы?
                </span>

            <div class="flex text-center xl:text-left mt-2 xl:mt-3">
              <div class="flex">
                <div class="w-fit border-round-2xl border-1 border-white-alpha-90 py-3 px-4 text-center">
                  <p class="text-white text-lg md:text-xl lg:text-2xl text-left font-light line-height-2">
                    Пишите в бот: <a class="text-white" href="https://t.me/Best_MEPHI_student_bot">@Best_MEPHI_student_bot</a>
                  </p>
                </div>
              </div>
            </div>
          </div>

          <PlanetRunner class="text-2xl sm:text-5xl md:text-6xl lg:text-7xl xl:text-7xl xxl:text-7_5xl absolute z-0 bottom-0"
                        planet="neptune"
                        style="left: 89%" text="Quantum Code Master" />
        </div>
      </div>

      <div id="contacts" class="flex flex-column pt-2 overflow-hidden justify-content-center">
        <div class="relative px-4 md:px-7 xl:pl-8">
          <h1 class="space-font text-primary text-4xl sm:text-5xl md:text-6xl lg:text-7xl mb-4 md:mb-6">Контакты</h1>

          <div class="field mb-5 text-nowrap mt-5 flex flex-wrap">
            <a
              class="border-round-3xl border-1 mr-2 md:mr-3 lg:mr-4 py-1 px-3 bg-primary-gradient text-white border-white-alpha-90 flex justify-content-center align-items-center mb-3 sm:mb-4 lg:mb-5 text-xl md:text-2xl lg:text-3xl"
              href="https://t.me/student_of_year">
              <img class="mr-2 p-0" src="/assets/images/logo_tg_alt.svg" style="height: 2em;" />Telegram-канал
            </a>

            <a
              class="border-round-3xl border-1 mr-2 md:mr-3 lg:mr-4 py-1 px-3 bg-primary-gradient text-white border-white-alpha-90 flex justify-content-center align-items-center mb-3 sm:mb-4 lg:mb-5 text-xl md:text-2xl lg:text-3xl"
              href="https://t.me/Best_MEPHI_student_bot">
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

  <Dialog v-model:visible="showDialog" closable header="Заявка на участие" modal style="min-width: 40%">
    <div class="">
      <p v-if="authed" class="mb-4">
        Персональные данные можно изменить в <b><a href="/participant/me">личном кабинете</a></b>.
      </p>
      <p v-else class="mb-4">
        Если вы студент НИЯУ МИФИ, г. Москва, необходимо <b><a
        v-tooltip.bottom="'Только для студентов НИЯУ МИФИ г. Москва'"
        :href="'https://auth.mephi.ru/login?service=' + encodeURI(window.$frontHost)">войти</a></b>
        в аккаунт перед заполнением формы.
      </p>

      <div class="field flex-auto">
        <label class="mb-0 block">Имя</label>
        <InputText v-model.trim="request.firstName" :disabled="authed" class="w-full" max-length="100" required />
      </div>
      <div class="field flex-auto">
        <label class="mb-0 block">Фамилия</label>
        <InputText v-model.trim="request.secondName" :disabled="authed" class="w-full" max-length="100" required />
      </div>
      <div class="field flex-auto">
        <label class="mb-0 block">Отчество</label>
        <InputText v-model.trim="request.thirdName" :disabled="authed" class="w-full" max-length="100" required />
      </div>

      <div class="field flex-auto">
        <label class="mb-0 block">Филиал</label>
        <Dropdown v-model.trim="request.filial_" :disabled="authed" :options="filials"
                  class="w-full" option-label="name" required />
      </div>
      <div v-if="request?.filial_?.name === 'Другой'" class="field flex-auto">
        <label class="mb-0 block">Учебное учреждение</label>
        <InputText v-model.trim="request.filial" :disabled="authed" class="w-full" max-length="100" minlength="1"
                   required />
      </div>
      <div class="field flex-auto">
        <label class="mb-0 block">Группа</label>
        <InputText v-model.trim="request.groupTitle" :disabled="authed" class="w-full" max-length="10" minlength="1"
                   required />
      </div>

      <div class="field flex-auto">
        <label class="mb-0 block">Ник в Телеграм</label>
        <InputText v-model.trim="request.tg" :disabled="authed" :placeholder="authed ? '' : 'myusername'"
                   class="w-full" max-length="255" required />
      </div>
      <div class="field flex-auto">
        <label class="mb-0 block">Почта</label>
        <InputText v-model.trim="request.email" class="w-full" max-length="255" placeholder="example@post.com"
                   required />
      </div>

      <div class="field flex-auto">
        <label class="mb-0 block">Задача</label>
        <Dropdown v-model.trim="request.task" :options="tasks"
                  class="w-full" option-label="name" required />
      </div>
      <div class="field flex-auto">
        <label class="mb-0 block">Название команды</label>
        <InputText v-model.trim="request.commandTitle" class="w-full" max-length="255" required />
      </div>

      <Button class="w-full mt-2 " @click="submitRequest">
        Отправить заявку
      </Button>
    </div>
  </Dialog>

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
  min-height: 100dvh;
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
  top: min(20vh, max(30%, 8rem));
}

.left-1 {
  left: calc(-50% + 1rem);
}

.left-2 {
  text-wrap: nowrap;
  left: 35%;
}

.bottom-1 {
  top: calc(100% - 12rem);
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

.h-adaptive-2 {
  height: clamp(3rem, 3vw, 5rem) !important;
}

.max-width-60 {
  max-width: 60%;
}

@media screen and (min-width: 576px) {
  .sm\:col-10-2 {
    flex: 0 0 auto;
    padding: 0.5rem;
    width: 20%;
  }

  .sm\:top-5 {
    top: 5%;
  }

  .sm\:top-10 {
    top: 10%;
  }

  .sm\:top-20 {
    top: 20%;
  }

  .sm\:top-30 {
    top: 30%;
  }

  .sm\:max-width-60 {
    max-width: 60%;
  }
}

@media screen and (min-width: 992px) {
  .lg\:max-w-70 {
    max-width: 70%;
  }

  .lg\:bottom-1 {
    top: calc(100% - 15rem);
  }

  .lg\:no-before::before {
    display: none !important;
  }
}


@media screen and (min-width: 1200px) {
  .xl\:left-1 {
    left: calc(-50% + 3rem);
  }

  .xl\:pl-9 {
    padding-left: 9rem !important;
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

  .xl\:text-adaptive-1 {
    font-size: min(2vw, 3rem) !important;
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

  .xxl\:text-9xl {
    font-size: 7.3rem !important;
  }

  .xxl\:text-10xl {
    font-size: 9rem !important;
  }

  .xxl\:col-4 {
    flex: 0 0 auto;
    padding: 0.5rem;
    width: 33.3333%;
  }

  .xxl\:col-8 {
    flex: 0 0 auto;
    padding: 0.5rem;
    width: 66.6667%;
  }

  .xxl\:block {
    display: block !important;
  }

  .xxl\:text-xl {
    font-size: 1.25rem !important;
  }
  .xxl\:text-2xl {
    font-size: 1.5rem !important;
  }
  .xxl\:text-3xl {
    font-size: 1.75rem !important;
  }
  .xxl\:text-4xl {
    font-size: 2rem !important;
  }
  .xxl\:text-5xl {
    font-size: 2.5rem !important;
  }
  .xxl\:text-6xl {
    font-size: 3rem !important;
  }
  .xxl\:text-7xl {
    font-size: 4rem !important;
  }
  .xxl\:text-7_5xl {
    font-size: 5rem !important;
  }
  .xxl\:text-8xl {
    font-size: 6rem !important;
  }
}
</style>