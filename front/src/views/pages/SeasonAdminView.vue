<script setup>
import {onMounted, ref} from 'vue';
import {useToast} from 'primevue/usetoast';
import {SeasonService} from '@/service/SeasonService';
import {AllowStateService} from '@/service/AllowStateService';
import {DateTimeService} from '@/service/DateTimeService';
import {useRoute, useRouter} from "vue-router";
import {CredsService} from "@/service/CredsService";
import {ToastService} from "@/service/ToastService";
import {FilterMatchMode, FilterOperator} from "primevue/api";
import {StageService} from "@/service/StageService";
import TextareaBlock from "@/components/prefab/TextareaBlock.vue";

const toast = useToast();

const loading = ref(false);

const stagesDt = ref(null);

const seasonService = new SeasonService();
const allowStateService = new AllowStateService();
const dateTimeService = new DateTimeService();
const toastService = new ToastService(toast);

const model = ref({});
const calendarStartValue = ref(null);
const calendarEndValue = ref(null);
const seasonDialog = ref(false);
const deleteSeasonDialog = ref(false);

const selectedStages = ref();

const submitted = ref(false);

const statuses = ref(allowStateService.getBadgeContentViewOnly());

const route = useRoute();
const router = useRouter();

onMounted(() => {
  loading.value = true;
  initStagesFilters();
  seasonService.get(route.params.id).then((data) => {
    createModelClient(data, true);
  });
});


const hideDialog = () => {
  seasonDialog.value = false;
  submitted.value = false;
};

const validateInput = () => {
  return (
    model.value &&
    //season.value.comment &&
    model.value.title &&
    model.value.title.trim() !== '' &&
    //season.value.description &&
    //season.value.rules &&
    calendarStartValue.value &&
    calendarEndValue.value &&
    // && season.value.seasonResultFormula
    model.value.seasonVisibility &&
    model.value.scoreVisibility
  );
};

const createModelClient = (modelServer, onMounted = false) => {
  calendarStartValue.value = dateTimeService.getDateFromTimestamp(modelServer.start);
  calendarEndValue.value = dateTimeService.getDateFromTimestamp(modelServer.end);

  const newModel = {
    ...modelServer,
    createdAt: dateTimeService.getDateFromTimestamp(modelServer.createdAt),
    modifiedAt: dateTimeService.getDateFromTimestamp(modelServer.modifiedAt),
    start: calendarStartValue.value,
    end: calendarEndValue.value
  };

  return (model.value.createdBy && model.value.createdBy.id == newModel.createdBy ?
      new Promise((resolve) => resolve(model.value.createdBy)) :
      credsService.getFullName(newModel.createdBy)
  ).then((createdData) => {
    if (createdData.err) throw createdData;

    const creatorIsEditor = newModel.createdBy === newModel.modifiedBy;
    newModel.createdBy = createdData;

    if (creatorIsEditor) return createdData;
    else if (model.value.modifiedBy && model.value.modifiedBy.id == newModel.modifiedBy) return model.value.modifiedBy;
    else if (newModel.modifiedBy) return credsService.getFullName(newModel.modifiedBy);
  }).then((modifiedData) => {
    if (!modifiedData) return;

    if (modifiedData.err) throw modifiedData;
    newModel.modifiedBy = modifiedData;
  }).catch((e) => {
    toastService.showAuditError(e);
  }).finally(() => {
    model.value = newModel;
    loading.value = false;
  });
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

const saveModel = () => {
  submitted.value = true;

  if (validateInput()) {
    seasonService.editShort(model.value.id, createModelDto())
      //.then((res) => {
      //  if (!toastService.checkServerError(res))
      //    return seasonService.bindStage(model.value.season, model.value.id);
      //})
      .then((res) => {
        if (!toastService.checkServerError(res))
          return seasonService.get(route.params.id)
      })
      .then((res) => {
        if (!toastService.checkServerError(res))
          return createModelClient(res).then(() => toastService.showEditedSuccess());
      })
      .catch((e) => toastService.showClientError(e));
  } else toastService.showValidationWarn();
};

const updateModel = () => {
  seasonService.get(route.params.id).then((data) => {
    createModelClient(data);
  });
}

const editSeason = (editSeason) => {
  model.value = {...editSeason};
  calendarStartValue.value = editSeason.start;
  calendarEndValue.value = editSeason.end;

  seasonDialog.value = true;
};

const exportCSV = () => {
  dt.value.exportCSV();
};

const confirmDeleteSeason = () => {
  deleteSeasonDialog.value = true;
};

const deleteSeason = () => {
  try {
    const res = seasonService.delete(model.value.id);
    if (res == null || res.err) {
      console.error(res);
      toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Сезон не удален', life: 3000});
      return;
    }
  } catch (e) {
    console.error(e);
    toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Сезон не удален', life: 3000});
    return;
  }

  deleteSeasonDialog.value = false;
  model.value = {};
  toast.add({severity: 'success', summary: 'Успешно', detail: 'Сезон удален', life: 3000});
  router.push('/admin/seasons');
};

const stagesFilters = ref();
const initStagesFilters = () => {
  stagesFilters.value = {
    global: {value: null, matchMode: FilterMatchMode.CONTAINS},
    id: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    title: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.STARTS_WITH}]},
  };
};

const deleteStageDialog = ref(false);
const deleteStagesDialog = ref(false);
const selectedStage = ref(null);
const confirmDeleteStage = (stage) => {
  selectedStage.value = stage;
  deleteStageDialog.value = true;
};

const stageService = new StageService();
const deleteSelectedStage = () => {
  stageService.delete(selectedStage.value.id)
    .then((res) => {
      if (res.err) toastService.showClientError(res);
      model.value.stages = model.value.stages.filter((stage) => stage.id !== selectedStage.value.id);
      toastService.showDeletedSuccess();
    })
    .catch((e) => toastService.showClientError(e))
    .finally(() => {
      selectedStage.value = null;
      deleteStageDialog.value = false;
    });
}

const deleteSelectedStages = () => {
  let ex = false;
  for (const val of selectedStages.value) {
    stageService.delete(val.id)
      .then((res) => {
        if (res.err) toastService.showClientError(res);
        model.value.stages = model.value.stages.filter((stage) => stage.id !== val.id);
      })
      .catch((e) => {
        ex = false;
        toastService.showClientError(e);
      })
      .finally(() => {
        selectedStage.value = null;
        deleteStageDialog.value = false;
      });
  }
  if (ex) { // todo Всунуть в промизы
    toastService.showClientError(ex);
  } else {
    toastService.showDeletedSuccess();
  }
  deleteStagesDialog.value = false;
  selectedStages.value = null;
};

const credsService = new CredsService();
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
            <TextareaBlock v-model="model.comment" auto-resize
                           label="Комментарий" maxlength="200" rows="1"/>
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
            <TextareaBlock v-model="model.description" label="Правила"/>
          </div>

          <div class="field col-12 md:col-6">
            <CalendarInputBlock v-model="calendarStartValue" :submitted="submitted" label="Начало сезона"/>
          </div>

          <div class="field col-12 md:col-6">
            <CalendarInputBlock v-model="calendarEndValue" :submitted="submitted" label="Конец сезона"/>
          </div>

          <div class="field col-12">
            <TextareaBlock v-model="model.seasonResultFormula" disabled label="Формула"/>
          </div>

          <div class="field col-12 md:col-6">
            <ViewStateInputBlock v-model="model.seasonVisibility" :is-read-view-only="true"
                                 :submitted="submitted" label="Видимость сезона участниками"/>
          </div>

          <div class="field col-12 md:col-6">
            <ViewStateInputBlock v-model="model.scoreVisibility" :is-read-view-only="true"
                                 :submitted="submitted" label="Видимость оценок участниками"/>
          </div>

          <div class="field col-12">
            <label for="comment">Этапы</label>

            <DataTable ref="stagesDt" v-model:filters="stagesFilters" v-model:selection="selectedStages"
                       :globalFilterFields="['id', 'title']" :rows="5" :rowsPerPageOptions="[5, 10, 20, 50]"
                       :value="model.stages" dataKey="id" filter-display="menu"
                       paginator paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                       show-gridlines sort-field="title"
                       sort-order="1">
              <template #header>
                <div class="p-fluid formgrid grid">
                  <div class="ml-2 mr-2">
                    <Button icon="pi pi-filter-slash" label="Сбросить фильтры" outlined type="button"
                            @click="initStagesFilters()"/>
                  </div>
                  <div>
                    <Button :disabled="!selectedStages || !selectedStages.length" icon="pi pi-trash" label="Удалить" outlined severity="danger"
                            type="button" @click="deleteStagesDialog = true"/>
                  </div>
                </div>
              </template>
              <Column headerStyle="width: 3rem" selectionMode="multiple"></Column>
              <Column dataType="numeric" field="id" header="ID" headerStyle="width:15%; min-width:5rem;"
                      sortable>
                <template #body="slotProps">{{ slotProps.data.id }}</template>
                <template #filter="{ filterModel }">
                  <InputNumber v-model="filterModel.value" mode="decimal"/>
                </template>
              </Column>
              <Column :sortable="true" field="title" header="Название"
                      headerStyle="width:80%; min-width:10rem;">
                <template #body="slotProps">{{ slotProps.data.title }}</template>
                <template #filter="{ filterModel, filterCallback }">
                  <InputText v-model="filterModel.value" class="p-column-filter"
                             placeholder="Искать по названию"
                             type="text" @keydown.enter="filterCallback()"/>
                </template>
              </Column>
              <Column header="Действия" headerStyle="width:5%;min-width:11rem;">
                <template #body="slotProps">
                  <RouterLink :to="'/admin/stage/' + slotProps.data.id">
                    <Button class="mr-2" icon="pi pi-eye" rounded severity="success"/>
                  </RouterLink>
                  <Button class="mr-2" icon="pi pi-trash" rounded severity="danger"
                          @click="confirmDeleteStage(slotProps.data)"/>
                </template>
              </Column>
            </DataTable>
          </div>

          <div class="field col-12 md:col-4">
            <Button class="sm:mb-2" icon="pi pi-check" label="Сохранить" @click="saveModel"/>
          </div>
          <div class="field col-12 md:col-4">
            <Button class="mr-2 sm:mb-2" icon="pi pi-refresh" label="Обновить" severity="warning" @click="updateModel"/>
          </div>
          <div class="field col-12 md:col-4">
            <Button class="mr-2 sm:mb-2" icon="pi pi-trash" label="Удалить" severity="danger"
                    @click="confirmDeleteSeason"/>
          </div>

          <Dialog v-model:visible="deleteSeasonDialog" :modal="true" :style="{ width: '700px' }"
                  header="Подтверждение">
            <div class="flex align-items-center justify-content-center">
              <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
              <span v-if="model"
              >Вы точно хотите удалить сезон <b>{{
                  model.title
                }}</b> и <u>все связанные данные</u>?</span
              >
            </div>
            <template #footer>
              <Button icon="pi pi-times" label="Нет" @click="deleteSeasonDialog = false"/>
              <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteSeason"/>
            </template>
          </Dialog>

          <Dialog v-model:visible="deleteStageDialog" :modal="true" :style="{ width: '700px' }" header="Подтверждение">
            <div class="flex align-items-center justify-content-center">
              <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
              <span v-if="model">Вы точно хотите удалить <b>{{ selectedStage.title }} <small>ID: {{
                  selectedStage.id
                }}</small></b> и <u>все</u> связанные данные?</span>
            </div>
            <template #footer>
              <Button icon="pi pi-times" label="Нет" @click="deleteStageDialog = false"/>
              <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteSelectedStage"/>
            </template>
          </Dialog>

          <Dialog v-model:visible="deleteStagesDialog" :modal="true" :style="{ width: '700px' }" header="Подтверждение">
            <div class="flex align-items-center justify-content-center">
              <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
              <span
                v-if="model">Вы точно хотите удалить <b>выбранные данные</b> и <u>всё</u>, что связано с ними?</span>
            </div>
            <template #footer>
              <Button icon="pi pi-times" label="Нет" @click="deleteStagesDialog = false"/>
              <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteSelectedStages"/>
            </template>
          </Dialog>
        </div>
      </div>
    </div>
  </div>
</template>
