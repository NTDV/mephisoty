<script setup>
import {FilterMatchMode, FilterOperator} from 'primevue/api';
import {onBeforeMount, onMounted, ref} from 'vue';
import {useToast} from 'primevue/usetoast';
import {SeasonService} from '@/service/SeasonService';
import {AllowStateService} from '@/service/AllowStateService';
import {DateTimeService} from '@/service/DateTimeService';
import {StageService} from "@/service/StageService";
import SelectIdByTitleBlock from "@/components/prefab/SelectIdByTitleBlock.vue";

const toast = useToast();

const loading = ref(false);
const lazyParams = ref({});
const first = ref(0);
const totalRecords = ref(0);
const selectAll = ref(false);

const dt = ref(null);

const parentSeason = ref(null);
const seasonSelectData = ref(null);

const seasonService = new SeasonService();
const stageService = new StageService();
const allowStateService = new AllowStateService();
const dateTimeService = new DateTimeService();

const models = ref(null);
const editDialog = ref(false);
const model = ref({});
const calendarStartValue = ref(null);
const calendarEndValue = ref(null);
const deleteModelDialog = ref(false);
const deleteModelsDialog = ref(false);

const selectedModels = ref(null);
const filters = ref({});
const submitted = ref(false);

const statuses = ref(allowStateService.getBadgeContentViewOnly());

onBeforeMount(() => {
  initFilters();
});

onMounted(() => {
  loading.value = true;
  lazyParams.value = {
    first: 0,
    rows: 10,
    sortField: null,
    sortOrder: null,
    filters: filters.value
  };
  seasonService.getAllForSelect().then((data) => {
    if (data && !data.err) seasonSelectData.value = data;
    else toast.add({
      severity: 'error',
      summary: 'Ошибка сервера',
      detail: 'Не удалось получить информацию о сезонах',
      life: 3000
    });
  });
  loadLazyData();
});

const initFilters = () => {
  filters.value = {
    global: {value: null, matchMode: FilterMatchMode.CONTAINS},
    id: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    title: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.STARTS_WITH}]},
    start: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.DATE_IS}]},
    end: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.DATE_IS}]},
    stageVisibility: {operator: FilterOperator.OR, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    scoreVisibility: {operator: FilterOperator.OR, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    scheduleAccessState: {operator: FilterOperator.OR, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]}
  };
};

const changeSeason = () => {
  loadLazyData();
}

const loadLazyData = (event) => {
  loading.value = true;
  lazyParams.value = {...lazyParams.value, first: event?.first || first.value};

  stageService.getAll(lazyParams.value, parentSeason.value).then((data) => {
    models.value = data.collection.map((val) => createModelClient(val));
    totalRecords.value = data.total;
    loading.value = false;
  })
};

const createModelClient = (seasonServer) => {
  return {
    ...seasonServer,
    start: dateTimeService.getDateFromTimestamp(seasonServer.start),
    end: dateTimeService.getDateFromTimestamp(seasonServer.end)
  };
};

const onPage = (event) => {
  lazyParams.value = event;
  loadLazyData(event);
};
const onSort = (event) => {
  lazyParams.value = event;
  loadLazyData(event);
};
const onFilter = (event) => {
  lazyParams.value.filters = filters.value;
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

const openNew = () => {
  if (!parentSeason.value) {
    toast.add({severity: 'error', summary: 'Ошибка', detail: 'Сначала выберите родительский сезон', life: 3000});
    return;
  }
  model.value = {
    stageVisibility: allowStateService.defaultState,
    scoreVisibility: allowStateService.defaultState,
    scheduleAccessState: allowStateService.defaultState
  };
  submitted.value = false;
  calendarStartValue.value = dateTimeService.getDateNow();
  calendarEndValue.value = dateTimeService.getDateNow();
  editDialog.value = true;
};

const hideDialog = () => {
  editDialog.value = false;
  submitted.value = false;
};

const validateInput = () => {
  return (
    model.value &&
    //season.value.comment &&
    model.value.title && model.value.title.trim() !== '' &&
    //season.value.description &&
    //season.value.rules &&
    calendarStartValue.value &&
    calendarEndValue.value &&
    // && season.value.seasonResultFormula
    model.value.stageVisibility &&
    model.value.scoreVisibility &&
    model.value.scheduleAccessState
  );
};

const createModelDto = () => {
  return {
    comment: model.value.comment ? model.value.comment : '',
    title: model.value.title,
    description: model.value.description ? model.value.description : '',
    rules: model.value.rules ? model.value.rules : '',
    start: dateTimeService.getDateTimeOffsetFromDate(calendarStartValue.value),
    end: dateTimeService.getDateTimeOffsetFromDate(calendarEndValue.value),
    literal: model.value.literal,
    stageResultFormula: model.value.stageResultFormula,
    stageVisibility: model.value.stageVisibility,
    scoreVisibility: model.value.scoreVisibility,
    scheduleAccessState: model.value.scheduleAccessState
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
        models.value[findSeasonIndexById(model.value.id)] = model.value;
        toast.add({severity: 'success', summary: 'Успешно', detail: 'Данные изменены', life: 3000});
      } catch (e) {
        console.error(e);
        toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Данные не изменены', life: 3000});
        return;
      }
    } else {
      try {
        const res = await seasonService.addStage(parentSeason.value, createModelDto());
        if (res.err) {
          console.error(res);
          toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Сезон не создан', life: 3000});
          return;
        }

        model.value = createModelClient(res);
        models.value.push(model.value);
        toast.add({severity: 'success', summary: 'Успешно', detail: 'Сезон добавлен', life: 3000});
      } catch (e) {
        console.error(e);
        toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Сезон не создан', life: 3000});
        return;
      }
    }

    editDialog.value = false;
    model.value = {};
  } else {
    toast.add({severity: 'warn', summary: 'Внимание', detail: 'Неверное заполнение', life: 3000});
  }
};

const editModel = (editModel) => {
  model.value = {...editModel};
  calendarStartValue.value = editModel.start;
  calendarEndValue.value = editModel.end;

  editDialog.value = true;
};

const findSeasonIndexById = (id) => {
  let index = -1;
  for (let i = 0; i < models.value.length; i++) {
    if (models.value[i].id === id) {
      index = i;
      break;
    }
  }
  return index;
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
    toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Данные не удалены', life: 3000});
    return;
  }

  models.value = models.value.filter((val) => val.id !== model.value.id);
  deleteModelDialog.value = false;
  model.value = {};
  toast.add({severity: 'success', summary: 'Успешно', detail: 'Данные удалены', life: 3000});
};

const confirmDeleteSelected = () => {
  deleteModelsDialog.value = true;
};

const deleteSelected = () => {
  try {
    for (const val of selectedModels.value) {
      const res = stageService.delete(val.id);
      if (res == null || res.err) {
        console.error(res);
        toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Часть данных не удалена', life: 3000});
        return;
      }
    }
  } catch (e) {
    console.error(e);
    toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Часть данных не удалена', life: 3000});
    return;
  }

  models.value = models.value.filter((val) => !selectedModels.value.includes(val));
  deleteModelsDialog.value = false;
  selectedModels.value = null;
  toast.add({severity: 'success', summary: 'Успешно', detail: 'Данные удалены', life: 3000});
};

const clearFilter = () => {
  initFilters();
  lazyParams.value.filters = filters.value;
  loadLazyData();
};
</script>

<template>
  <div class="grid">
    <div class="col-12">
      <div class="card">
        <Toolbar class="mb-4">
          <template v-slot:start>
            <div class="my-2">
              <SelectIdByTitleBlock v-model="parentSeason" :crudService="seasonService" infix="season"
                                    label="Родительский сезон: " style="min-width: 10em; max-width: 20em;" @change="changeSeason"/>
            </div>
          </template>
          <template v-slot:end>
            <div>
              <Button class="mr-2 mb-2" icon="pi pi-upload" label="Экспорт" severity="help"
                      @click="exportCSV($event)"/>
              <Button class="mr-2 mb-2" icon="pi pi-plus" label="Добавить" severity="success"
                      @click="openNew"/>
              <Button :disabled="!selectedModels || !selectedModels.length" class="mb-2" icon="pi pi-trash"
                      label="Удалить" severity="danger" @click="confirmDeleteSelected"/>
            </div>
          </template>
        </Toolbar>

        <DataTable
          ref="dt"
          v-model:filters="filters"
          v-model:selection="selectedModels"
          :first="first"
          :global-filter-fields="['id', 'title']"
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
              <h5 class="m-0">Управление этапами</h5>
              <Button icon="pi pi-filter-slash" label="Сбросить фильтры" outlined type="button"
                      @click="clearFilter()"/>
            </div>
          </template>
          <template #empty>Не найдено.</template>

          <Column headerStyle="width: 3rem" selectionMode="multiple"></Column>

          <Column :sortable="true" dataType="numeric" field="id" header="ID"
                  headerStyle="width:10%; min-width:5rem;">
            <template #body="slotProps">
              {{ slotProps.data.id }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" mode="decimal" @keydown.enter="filterCallback()"/>
            </template>
          </Column>

          <Column :sortable="true" field="title" header="Название" headerStyle="width:14%; min-width:10rem;">
            <template #body="slotProps">
              {{ slotProps.data.title }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по названию"
                         type="text" @keydown.enter="filterCallback()"/>
            </template>
          </Column>

          <Column :sortable="true" dataType="date" field="start" header="Начало"
                  headerStyle="width:16%; min-width:10rem;">
            <template #body="slotProps">
              {{ dateTimeService.formatDateTimeFromDate(slotProps.data.start) }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <Calendar
                v-model="filterModel.value"
                :showButtonBar="true"
                :showIcon="true"
                :showSeconds="true"
                :showTime="true"
                date-format="dd.mm.yy"
                hour-format="24"
                mask="99.99.9999 99:99:99"
                placeholder="dd.mm.yyyy hh:mm:ss"
                selectionMode="single"
                @keydown.enter="filterCallback()"
              />
            </template>
          </Column>

          <Column :sortable="true" dataType="date" field="end" header="Конец"
                  headerStyle="width:16%; min-width:10rem;">
            <template #body="slotProps">
              {{ dateTimeService.formatDateTimeFromDate(slotProps.data.end) }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <Calendar
                v-model="filterModel.value"
                :showButtonBar="true"
                :showIcon="true"
                :showSeconds="true"
                :showTime="true"
                date-format="dd.mm.yy"
                hour-format="24"
                mask="99.99.9999 99:99:99"
                placeholder="dd.mm.yyyy hh:mm:ss"
                selectionMode="single"
                @keydown.enter="filterCallback()"
              />
            </template>
          </Column>

          <Column :showFilterMatchModes="false" :sortable="true" field="stageVisibility"
                  header="Видимость этапа участниками" headerStyle="width:16%; min-width:13rem;">
            <template #body="slotProps">
              <Tag :severity="allowStateService.getBadgeSeverityFor(slotProps.data.stageVisibility)">
                {{ allowStateService.getBadgeContentFor(slotProps.data.stageVisibility) }}
              </Tag>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <Dropdown v-model="filterModel.value" :options="statuses" class="p-column-filter"
                        option-value="value" optionLabel="label" placeholder="Выберите один" showClear
                        @keydown.enter="filterCallback()">
                <template #option="slotProps">
                  <Tag :key="slotProps.option.value"
                       :severity="allowStateService.getBadgeSeverityFor(slotProps.option.value)"
                       :value="slotProps.option.label"/>
                </template>
              </Dropdown>
            </template>
          </Column>

          <Column :showFilterMatchModes="false" :sortable="true" field="scoreVisibility"
                  header="Видимость оценок участниками" headerStyle="width:16%; min-width:13rem;">
            <template #body="slotProps">
              <Tag :severity="allowStateService.getBadgeSeverityFor(slotProps.data.scoreVisibility)">
                {{ allowStateService.getBadgeContentFor(slotProps.data.scoreVisibility) }}
              </Tag>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <Dropdown v-model="filterModel.value" :options="statuses" class="p-column-filter"
                        option-value="value" optionLabel="label" placeholder="Выберите один" showClear
                        @keydown.enter="filterCallback()">
                <template #option="slotProps">
                  <Tag :key="slotProps.option.value"
                       :severity="allowStateService.getBadgeSeverityFor(slotProps.option.value)"
                       :value="slotProps.option.label"/>
                </template>
              </Dropdown>
            </template>
          </Column>

          <Column :showFilterMatchModes="false" :sortable="true" field="scheduleAccessState"
                  header="Видимость расписания участниками" headerStyle="width:16%; min-width:13rem;">
            <template #body="slotProps">
              <Tag :severity="allowStateService.getBadgeSeverityFor(slotProps.data.scheduleAccessState)">
                {{ allowStateService.getBadgeContentFor(slotProps.data.scheduleAccessState) }}
              </Tag>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <Dropdown v-model="filterModel.value" :options="statuses" class="p-column-filter"
                        option-value="value" optionLabel="label" placeholder="Выберите один" showClear
                        @keydown.enter="filterCallback()">
                <template #option="slotProps">
                  <Tag :key="slotProps.option.value"
                       :severity="allowStateService.getBadgeSeverityFor(slotProps.option.value)"
                       :value="slotProps.option.label"/>
                </template>
              </Dropdown>
            </template>
          </Column>

          <Column header="Действия" headerStyle="width:10%;min-width:11rem;">
            <template #body="slotProps">
              <RouterLink :to="'/admin/stage/' + slotProps.data.id">
                <Button class="mr-2" icon="pi pi-eye" rounded severity="success"/>
              </RouterLink>
              <Button class="mr-2" icon="pi pi-pencil" rounded severity="warning"
                      @click="editModel(slotProps.data)"/>
              <Button class="mr-2" icon="pi pi-trash" rounded severity="danger"
                      @click="confirmDeleteModel(slotProps.data)"/>
            </template>
          </Column>
        </DataTable>

        <Dialog v-model:visible="editDialog" :modal="true"
                :style="{ width: '700px' }" class="p-fluid"
                header="Информация об этапе">
          <div class="field">
            <TextareaBlock v-model="model.comment" auto-resize
                           label="Комментарий" maxlength="200"
                           rows="1"/>
          </div>

          <div class="field">
            <TextInputBlock v-model="model.title" :autofocus="true"
                            :invalid="submitted && !model.title"
                            label="Название"/>
          </div>

          <div class="field">
            <TextareaBlock v-model="model.description" label="Описание"/>
          </div>

          <div class="field">
            <TextareaBlock v-model="model.rules" label="Правила"/>
          </div>

          <div class="field">
            <CalendarInputBlock v-model="calendarStartValue" :submitted="submitted"
                                label="Начало этапа"/>
          </div>
          <div class="field">
            <CalendarInputBlock v-model="calendarEndValue" :submitted="submitted"
                                label="Конец этапа"/>
          </div>
          <div class="field">
            <TextInputBlock v-model="model.literal" label="Литерал" disabled max-length="100"/>
          </div>
          <div class="field">
            <TextareaBlock v-model="model.stageResultFormula" label="Формула" disabled/>
          </div>
          <div class="field">
            <ViewStateInputBlock v-model="model.stageVisibility" :is-read-view-only="true"
                                 :submitted="submitted" label="Видимость этапа участниками"/>
          </div>
          <div class="field">
            <ViewStateInputBlock v-model="model.scoreVisibility" :is-read-view-only="true"
                                 :submitted="submitted" label="Видимость оценок за этап участниками"/>
          </div>
          <div class="field">
            <ViewStateInputBlock v-model="model.scheduleAccessState" :submitted="submitted"
                                 label="Доступ к расписанию этапа"/>
          </div>

          <template #footer>
            <Button icon="pi pi-times" label="Отменить" text @click="hideDialog"/>
            <Button icon="pi pi-check" label="Сохранить" @click="saveModel"/>
          </template>
        </Dialog>

        <Dialog v-model:visible="deleteModelDialog" :modal="true" :style="{ width: '700px' }" header="Подтверждение">
          <div class="flex align-items-center justify-content-center">
            <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
            <span v-if="model">Вы точно хотите удалить <b>{{ model.title }} <small>ID: {{model.id }}</small></b> и <u>все</u> связанные данные?</span>
          </div>
          <template #footer>
            <Button icon="pi pi-times" label="Нет" @click="deleteModelDialog = false"/>
            <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteModel"/>
          </template>
        </Dialog>

        <Dialog v-model:visible="deleteModelsDialog" :modal="true" :style="{ width: '700px' }" header="Подтверждение">
          <div class="flex align-items-center justify-content-center">
            <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
            <span v-if="model">Вы точно хотите удалить <b>выбранные данные</b> и <u>всё</u>, что связано с ними?</span>
          </div>
          <template #footer>
            <Button icon="pi pi-times" label="Нет" @click="deleteModelsDialog = false"/>
            <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteSelected"/>
          </template>
        </Dialog>

      </div>
    </div>
  </div>
</template>
