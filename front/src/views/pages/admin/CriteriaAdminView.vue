<script setup>
import { onMounted, ref } from 'vue';
import { useToast } from 'primevue/usetoast';
import { DateTimeService } from '@/service/util/DateTimeService';
import { useRoute } from 'vue-router';
import { CredsService } from '@/service/admin/CredsService';
import { StageService } from '@/service/admin/StageService';
import SelectIdByTitleBlock from '@/components/prefab/SelectIdByTitleBlock.vue';
import { ToastService } from '@/service/util/ToastService';
import { CriteriaService } from '@/service/admin/CriteriaService';
import router from '@/router';

const toast = useToast();

const loading = ref(false);

const stageService = new StageService();
const criteriaService = new CriteriaService();
const dateTimeService = new DateTimeService();
const credsService = new CredsService();
const toastService = new ToastService(toast);

const model = ref({});
const calendarStartValue = ref(null);
const calendarEndValue = ref(null);
const modelDialog = ref(false);
const deleteModelDialog = ref(false);

const submitted = ref(false);

const route = useRoute();

onMounted(() => {
  loading.value = true;
  criteriaService.get(route.params.id).then((data) => {
    createModelClient(data, true);
  });
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
      stage: modelServer.stage.id,
    }

    loading.value = false;
  });
};


const hideDialog = () => {
  modelDialog.value = false;
  submitted.value = false;
};

const validateInput = () => {
  return (
    model.value &&
    model.value.title &&
    model.value.title.trim() !== '' &&
    (model.value.min !== null && model.value.min !== undefined && model.value.max !== null && model.value.max !== undefined && model.value.min <= model.value.max)
  );
};

const createModelDto = () => {
  return {
    ...model.value,
  };
};

const saveModel = () => {
  submitted.value = true;

  if (validateInput()) {
    criteriaService.edit(model.value.id, createModelDto())
      .then((res) => {
        if (!toastService.isServerError(res))
          return criteriaService.bind(model.value.stage, model.value.id);
      })
      .then((res) => {
        if (!toastService.isServerError(res))
          return createModelClient(res).then(() => toastService.showEditedSuccess());
      })
      .catch((e) => toastService.showClientError(e));
  } else toastService.showValidationWarn();
};

const updateModel = () => {
  criteriaService.get(route.params.id).then((data) => {
    createModelClient(data);
  });
};

const confirmDeleteModel = () => {
  deleteModelDialog.value = true;
};

const deleteModel = () => {
  criteriaService.delete(model.value.id).then((res) => {
    if (res === true) {
      deleteModelDialog.value = false;
      model.value = {};
      toastService.showDeletedSuccess();
      return router.push('/admin/criterias');
    } else toastService.showServerError(res);
  }).catch((e) => toastService.showClientError(e));
};

</script>

<template>
  <div v-if="loading">
    <SkeletonAdminView title="Редактирование критерия"/>
  </div>
  <div v-else>
    <div class="col-12">
      <div class="card">
        <h5>Редактирование критерия</h5>
        <div class="p-fluid formgrid grid">
          <div class="field col-12">
            <CreatedModifiedBlock v-model="model"/>
          </div>

          <div class="field col-12">
            <SelectIdByTitleBlock v-model="model.stage" :crudService="stageService"
                                  infix="stage" label="Родительский этап"/>
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
            <InputNumberBlock v-model="model.min" :invalid="submitted && (model.min === null || model.min === undefined || model.min > model.max)"
                              label="Минимальное значение"/>
          </div>

          <div class="field col-12 md:col-6">
            <InputNumberBlock v-model="model.max" :invalid="submitted && (model.max === null || model.max === undefined || model.min > model.max)"
                              label="Максимальное значение"/>
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

          <Dialog v-model:visible="deleteModelDialog" :modal="true" :style="{ width: '700px' }"
                  header="Подтверждение">
            <div class="flex align-items-center justify-content-center">
              <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
              <span v-if="model">Вы точно хотите удалить <b>{{ model.title }} <small>ID: {{
                  model.id
                }}</small></b> и <u>все</u> связанные данные?</span>
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
