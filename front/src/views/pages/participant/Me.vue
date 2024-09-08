<script setup>

import { onBeforeMount, ref } from 'vue';
import { MeService } from '@/service/participant/MeService';
import { useToast } from 'primevue/usetoast';
import { ToastService } from '@/service/util/ToastService';
import { FileService } from '@/service/admin/FileService';
import { findIndexById } from '@/service/util/UtilsService';
import { DateTimeService } from '@/service/util/DateTimeService';
import { ParticipantStateService } from '@/service/util/ParticipantStateService';
import router from '@/router';

const toast = useToast();

const toastService = new ToastService(toast);
const meService = new MeService();
const fileService = new FileService();
const dateTimeService = new DateTimeService();
const participantStateService = new ParticipantStateService();

onBeforeMount(() => {
  meService.getMe()
    .then(me => {
      if (!toastService.isServerError(me)) {
        user.value = me;
        setAvatar();
      }
    });
});

const user = ref(null);
const avatarImg = ref(null);

const setAvatar = () => {
  if (user.value?.avatarFileId) {
    fileService.getFileUrl(user.value.avatarFileId)
      .then(url => {
        if (avatarImg.value) {
          avatarImg.value.src = url;
          avatarImg.value.onload = () => URL.revokeObjectURL(url);
        }
      });
  }
};

const trimHttp = (str) => {
  if (str == null) return '';

  return str.replace('http://', '').replace('https://', '');
};

const trimVk = (str) => {
  if (str == null) return '';

  const ret = trimHttp(str).replace('vk.com/', '').replace('vk.ru/', '');
  if (ret[0] === '@') return ret.slice(1);
  return ret;
};

const trimTg = (str) => {
  if (str == null) return '';

  const ret = trimHttp(str).replace('t.me/', '');
  if (ret[0] === '@') return ret.slice(1);
  return ret;
};

const trimPhone = (str) => {
  if (str == null) return '';

  return str.replaceAll(' ', '').replaceAll('+', '').replaceAll('(', '').replaceAll(')', '').replaceAll('-', '');
};

const save = () => {
  if (isSaving.value) return;

  if (isNew.value && (user.value.groupTitle === null || user.value.groupTitle === undefined || user.value.groupTitle.trim() === '')) {
    toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Необходимо указать группу', life: 3000 });
    return;
  }

  submitted.value = true;
  isSaving.value = true;
  user.value = {
    ...user.value,
    vk: trimVk(user.value.vk),
    tg: trimTg(user.value.tg)
  };

  meService.updateMe(user.value).then((bool) => {
    if (bool) {
      localStorage.fio = `${user.value.secondName} ${user.value.firstName[0]}.${
        user.value.thirdName && user.value.thirdName !== '' ? ' ' + user.value.thirdName[0] + '.' : ''}`;
      localStorage.fullName = `${user.value.secondName} ${user.value.firstName} ${user.value.thirdName}`.trim();
      localStorage.isNew = false;
      isNew.value = false;
      isEditing.value = false;
    } else {
      toast.add({
        severity: 'error',
        summary: 'Ошибка',
        detail: 'Данные не сохранены, обновите страницу или повторите вход в аккаунт.',
        life: 3000
      });
    }
  }).then(() => setAvatar())
    .finally(() => {
      isSaving.value = false;
      submitted.value = false;
    });
}

const isEditing = ref(false);
const isSaving = ref(false);
const submitted = ref(false);
const credId = ref(localStorage.cred_id);
const isParticipantCreds = ref(localStorage.role === 'PARTICIPANT');
const isNew = ref(localStorage.isNew === 'true');
const canEditName = ref((localStorage.role === 'EXPERT' && isNew) || localStorage.role === 'ADMIN');

const uploader = async (event) => {
  fileService.upload(event.files[0])
    .then(res => {
      if (!toastService.isServerError(res)) {
        user.value.avatarFileId = res.id;
      }
    });
};

const videoDto = ref({ url: null, fileId: null });
const isUploading = ref(false);

const uploaderVideo = async (event) => {
  if (isUploading.value) return;
  videoDto.value.fileId = '';
  isUploading.value = true;
  fileService.upload(event.files[0])
    .then(res => {
      if (!toastService.isServerError(res)) {
        videoDto.value.fileId = res.id;
      }
    }).finally(() => {
    isUploading.value = false;
  });
};

const submitVideo = () => {
  meService.uploadVideo(videoDto.value)
    .then((res) => {
      if (!toastService.isServerError(res)) {
        user.value.appliedStages[findIndexById(user.value.appliedStages, 6)].additionalInfo = 'sent';
        videoDto.value = {};
      }
    })
    .catch(() => toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Видео не загружено', life: 3000 }));
};

const chooseDictantDate = (date) => {
  meService.chooseDictantDate(date)
    .then((res) => {
      if (!toastService.isServerError(res)) {
        user.value.appliedStages[findIndexById(user.value.appliedStages, 7)].additionalInfo = date === 1 ? '05.09.2024 18:00' : '11.09.2024 18:00';
      }
    })
    .catch(() => toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Видео не загружено', life: 3000 }));
};

const getMyState = () => {
  return participantStateService.getContentFor(user.value.state);
};

const particlesOption = ref({
  fpsLimit: 24,
  fullScreen: false,
  detectRetina: true,
  particles: {
    color: { value: '#ffffff' },
    move: { direction: 'none', enable: true, outModes: 'bounce', random: true, speed: 0.3, straight: false },
    number: {
      density: { enable: true, area: 200 },
      value: 8000
    },
    opacity: {
      animation: { enable: true, speed: 0.05, startValue: 'random', sync: false, count: 250, destroy: 'none' },
      value: { min: 0, max: 0.3 }
    },
    shape: { type: 'circle' },
    size: { value: { min: 0.2, max: 2 } }
  }
});

const uploadVideoPanel = ref(null);
const chooseDictantDatePanel = ref(null);
const chooseWireparkDatePanel = ref(null);
const wireparkDates = ref(null);

const openWireparkDatePanel = (event) => {
  let isNew = false;
  if (wireparkDates.value === null) {
    isNew = true;
    wireparkDates.value = [];
  }

  if (isNew) {
    meService.getWireparkDates()
      .then(res => {
        if (!toastService.isServerError(res)) {
          wireparkDates.value = Object.entries(res)
            .sort(([key1, value1], [key2, value2]) => {
              return key1.localeCompare(key2);
            })
            .map(([key, value]) => {
              return {
                date: dateTimeService.translateLocalDateToDayMonth(key),
                array: value.map(schedule => {
                  return {
                    ...schedule,
                    start: dateTimeService.getOnlyHoursMinutes(dateTimeService.getDateFromTimestamp(schedule.start)),
                    end: dateTimeService.getOnlyHoursMinutes(dateTimeService.getDateFromTimestamp(schedule.end))
                  };
                })
              };
            });
        }
      });
  }
  chooseWireparkDatePanel.value.toggle(event);
};

const chooseWireparkDate = (schedule_id, event) => {
  event.preventDefault();

  meService.chooseWireparkDate(schedule_id)
    .then((res) => {
      if (!res || res.err) {
        toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Не удалось выбрать дату', life: 3000 });
      } else {
        chooseWireparkDatePanel.value.toggle();
        router.go(0);
      }
    })
    .catch(() => toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Не удалось выбрать дату', life: 3000 }));
};
</script>

<template>
  <div v-if="user" class="grid px-10 sm:p-10 md:p-10 lg:p-10 xl:p-10 xxl:p-10">
    <div class="col-12 md:col-5 xl:col-4 xxl:col-3 gap-4">
      <div class="grid">
        <div class="col-12">
          <div v-if="!isEditing" class="card">
            <div class="flex -mt-4 -ml-4 absolute">
              <button class="p-link layout-topbar-button" @click="isEditing = true">
                <i class="pi pi-user-edit text-2xl"></i>
              </button>
            </div>

            <div class="flex justify-content-center mb-3">
              <div class="cropper">
                <img ref="avatarImg" alt="Avatar" src="/assets/images/avatar.png" />
              </div>
            </div>

            <div class="text-center mb-4 md:mb-5">
              <span
                class="font-semibold text-2xl sm:text-3xl md:text-4xl lg:text-5xl line-height-1">{{ user.fullName
                }}</span>
              <br>
              <span
                class="font-semibold text-2xl sm:text-3xl md:text-4xl mb-1">{{ user.groupTitle ? user.groupTitle : 'Без группы'
                }}</span>
            </div>

            <span v-if="isNew && isParticipantCreds" class="text-primary-colors-2 mb-4 inline-block">
              Укажите группу до начала работы с личным кабинетом! Учетные записи без группы считаются недействительными.
            </span>
            <span v-if="!isNew && isParticipantCreds && !user.groupTitle"
                  class="text-primary-colors-2 mb-4 inline-block">
              Вы не указали группу до начала работы с личным кабинетом! Учетные записи без группы считаются недействительными. Сообщите организаторам ваш ID: {{ credId
              }}.
            </span>

            <div>
              <div class="flex align-items-center mb-2">
                <img alt="Phone logo" class="mr-2" height="20" src="/assets/images/logo_vk.svg" />
                <a v-if="user.vk" :href="'https://vk.com/' + user.vk">vk.com/{{ user.vk }}</a>
                <span v-else>Не указан</span>
              </div>
              <div class="flex align-items-center mb-2">
                <img alt="Phone logo" class="mr-2" height="20" src="/assets/images/logo_tg.svg" />
                <a v-if="user.tg" :href="'https://t.me/' + user.tg">@{{ user.tg }}</a>
                <span v-else>Не указан</span>
              </div>
              <div class="flex align-items-center mb-2">
                <img alt="Phone logo" class="mr-2" height="20" src="/assets/images/logo_phone.svg" />
                <a v-if="user.phone" :href="'tel:+' + trimPhone(user.phone)">{{ user.phone }}</a>
                <span v-else>Не указан</span>
              </div>
            </div>
          </div>

          <div v-else class="p-fluid card">
            <div class="flex -mt-4 -ml-4 mb-3">
              <button :disabled="isSaving" class="p-link layout-topbar-button" @click="save">
                <i class="pi pi-save text-2xl"></i>
              </button>
            </div>

            <div class="field">
              <label class="mb-0">Сменить изображение профиля</label>
              <FileUpload :maxFileSize="5 * (8 * 1024 * 1024)" :multiple="false"
                          accept="image/png, image/jpg, image/jpeg, .pdf" cancel-label="Отмена"
                          choose-label="Выбрать файл" customUpload
                          mode="basic" name="file" upload-label="Импортировать" @uploader="uploader" />
            </div>

            <div class="field">
              <label class="mb-0">Имя</label>
              <InputText v-model.trim="user.firstName" :disabled="!canEditName" max-length="100" />
            </div>
            <div class="field">
              <label class="mb-0">Фамилия</label>
              <InputText v-model.trim="user.secondName" :disabled="!canEditName" max-length="100" />
            </div>
            <div class="field">
              <label class="mb-0">Отчество</label>
              <InputText v-model.trim="user.thirdName" :disabled="!canEditName" max-length="100" />
            </div>

            <div class="field">
              <label class="mb-0">Группа</label>
              <InputText v-model.trim="user.groupTitle" :disabled="!isNew" max-length="10" required />
            </div>

            <div class="field">
              <img alt="Vk logo" class="-mb-1 mr-1" height="20" src="/assets/images/logo_vk.svg" />
              <label class="mb-0">Ник или id ВКонтакте</label>
              <InputText v-model.trim="user.vk" max-length="255" placeholder="id123456789" />
            </div>
            <div class="field">
              <img alt="Vk logo" class="-mb-1 mr-1" height="20" src="/assets/images/logo_tg.svg" />
              <label class="mb-0">Ник в Телеграм</label>
              <InputText v-model.trim="user.tg" max-length="255" placeholder="myusername" />
            </div>
            <div class="field">
              <img alt="Phone logo" class="-mb-1 mr-1" height="20" src="/assets/images/logo_phone.svg" />
              <label class="mb-0">Номер телефона</label>
              <InputMask v-model="user.phone" mask="+7 (999) 999-99-99" placeholder="+7 (123) 456-78-90" />
            </div>
          </div>
        </div>
        <div class="col-12">
          <div class="card bg-black-alpha-90 relative overflow-hidden">
            <vue-particles id="tsparticles" :options="particlesOption" class="absolute h-full w-full -ml-5 -mt-5" />

            <div class="relative">
              <table class="total-table">
                <tr>
                  <td class="undersquare-4-alt">Этапы</td>
                  <td>{{ user.appliedStages.length }}/9</td>
                </tr>
                <tr>
                  <td class="undersquare-2">Рейтинг</td>
                  <td>{{ user.place ?? '??' }}/{{ user.lastPosition }}</td>
                </tr>
                <tr>
                  <td class="undersquare-1">Баллы</td>
                  <td>{{ user.score ?? '??' }}/{{ user.maximumScore }}</td>
                </tr>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="col-12 md:col-7 xl:col-8 xxl:col-9 gap-4">
      <p class="space-font text-2xl md:text-3xl lg:text-4xl xl:text-5xl text-primary-colors-1">Мои этапы</p>

      <div v-if="user.appliedStages.length === 0" class="font-light text-xl lg:text-2xl">
        <p class="mb-6">
          Пока вы не приняли участие ни в одном из этапов.
          <br><br>
          Для участия в Конкурсе «Студент года НИЯУ МИФИ 2024» участнику необходимо принять участие <b>минимум в
          одном</b> из этапов Конкурса.
        </p>
      </div>

      <div v-else class="mb-6">
        <p
          class="inline-block border-round-2xl -mt-4 py-2 px-4 bg-primary-gradient text-white space-font text-2xl md:text-3xl">
          {{ getMyState() }}
        </p>

        <div class="grid m-0">
          <template v-for="stage in user.appliedStages">
            <div class="col-12 lg:col-6 xxl:col-4 pl-0">
              <div class="card py-1 font-semibold h-full">
                <p class="">{{ stage.title }}</p>

                <p v-if="stage.id === 4" class="-mt-3">
                  <span v-if="stage.additionalInfo === 'multiple'"
                        class="text-red-600">Ошибка: Выбрано больше одной даты!</span>
                  <span v-else-if="stage.additionalInfo !== null" class="text-green-600"
                        @click="openWireparkDatePanel">{{ stage.additionalInfo }}</span>
                  <span v-else class="text-primary-colors-4 cursor-pointer"
                        @click="openWireparkDatePanel">Выбрать дату</span>
                </p>

                <p v-else-if="stage.id === 5" class="-mt-3"><a href="https://t.me/run_mephi_bot">ТГ-бот для отчетов</a>
                </p>

                <p v-else-if="stage.id === 6" class="-mt-3">
                  <span v-if="stage.additionalInfo === 'multiple'"
                        class="text-red-600">Ошибка: Указано больше одного видео!</span>
                  <span v-else-if="stage.additionalInfo === 'sent'" class="text-green-600">Видео отправлено</span>
                  <span v-else class="text-primary-colors-4 cursor-pointer" @click="uploadVideoPanel.toggle">Загрузить видео</span>
                </p>

                <p v-else-if="stage.id === 7" class="-mt-3">
                  <span v-if="stage.additionalInfo === 'multiple'"
                        class="text-red-600">Ошибка: Выбрано больше одной даты!</span>
                  <span v-else-if="stage.additionalInfo !== null" @click="chooseDictantDatePanel.toggle"
                        class="text-green-600">{{ stage.additionalInfo }}</span>
                  <span v-else class="text-primary-colors-4 cursor-pointer" @click="chooseDictantDatePanel.toggle">Выбрать дату</span>
                </p>

                <p v-else-if="stage.id === 8" class="-mt-3"><a href="https://it.mephi.ru/webform/1970">Загрузить проект
                  или идею</a></p>

                <a v-if="stage.protocol" :href="window.$apiHost + '/file/' + stage.protocol"
                   class="flex align-items-center -mt-3 mb-4">
                  <i class="text-primary-colors-4 pi pi-file-pdf text-2xl mr-1 font-100"></i>
                  <span class="text-primary">Протокол этапа</span>
                </a>
                <a v-else class="flex align-items-center -mt-3 mb-4">
                  <i class="text-primary-colors-1 pi pi-file text-xl lg:text-2xl mr-1 font-100"></i>
                  <span class="text-primary-colors-1">Протокол не готов</span>
                </a>
                <div>
                  <span class="mr-4">Баллы за этап</span>
                  <div class="inline-block absolute text-center" style="margin-top: -2.55em;">
                    <img class="w-full" src="/assets/images/atom_alt.svg" />
                    <div class="absolute top-50 left-50" style="transform: translate(-50%, -50%)">
                      <span class="text-2xl text-primary-colors-1">{{ stage.score ?? '??' }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>

      <a v-if="user.appliedStages.length !== 9"
         class="font-light text-lg sm:text-xl text-nowrap lg:text-2xl mb-4 text-primary-colors-1 border-round-2xl border-2 border-primary-colors-1 px-3 py-2"
         href="/#stages">Зарегистрироваться на этапы</a>
    </div>

    <OverlayPanel ref="uploadVideoPanel" appendTo="body" class="border-round-2xl max-w-full md:max-w-30rem"
                  style="max-width: 50%">
      <div class="md:text-lg lg:text-xl p-3">
        <div class="field">
          <label class="mb-0">Загрузите файл до 200 Мб</label>
          <FileUpload v-if="!isUploading" :maxFileSize="201 * (8 * 1024 * 1024)" :multiple="false"
                      accept=".mp4, .mov, .wmv, .webm, .avi" cancel-label="Отмена" choose-label="Выбрать файл"
                      customUpload
                      mode="basic" name="file" upload-label="Загрузить" @uploader="uploaderVideo" />
          <ProgressSpinner v-else :style="{ width: '25px', height: '25px' }" class="block" stroke-width="10" />
        </div>
        <div class="field">
          <label class="mb-0">Или предоставьте ссылку</label>
          <InputText v-model.trim="videoDto.url" placeholder="https://ya.disk/..." />
        </div>
        <div class="field">
          <Button :disabled="isUploading || videoDto.url == null && videoDto.fileId == null" @click="submitVideo">
            Отправить
          </Button>
        </div>
      </div>
    </OverlayPanel>

    <OverlayPanel ref="chooseDictantDatePanel" appendTo="body" class="border-round-2xl max-w-full md:max-w-30rem"
                  style="max-width: 50%">
      <div class="md:text-lg lg:text-xl p-3">
        <label>Выберите удобную дату</label>
        <div class="field mt-2">
          <Button class="w-full text-center" outlined @click="chooseDictantDate(1)">05 сентября в 18:00</Button>
        </div>
        <div class="field">
          <Button class="w-full text-center" outlined @click="chooseDictantDate(2)">11 сентября в 18:00</Button>
        </div>
      </div>
    </OverlayPanel>

    <OverlayPanel ref="chooseWireparkDatePanel" appendTo="body" class="border-round-2xl max-w-full md:max-w-30rem mx-3">
      <div class="md:text-lg lg:text-xl p-3">
        <label>Выберите день и удобное время</label>

        <div class="m-0 mt-2 gap-3 text-nowrap">
          <template v-for="day in wireparkDates">
            <div class="field">
              <div class="border-round-2xl border-primary-500 border-1 p-1 m-0 text-center text-lg cursor-pointer"
                   @click="day.show = !day.show">
                <div class="text-center">
                  <span class="block p-1 px-3">{{ day.date }}</span>

                  <div v-if="day.show" class="p-2 pb-1 w-full flex justify-content-center flex-wrap gap-2 mb-2">
                    <template v-for="schedule in day.array">
                      <div>
                        <Button class="block" outlined @click="(event) => chooseWireparkDate(schedule.id, event)">
                          {{ schedule.start }}—{{ schedule.end }}
                        </Button>
                      </div>
                    </template>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </OverlayPanel>
  </div>
</template>

<style lang="scss">
.font-100 {
  font-weight: 100;
}

table.total-table {
  border-collapse: collapse;
  width: 100%;

  tr:first-child td {
    border-top: 0;
  }

  tr:last-child td {
    border-bottom: 0;
  }

  tr td:first-child {
    border-left: 0;
    text-align: left;
  }

  tr td:last-child {
    border-right: 0;
    text-align: center;
  }

  td {
    border: 1px solid var(--gray-400);
    padding: 1rem;
    font-family: "Space Ranger", sans-serif;
    font-size: 1.5rem;
    color: white;
  }
}

.cropper {
  width: 210px;
  aspect-ratio: 1;
  max-height: 210px;
  position: relative;
  overflow: hidden;
  border-radius: 50%;

  img {
    display: inline;
    margin: 0 auto;
    height: 100%;
    width: auto;
  }
}

.px-10 {
  padding-left: 0;
  padding-right: 0;
}

@media screen and (min-width: 576px) {
  .sm\:p-10 {
    padding-left: 0;
    padding-right: 0;
  }
}

@media screen and (min-width: 768px) {
  .md\:p-10 {
    padding-left: 0;
    padding-right: 0;
  }
}

@media screen and (min-width: 992px) {
  .lg\:p-10 {
    padding-left: 6rem;
    padding-right: 4rem;
  }
}

@media screen and (min-width: 1200px) {
  .xl\:p-10 {
    padding-left: 12rem;
    padding-right: 7rem;
  }
}

@media screen and (min-width: 1400px) {
  .xxl\:p-10 {
    padding-left: 15rem;
    padding-right: 10rem;
  }

  .xxl\:col-3 {
    flex: 0 0 auto;
    padding: 0.5rem;
    width: 25%;
  }

  .xxl\:col-4 {
    flex: 0 0 auto;
    padding: 0.5rem;
    width: 33.333333%;
  }

  .xxl\:col-9 {
    flex: 0 0 auto;
    padding: 0.5rem;
    width: 75%;
  }
}

.layout-topbar-button {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  position: relative;
  color: var(--text-color-secondary);
  border-radius: 50%;
  width: 3rem;
  height: 3rem;
  cursor: pointer;
  transition: background-color .2s;

  &:hover {
    color: var(--text-color);
    background-color: var(--surface-hover);
  }

  i {
    font-size: 1.5rem;
  }

  span {
    font-size: 1rem;
    display: none;
  }
}
</style>
