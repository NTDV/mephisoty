<script setup>
import {ref, onMounted} from 'vue';
import {useToast} from 'primevue/usetoast';
import {AllowStateService} from '@/service/AllowStateService';
import {DateTimeService} from '@/service/DateTimeService';
import {useRoute} from "vue-router";
import {CredsService} from "@/service/CredsService";
import {StageService} from "@/service/StageService";
import SelectIdByTitleBlock from "@/components/prefab/SelectIdByTitleBlock.vue";
import {SeasonService} from "@/service/SeasonService";

const toast = useToast();

const loading = ref(false);

const dt = ref(null);

const seasonService = new SeasonService();
const stageService = new StageService();
const allowStateService = new AllowStateService();
const dateTimeService = new DateTimeService();
const credsService = new CredsService();

const model = ref({});
const calendarStartValue = ref(null);
const calendarEndValue = ref(null);
const modelDialog = ref(false);
const deleteModelDialog = ref(false);

const submitted = ref(false);

const statuses = ref(allowStateService.getBadgeContentViewOnly());
const allSeasons = ref([]);

const route = useRoute();

onMounted(() => {
  loading.value = true;
  //seasonService.getAllForSelect().then((data) => allSeasons.value = data);
  stageService.get(route.params.id).then((data) => {
    model.value = createModelClient(data);
    const creatorIsEditor = model.value.createdBy === model.value.modifiedBy;

    credsService.getFullName(model.value.createdBy).then((data) => {
      if (!data.err) model.value.createdBy = data;
      else console.error(data);

      if (creatorIsEditor) {
        model.value.modifiedBy = data;
        loading.value = false;
      }
    });

    if (!creatorIsEditor) {
      credsService.getFullName(model.value.modifiedBy).then((data) => {
        if (!data.err) model.value.lastModifiedBy = data;
        else console.error(data);

        loading.value = false;
      });
    }
  });
});

const createModelClient = (modelServer) => {
  calendarStartValue.value = dateTimeService.getDateFromTimestamp(modelServer.start);
  calendarEndValue.value = dateTimeService.getDateFromTimestamp(modelServer.end);

  return {
    ...modelServer,
    createdAt: dateTimeService.getDateFromTimestamp(modelServer.createdAt),
    modifiedAt: dateTimeService.getDateFromTimestamp(modelServer.modifiedAt),
    start: calendarStartValue.value,
    end: calendarEndValue.value
  };
};


const hideDialog = () => {
  modelDialog.value = false;
  submitted.value = false;
};

const validateInput = () => {
  return (
    model.value &&
    //model.value.comment &&
    model.value.title &&
    model.value.title.trim() !== '' &&
    //model.value.description &&
    //model.value.rules &&
    calendarStartValue.value &&
    calendarEndValue.value &&
    // && model.value.modelResultFormula
    model.value.modelVisibility &&
    model.value.scoreVisibility &&
    model.value.scheduleAccessState
  );
};

const createModelDto = () => {
  return {
    ...model.value,

    comment: model.value.comment ? model.value.comment : '',
    description: model.value.description ? model.value.description : '',
    rules: model.value.rules ? model.value.rules : '',
    start: dateTimeService.getDateTimeOffsetFromDate(calendarStartValue.value),
    end: dateTimeService.getDateTimeOffsetFromDate(calendarEndValue.value),
  };
};

const saveModel = async () => {
  submitted.value = true;

  if (validateInput()) {
    if (model.value.id) {
      try {
        const res = await stageService.edit(model.value.id, createModelDto());
        if (res.err) {
          console.error(res);
          toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Данные не изменены', life: 3000});
          return;
        }

        model.value = createModelClient(res);
        model.value[findModelIndexById(model.value.id)] = model.value;
        toast.add({severity: 'success', summary: 'Успешно', detail: 'Данные изменен', life: 3000});
      } catch (e) {
        console.error(e);
        toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Данные не изменены', life: 3000});
        return;
      }
    } else {
      try {
        const res = await stageService.create(createModelDto());
        if (res.err) {
          console.error(res);
          toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Данные не сохранены', life: 3000});
          return;
        }

        model.value = createModelClient(res);
        models.value.push(model.value);
        toast.add({severity: 'success', summary: 'Успешно', detail: 'Данные сохранены', life: 3000});
      } catch (e) {
        console.error(e);
        toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Данные не сохранены', life: 3000});
        return;
      }
    }

    modelDialog.value = false;
    model.value = {};
  } else {
    toast.add({severity: 'warn', summary: 'Внимание', detail: 'Неверное заполнение', life: 3000});
  }
};

const editModel = (editModel) => {
  model.value = {...editModel};
  calendarStartValue.value = editModel.start;
  calendarEndValue.value = editModel.end;

  modelDialog.value = true;
};

const exportCSV = () => {
  dt.value.exportCSV();
};

const confirmDeleteModel = (deleteModel) => {
  model.value = deleteModel;
  deleteModelDialog.value = true;
};

const deleteModel = () => {
  try {
    const res = stageService.delete(model.value.id);
    if (res == null || res.err) {
      console.error(res);
      toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Данные не удалены', life: 3000});
      return;
    }
  } catch (e) {
    console.error(e);
    toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Данные не удален', life: 3000});
    return;
  }

  deleteModelDialog.value = false;
  model.value = {};
  toast.add({severity: 'success', summary: 'Успешно', detail: 'Данные удалены', life: 3000});
};

</script>

<template>
  <div v-if="loading" class="grid">
    <Skeleton class="mb-2" height="2.5em" width="100%"></Skeleton>
  </div>
  <div v-else>
    <div class="col-12">
      <div class="card">
        <h5>Редактирование сезона</h5>
        <div class="p-fluid formgrid grid">
          <div class="field col-12">
            <CreatedModifiedBlock v-model="model"/>
          </div>

          <div class="field col-12">
            <SelectIdByTitleBlock v-model="model.season"
                                  label="Родительский сезон"/>
          </div>

          <div class="field col-12">
            <TextareaBlock v-model="model.comment" auto-resize
                           label="Комментарий" maxlength="200"
                           rows="1"/>
          </div>

          <div class="field col-12">
            <TextInputBlock v-model="model.title" :autofocus="true"
                            :invalid="submitted && !model.title"
                            label="Название"/>
          </div>

          <div class="field col-12">
            <TextareaBlock v-model="model.description" label="Описание"/>
          </div>

          <div class="field col-12">
            <TextareaBlock v-model="model.rules" label="Правила"/>
          </div>

          <div class="field col-12 md:col-6">
            <CalendarInputBlock v-model="calendarStartValue" :submitted="submitted"
                                label="Начало этапа"/>
          </div>

          <div class="field col-12 md:col-6">
            <CalendarInputBlock v-model="calendarEndValue" :submitted="submitted"
                                label="Конец этапа"/>
          </div>

          <div class="field col-12">
            <TextInputBlock v-model="model.literal" label="Литерал" disabled max-length="100"/>
          </div>

          <div class="field col-12">
            <TextareaBlock v-model="model.stageResultFormula" label="Формула" disabled/>
          </div>

          <div class="field col-12 md:col-4">
            <ViewStateInputBlock v-model="model.stageVisibility" :is-read-view-only="true"
                                 :submitted="submitted" label="Видимость этапа участниками"/>
          </div>

          <div class="field col-12 md:col-4">
            <ViewStateInputBlock v-model="model.scoreVisibility" :is-read-view-only="true"
                                 :submitted="submitted" label="Видимость оценок за этап участниками"/>
          </div>

          <div class="field col-12 md:col-4">
            <ViewStateInputBlock v-model="model.scheduleAccessState" :submitted="submitted"
                                 label="Доступ к расписанию этапа"/>
          </div>

          <div class="field col-12">
            <label for="comment">Этапы</label>
          </div>

          <div class="field col-12 md:col-4">
            <Button class="sm:mb-2" icon="pi pi-check" label="Сохранить"/>
          </div>
          <div class="field col-12 md:col-4">
            <Button class="mr-2 sm:mb-2" icon="pi pi-refresh" label="Обновить" severity="warning"/>
          </div>
          <div class="field col-12 md:col-4">
            <Button class="mr-2 sm:mb-2" icon="pi pi-trash" label="Удалить" severity="danger"/>
          </div>

          <Dialog v-model:visible="deleteModelDialog" :modal="true" :style="{ width: '700px' }"
                  header="Подтверждение">
            <div class="flex align-items-center justify-content-center">
              <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
              <span v-if="model">Вы точно хотите удалить <b>{{ model.title }} <small>ID: {{model.id }}</small></b> и <u>все</u> связанные данные?</span>
            </div>
            <template #footer>
              <Button icon="pi pi-times" label="Нет" @click="deleteModelDialog = false"/>
              <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteModel"/>
            </template>
          </Dialog>
        </div>
      </div>
    </div>
  </div>
</template>
