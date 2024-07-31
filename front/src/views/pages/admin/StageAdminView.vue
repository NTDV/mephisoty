<script setup>
import {onMounted, ref} from 'vue';
import {useToast} from 'primevue/usetoast';
import {DateTimeService} from '@/service/util/DateTimeService';
import {useRoute} from "vue-router";
import {CredsService} from "@/service/admin/CredsService";
import {StageService} from "@/service/admin/StageService";
import SelectIdByTitleBlock from "@/components/prefab/SelectIdByTitleBlock.vue";
import {SeasonService} from "@/service/admin/SeasonService";
import {ToastService} from "@/service/util/ToastService";
import router from "@/router";
import {FilterMatchMode, FilterOperator} from "primevue/api";
import {CriteriaService} from "@/service/admin/CriteriaService";

const toast = useToast();

const loading = ref(false);

const seasonService = new SeasonService();
const stageService = new StageService();
const dateTimeService = new DateTimeService();
const credsService = new CredsService();
const toastService = new ToastService(toast);

const model = ref({});
const calendarStartValue = ref(null);
const calendarEndValue = ref(null);
const deleteModelDialog = ref(false);

const submitted = ref(false);

const route = useRoute();

onMounted(() => {
  loading.value = true;
  stageService.get(route.params.id).then((data) => {
    createModelClient(data, true);
  });
  initCriteriasFilters();
});

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
    toastService.showAuditError();
  }).finally(() => {
    if (onMounted) model.value = newModel;
    else model.value = {
      ...newModel,
      season: modelServer.season.id,
    }

    loading.value = false;
  });
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
    model.value.stageVisibility &&
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

const saveModel = () => {
  submitted.value = true;

  if (validateInput()) {
    stageService.edit(model.value.id, createModelDto())
      .then((res) => {
        if (!toastService.checkServerError(res))
          return stageService.bindStage(model.value.season, model.value.id);
      })
      .then((res) => {
        if (!toastService.checkServerError(res))
          return createModelClient(res).then(() => toastService.showEditedSuccess());
      })
      .catch((e) => toastService.showClientError(e));
  } else toastService.showValidationWarn();
};

const confirmDeleteModel = () => {
  deleteModelDialog.value = true;
};

const deleteModel = async () => {
  try {
    const res = await stageService.delete(model.value.id);
    if (toastService.checkServerError(res)) return;
  } catch (e) {
    toastService.showClientError(e);
    return;
  }

  deleteModelDialog.value = false;
  model.value = {};
  toastService.showDeletedSuccess();
  await router.push('/admin/criterias');
};

const updateModel = () => {
  stageService.get(route.params.id).then((data) => {
    createModelClient(data);
  });
};

const criteriasFilters = ref();
const initCriteriasFilters = () => {
  criteriasFilters.value = {
    global: {value: null, matchMode: FilterMatchMode.CONTAINS},
    id: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    title: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.STARTS_WITH}]},
  };
};

const deleteCriteriaDialog = ref(false);
const deleteCriteriasDialog = ref(false);
const selectedCriteria = ref(null);
const selectedCriterias = ref(null);
const confirmDeleteStage = (stage) => {
  selectedCriteria.value = stage;
  deleteCriteriaDialog.value = true;
};

const criteriaService = new CriteriaService();
const deleteSelectedCriteria = () => {
  criteriaService.delete(selectedCriteria.value.id)
    .then((res) => {
      if (res.err) toastService.showClientError(res);
      model.value.stages = model.value.stages.filter((stage) => stage.id !== selectedCriteria.value.id);
      toastService.showDeletedSuccess();
    })
    .catch((e) => toastService.showClientError(e))
    .finally(() => {
      selectedCriteria.value = null;
      deleteCriteriaDialog.value = false;
    });
}

const deleteSelectedCriterias = async () => {
  let ex = false;
  for (const val of selectedCriterias.value) {
    await criteriaService.delete(val.id)
      .then((res) => {
        if (res.err) toastService.showClientError(res);
        model.value.criterias = model.value.criterias.filter((stage) => stage.id !== val.id);
      })
      .catch((e) => {
        ex = false;
        toastService.showClientError(e);
      })
      .finally(() => {
        selectedCriteria.value = null;
        deleteCriteriaDialog.value = false;
      });
  }
  if (ex) { // todo Всунуть в промизы
    toastService.showClientError(ex);
  } else {
    toastService.showDeletedSuccess();
  }
  deleteCriteriasDialog.value = false;
  selectedCriterias.value = null;
};

</script>

<template>
  <div v-if="loading">
    <SkeletonAdminView title="Редактирование этапа"/>
  </div>
  <div v-else>
    <div class="col-12">
      <div class="card">
        <h5>Редактирование этапа</h5>
        <div class="p-fluid formgrid grid">
          <div class="field col-12">
            <CreatedModifiedBlock v-model="model"/>
          </div>

          <div class="field col-12">
            <SelectIdByTitleBlock v-model="model.season" :crudService="seasonService"
                                  infix="season" label="Родительский сезон"/>
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
            <label for="comment">Критерии</label>
            <DataTable ref="stagesDt" v-model:filters="criteriasFilters" v-model:selection="selectedCriterias"
                       :globalFilterFields="['id', 'title']" :rows="5" :rowsPerPageOptions="[5, 10, 20, 50]"
                       :value="model.criterias" dataKey="id" filter-display="menu"
                       paginator
                       paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                       show-gridlines sort-field="title"
                       sort-order="1">
              <template #header>
                <div class="p-fluid formgrid grid">
                  <div class="ml-2 mr-2">
                    <Button icon="pi pi-filter-slash" label="Сбросить фильтры" outlined type="button"
                            @click="initCriteriasFilters()"/>
                  </div>
                  <div>
                    <Button :disabled="!selectedCriterias || !selectedCriterias.length" icon="pi pi-trash"
                            label="Удалить" outlined severity="danger"
                            type="button" @click="deleteCriteriasDialog = true"/>
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
                  <RouterLink :to="'/admin/criteria/' + slotProps.data.id">
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
                    @click="confirmDeleteModel"/>
          </div>

          <Dialog v-model:visible="deleteCriteriaDialog" :modal="true" :style="{ width: '700px' }"
                  header="Подтверждение">
            <div class="flex align-items-center justify-content-center">
              <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
              <span v-if="model">Вы точно хотите удалить <b>{{ selectedCriteria.title }}
                <small>ID: {{ selectedCriteria.id }}</small></b> и
                <u>все</u> связанные данные?</span>
            </div>
            <template #footer>
              <Button icon="pi pi-times" label="Нет" @click="deleteCriteriaDialog = false"/>
              <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteSelectedCriteria"/>
            </template>
          </Dialog>

          <Dialog v-model:visible="deleteCriteriasDialog" :modal="true" :style="{ width: '700px' }"
                  header="Подтверждение">
            <div class="flex align-items-center justify-content-center">
              <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
              <span
                v-if="model">Вы точно хотите удалить <b>выбранные данные</b> и <u>всё</u>, что связано с ними?</span>
            </div>
            <template #footer>
              <Button icon="pi pi-times" label="Нет" @click="deleteCriteriasDialog = false"/>
              <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteSelectedCriteria"/>
            </template>
          </Dialog>

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
