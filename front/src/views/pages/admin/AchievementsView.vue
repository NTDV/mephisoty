<script setup>
import { useToast } from 'primevue/usetoast';
import SelectIdByTitleBlock from '@/components/prefab/SelectIdByTitleBlock.vue';
import { onBeforeMount, onMounted, ref } from 'vue';
import { ToastService } from '@/service/util/ToastService';
import { StageService } from '@/service/admin/StageService';
import { ParticipantsService } from '@/service/admin/ParticipantsService';
import { AchievementTypeService } from '@/service/util/AchievementTypeService';
import { FilterMatchMode, FilterOperator } from 'primevue/api';
import { AchievementService } from '@/service/admin/AchievementService';
import { AchievementScoreService } from '@/service/admin/AchievementScoreService';
import InputNumberBlock from '@/components/prefab/InputNumberBlock.vue';

const toast = useToast();

const toastService = new ToastService(toast);
const stageService = new StageService();
const participantsService = new ParticipantsService();
const achievementTypeService = new AchievementTypeService();
const achievementService = new AchievementService();
const achievementScoreService = new AchievementScoreService();

const parentStage = ref(null);
const parentParticipant = ref(null);
const delimiter = ref(';');
const hasHeader = ref(true);
const loadingFile = ref(false);

const dt = ref(null);
const filename = ref('export');
const filters = ref({});
const selectedModels = ref(null);
const first = ref(0);
const loading = ref(false);
const selectAll = ref(false);
const totalRecords = ref(0);
const models = ref(null);
const lazyParams = ref({});

const types = ref(achievementTypeService.getBadgeContentAll());

const scores = ref(null);
const loadingScore = ref(false);

const flushScores = () => {
  if (!parentStage.value || !parentParticipant.value || loadingScore.value) return;

  loadingScore.value = true;
  achievementScoreService
    .getFor(parentStage.value, parentParticipant.value)
    .then(data => {
      if (!toastService.checkServerError(data))
        scores.value = data;
    })
    .catch(e => toastService.showClientError(e))
    .finally(() => loadingScore.value = false);
};

onBeforeMount(() => {
  initFilters();
});

onMounted(() => {
  lazyParams.value = {
    first: 0,
    rows: 10,
    sortField: null,
    sortOrder: null,
    filters: filters.value
  };
});

const changeParent = () => {
  if (parentStage.value && parentParticipant.value) {
    filename.value = `achievements_${parentStage.value}_${parentParticipant.value}`;
    loadLazyData();
  }
};

const uploader = async (event) => {
  if (!parentStage.value) {
    toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Выберите этап', life: 3000 });
    return;
  }
  if (loadingFile.value) {
    toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Дождитесь окончания импорта', life: 3000 });
    return;
  }

  loadingFile.value = true;
  achievementService.importNew(event.files[0], delimiter.value, hasHeader.value, parentStage.value)
    .then((e) => {
      if (!toastService.checkServerError(e))
        toast.add({ severity: 'success', summary: 'Успешно', detail: 'Все достижения были добавлены', life: 3000 });
    })
    .catch(e => toastService.showClientError(e))
    .finally(() => loadingFile.value = false);
};

const onFilter = (event) => {
  lazyParams.value.filters = filters.value;
  loadLazyData(event);
};
const onPage = (event) => {
  lazyParams.value = event;
  loadLazyData(event);
};
const onSort = (event) => {
  lazyParams.value = event;
  loadLazyData(event);
};

const onSelectAllChange = (event) => {
  selectAll.value = event.checked;

  if (selectAll.value) {
    selectedModels.value = models.value;
  } else {
    selectAll.value = false;
    selectedModels.value = [];
  }
};
const onRowSelect = () => {
  selectAll.value = selectedModels.value.length === totalRecords.value;
};
const onRowUnselect = () => {
  selectAll.value = false;
};

const clearFilter = () => {
  initFilters();
  lazyParams.value.filters = filters.value;
  loadLazyData();
};

const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    id: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
    typeCode: { operator: FilterOperator.OR, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
    comment: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.CONTAINS }] },
    criteriaTitle: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.CONTAINS }]
    },
    typeTitle: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.CONTAINS }] },
    description: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.CONTAINS }] },
    levelTitle: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.CONTAINS }] },
    statusTitle: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.CONTAINS }] },
    thanksFrom: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.CONTAINS }] },
    totalScore: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] }
  };
};

const loadLazyData = (event) => {
  flushScores();
  loading.value = true;
  lazyParams.value = { ...lazyParams.value, first: event?.first || first.value };

  achievementService.getAll(lazyParams.value, parentStage.value, parentParticipant.value)
    .then((data) => {
      models.value = data.collection;
      totalRecords.value = data.total;
    })
    .catch(e => toastService.showAccessError(e))
    .finally(() => loading.value = false);
};

const exportCSV = () => {
  dt.value.exportCSV();
};

const openNew = () => {
  if (!parentStage.value || !parentParticipant.value) {
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: 'Сначала выберите родительский этап и участника',
      life: 3000
    });
    return;
  }
  model.value = {
    typeCode: 1,
    comment: '',
    description: '',
    totalScore: 0,
    criteriaTitle: '',
    typeTitle: '',
    levelTitle: '',
    statusTitle: '',
    thanksFrom: ''
  };
  submitted.value = false;
  editDialog.value = true;
};

const model = ref({});
const editDialog = ref(false);
const submitted = ref(false);

const deleteModelDialog = ref(false);
const deleteModelsDialog = ref(false);

const confirmDeleteModel = (deleteModel) => {
  model.value = deleteModel;
  deleteModelDialog.value = true;
};

const deleteModel = async () => {
  try {
    const res = await achievementService.delete(model.value.id);
    if (res == null || res.err) {
      console.error(res);
      toast.add({ severity: 'error', summary: 'Ошибка сервера', detail: 'Данные не удалены', life: 3000 });
      return;
    }
  } catch (e) {
    console.error(e);
    toast.add({ severity: 'error', summary: 'Ошибка клиента', detail: 'Данные не удалены', life: 3000 });
    return;
  }

  models.value = models.value.filter((val) => val.id !== model.value.id);
  deleteModelDialog.value = false;
  model.value = {};
  toast.add({ severity: 'success', summary: 'Успешно', detail: 'Данные удалены', life: 3000 });
};

const confirmDeleteSelected = () => {
  deleteModelsDialog.value = true;
};

const deleteSelected = () => {
  try {
    for (const val of selectedModels.value) {
      const res = achievementService.delete(val.id);
      if (res == null || res.err) {
        console.error(res);
        toast.add({ severity: 'error', summary: 'Ошибка сервера', detail: 'Часть данных не удалена', life: 3000 });
        return;
      }
    }
  } catch (e) {
    console.error(e);
    toast.add({ severity: 'error', summary: 'Ошибка клиента', detail: 'Часть данных не удалена', life: 3000 });
    return;
  }

  models.value = models.value.filter((val) => !selectedModels.value.includes(val));
  deleteModelsDialog.value = false;
  selectedModels.value = null;
  toast.add({ severity: 'success', summary: 'Успешно', detail: 'Данные удалены', life: 3000 });
};

const editModel = (editModel) => {
  model.value = { ...editModel };

  editDialog.value = true;
};

const hideDialog = () => {
  editDialog.value = false;
  submitted.value = false;
};

const validateInput = () => {
  return (
    model.value != null &&
    (model.value.typeCode != null || model.value.typeCode === 0) &&
    model.value.comment != null &&
    model.value.description != null &&
    (model.value.totalScore != null || model.value.totalScore === 0) &&
    model.value.criteriaTitle != null &&
    model.value.typeTitle != null &&
    model.value.levelTitle != null &&
    model.value.statusTitle != null &&
    model.value.thanksFrom != null
  );
};

const createModelDto = () => {
  return model.value;
};

const createModelClient = (seasonServer) => {
  return seasonServer;
};

const findIndexById = (id) => {
  for (let i = 0; i < models.value.length; i++) {
    if (models.value[i].id === id) return i;
  }
  return -1;
};

const saveModel = async () => {
  submitted.value = true;

  if (validateInput()) {
    if (model.value.id) {
      try {
        const res = await achievementService.edit(model.value.id, createModelDto());
        if (res.err) {
          console.error(res);
          toast.add({ severity: 'error', summary: 'Ошибка сервера', detail: 'Данные не изменены', life: 3000 });
          return;
        }

        model.value = createModelClient(res);
        models.value[findIndexById(model.value.id)] = model.value;
        toast.add({ severity: 'success', summary: 'Успешно', detail: 'Данные изменены', life: 3000 });
      } catch (e) {
        console.error(e);
        toast.add({ severity: 'error', summary: 'Ошибка клиента', detail: 'Данные не изменены', life: 3000 });
        return;
      }
    } else {
      try {
        const res = await participantsService.createAchievement(parentParticipant.value, parentStage.value, createModelDto());
        if (res.err) {
          console.error(res);
          toast.add({ severity: 'error', summary: 'Ошибка сервера', detail: 'Этап не создан', life: 3000 });
          return;
        }

        model.value = createModelClient(res);
        models.value.push(model.value);
        toast.add({ severity: 'success', summary: 'Успешно', detail: 'Этап добавлен', life: 3000 });
      } catch (e) {
        console.error(e);
        toast.add({ severity: 'error', summary: 'Ошибка клиента', detail: 'Этап не создан', life: 3000 });
        return;
      }
    }

    editDialog.value = false;
    model.value = {};
  } else {
    toast.add({ severity: 'warn', summary: 'Внимание', detail: 'Неверное заполнение', life: 3000 });
  }
};

const flushExpertValue = (event) => {
  if (event.value === event.newValue) return;

  achievementScoreService
    .setExpertScoreFor(event.data.id, event.data.typeCode, event.newValue)
    .then(e => {
      if (!toastService.checkServerError(e))
        event.data.expert = event.newValue;
    })
    .catch(e => {
      toastService.showClientError(e);
    });
};

</script>

<template>
  <div class="grid">
    <div class="col-12">
      <div class="card">
        <h5>Управление достижениями</h5>
        <Toolbar class="mb-4 overflow-hidden">
          <template v-slot:start>
            <div class="my-2" style="display: grid">
              <SelectIdByTitleBlock v-model="parentStage" :crudService="stageService"
                                    infix="stage"
                                    label="Родительский этап: " style="min-width: 10em; max-width: 20em;"
                                    @change="changeParent" />
              <br>
              <SelectIdByTitleBlock v-model="parentParticipant" :crudService="participantsService"
                                    infix="user" label="Участник: "
                                    lazy-filter style="min-width: 10em; max-width: 20em;"
                                    @change="changeParent" />
            </div>
          </template>
          <template v-slot:center>
            <div v-if="!loadingFile" class="my-2">
              <div>
                <label>Импортировать достижения</label>
                <FileUpload :maxFileSize="30 * (8 * 1024 * 1024)" :multiple="false"
                            accept=".csv" cancel-label="Отмена" choose-label="Выбрать файл" customUpload
                            mode="basic" name="file" upload-label="Импортировать" @uploader="uploader" />
              </div>
              <br>
              <div class="flex">
                <div>
                  <label>Разделитель CSV</label>
                  <br>
                  <Dropdown v-model="delimiter" :options="[';', ',']" placeholder="Выберите" />
                </div>
                <div class="ml-2">
                  <label>Заголовок</label>
                  <br>
                  <ToggleButton v-model="hasHeader" offLabel="Нет" onLabel="Есть" />
                </div>
              </div>
            </div>

            <div v-else class="my-2">
              <div>
                <label>Импорт достижений</label>
                <ProgressSpinner animationDuration=".5s"
                                 class="mt-2" strokeWidth="8" style="display: block; width: 33px; height: 33px" />
              </div>
            </div>
          </template>
          <template v-slot:end>
            <div>
              <Button class="mr-2 mb-2" icon="pi pi-upload" label="Экспорт" severity="help"
                      @click="exportCSV($event)" />
              <Button class="mr-2 mb-2" icon="pi pi-plus" label="Добавить" severity="success"
                      @click="openNew" />
              <Button :disabled="!selectedModels || !selectedModels.length" class="mb-2" icon="pi pi-trash"
                      label="Удалить" severity="danger" @click="confirmDeleteSelected" />
            </div>
          </template>
        </Toolbar>

        <DataTable :editing-rows="['expert']" :value="scores" class="mb-5"
                   data-key="typeCode" edit-mode="cell"
                   @cell-edit-complete="flushExpertValue($event)">
          <template #header>
            <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center">
              <h5 class="m-0">Статистика баллов</h5>
              <Button :disabled="!parentStage || !parentParticipant || loadingScore" icon="pi pi-refresh"
                      label="Обновить" outlined
                      type="button" @click="flushScores" />
            </div>
          </template>
          <template #empty>
            <span v-if="parentStage && parentParticipant && !loadingScore">Данные не готовы. Повторите попытку через пару минут.</span>
            <span v-else>Не найдено.</span>
          </template>

          <Column :sortable="true" field="typeCode" header="Вид" headerStyle="width:16%; min-width:5rem;">
            <template #body="slotProps">
              <Tag :severity="achievementTypeService.getBadgeSeverityFor(slotProps.data.typeCode)">
                {{ achievementTypeService.getBadgeContentFor(slotProps.data.typeCode) }}
              </Tag>
            </template>
          </Column>

          <Column :sortable="true" dataType="numeric" field="mean" header="Среднее"
                  headerStyle="width:14%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.mean }}</template>
          </Column>

          <Column :sortable="true" dataType="numeric" field="sum" header="Сумма"
                  headerStyle="width:14%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.sum }}</template>
          </Column>

          <Column :sortable="true" dataType="numeric" field="formula" header="Формула"
                  headerStyle="width:14%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.formula }}</template>
          </Column>

          <Column :sortable="true" dataType="numeric" field="expert" header="Эксперт"
                  headerStyle="width:14%; min-width:5rem;"
                  row-editor>
            <template #body="slotProps">{{ slotProps.data.expert }}</template>
            <template #editor="slotProps">
              <InputNumberBlock v-model="slotProps.data.expert" />
            </template>
          </Column>

          <Column :sortable="true" dataType="numeric" field="min" header="Минимум"
                  headerStyle="width:14%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.min }}</template>
          </Column>

          <Column :sortable="true" dataType="numeric" field="max" header="Максимум"
                  headerStyle="width:14%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.max }}</template>
          </Column>
        </DataTable>

        <DataTable
          ref="dt"
          v-model:filters="filters"
          v-model:selection="selectedModels"
          :first="first"
          :global-filter-fields="['id']"
          :loading="loading"
          :paginator="true"
          :rows="10"
          :rowsPerPageOptions="[5, 10, 25, 50, 100, 500, 1000]"
          :selectAll="selectAll"
          :totalRecords="totalRecords"
          :value="models"
          currentPageReportTemplate="Записи с {first} по {last} из {totalRecords} всего"
          dataKey="id"
          filter-display="menu"
          lazy
          :export-filename="filename"
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
          sortMode="multiple"
          @filter="onFilter($event)"
          @page="onPage($event)"
          @sort="onSort($event)"
          @select-all-change="onSelectAllChange"
          @row-select="onRowSelect"
          @row-unselect="onRowUnselect">
          <template #header>
            <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center">
              <h5 class="m-0">Достижения участника</h5>
              <Button icon="pi pi-filter-slash" label="Сбросить фильтры" outlined type="button"
                      @click="clearFilter()" />
            </div>
          </template>
          <template #empty>Не найдено.</template>

          <Column headerStyle="width: 3rem" selectionMode="multiple"></Column>

          <Column :sortable="true" dataType="numeric" field="id" header="ID"
                  headerStyle="width:5%; min-width:5rem;">
            <template #body="slotProps">
              {{ slotProps.data.id }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" mode="decimal" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :showFilterMatchModes="false" :sortable="true" field="typeCode"
                  header="Вид" headerStyle="width:5%; min-width:3rem;">
            <template #body="slotProps">
              <Tag :severity="achievementTypeService.getBadgeSeverityFor(slotProps.data.typeCode)">
                {{ achievementTypeService.getBadgeContentFor(slotProps.data.typeCode) }}
              </Tag>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <Dropdown v-model="filterModel.value" :options="types" class="p-column-filter"
                        option-value="value" optionLabel="label" placeholder="Выберите один" showClear
                        @keydown.enter="filterCallback()">
                <template #option="slotProps">
                  <Tag :key="slotProps.option.value"
                       :severity="achievementTypeService.getBadgeSeverityFor(slotProps.option.value)"
                       :value="slotProps.option.label" />
                </template>
              </Dropdown>
            </template>
          </Column>

          <Column :sortable="true" field="comment" header="Комментарий" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">
              {{ slotProps.data.comment }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по комментарию"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="description" header="Описание" headerStyle="width:25%; min-width:20rem;">
            <template #body="slotProps">
              {{ slotProps.data.description }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по описанию"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" dataType="numeric" field="totalScore" header="Балл"
                  headerStyle="width:5%; min-width:5rem;">
            <template #body="slotProps">
              <span class="font-semibold">{{ slotProps.data.totalScore }}</span>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" mode="decimal" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="criteriaTitle" header="Показатель" headerStyle="width:15%; min-width:15rem;">
            <template #body="slotProps">
              {{ slotProps.data.criteriaTitle }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по показателю"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="typeTitle" header="Тип" headerStyle="width:15%; min-width:15rem;">
            <template #body="slotProps">
              {{ slotProps.data.typeTitle }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по типу"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="levelTitle" header="Уровень" headerStyle="width:7%; min-width:10rem;">
            <template #body="slotProps">
              {{ slotProps.data.levelTitle }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по уровню"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="statusTitle" header="Статус" headerStyle="width:7%; min-width:10rem;">
            <template #body="slotProps">
              {{ slotProps.data.statusTitle }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по статусу"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="thanksFrom" header="Бл-сть" headerStyle="width:10%; min-width:9rem;">
            <template #body="slotProps">
              {{ slotProps.data.thanksFrom }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по благодарности"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column header="Действия" headerStyle="width:10%;min-width:11rem;">
            <template #body="slotProps">
              <RouterLink :to="'/admin/achievement/' + slotProps.data.id">
                <Button class="mr-2" icon="pi pi-eye" rounded severity="success" />
              </RouterLink>
              <Button class="mr-2" icon="pi pi-pencil" rounded severity="warning"
                      @click="editModel(slotProps.data)" />
              <Button class="mr-2" icon="pi pi-trash" rounded severity="danger"
                      @click="confirmDeleteModel(slotProps.data)" />
            </template>
          </Column>
        </DataTable>

        <Dialog v-model:visible="editDialog" :modal="true" :style="{ width: '700px' }" class="p-fluid"
                header="Информация о достижении">
          <div class="field">
            <AchievementTypeInputBlock v-model="model.typeCode" :submitted="submitted" is-without-all
                                       label="Видимость этапа участниками" />
          </div>

          <div class="field">
            <TextareaBlock v-model="model.comment" auto-resize label="Комментарий" maxlength="200" not-required
                           rows="1" />
          </div>

          <div class="field">
            <TextareaBlock v-model="model.description" auto-resize label="Описание" maxlength="1500" not-required
                           rows="3" />
          </div>

          <div class="field">
            <InputNumberBlock v-model="model.totalScore" :submitted="submitted" label="Балл" max="1000" />
          </div>

          <div class="field">
            <TextareaBlock v-model="model.criteriaTitle" auto-resize label="Показатель" maxlength="255" not-required />
          </div>

          <div class="field">
            <TextareaBlock v-model="model.typeTitle" auto-resize label="Тип" maxlength="500" not-required />
          </div>

          <div class="field">
            <TextInputBlock v-model="model.levelTitle" label="Уровень" maxlength="255" not-required />
          </div>

          <div class="field">
            <TextInputBlock v-model="model.statusTitle" label="Статус" maxlength="255" not-required />
          </div>

          <div class="field">
            <TextInputBlock v-model="model.thanksFrom" label="Благодарность" maxlength="255" not-required />
          </div>

          <template #footer>
            <Button icon="pi pi-times" label="Отменить" text @click="hideDialog" />
            <Button icon="pi pi-check" label="Сохранить" @click="saveModel" />
          </template>
        </Dialog>

        <Dialog v-model:visible="deleteModelDialog" :modal="true" :style="{ width: '700px' }" header="Подтверждение">
          <div class="flex align-items-center justify-content-center">
            <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
            <span v-if="model">Вы точно хотите удалить <b>{{ model.title }} <small>ID: {{ model.id }}</small></b> и <u>все</u> связанные данные?</span>
          </div>
          <template #footer>
            <Button icon="pi pi-times" label="Нет" @click="deleteModelDialog = false" />
            <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteModel" />
          </template>
        </Dialog>

        <Dialog v-model:visible="deleteModelsDialog" :modal="true" :style="{ width: '700px' }" header="Подтверждение">
          <div class="flex align-items-center justify-content-center">
            <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
            <span v-if="model">Вы точно хотите удалить <b>выбранные данные</b> и <u>всё</u>, что связано с ними?</span>
          </div>
          <template #footer>
            <Button icon="pi pi-times" label="Нет" @click="deleteModelsDialog = false" />
            <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteSelected" />
          </template>
        </Dialog>
      </div>
    </div>
  </div>
</template>
