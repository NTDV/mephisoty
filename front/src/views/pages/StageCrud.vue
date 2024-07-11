<script setup>
import {FilterMatchMode, FilterOperator} from 'primevue/api';
import {onBeforeMount, onMounted, ref} from 'vue';
import {useToast} from 'primevue/usetoast';
import {SeasonService} from '@/service/SeasonService';
import {AllowStateService} from '@/service/AllowStateService';
import {DateTimeService} from '@/service/DateTimeService';
import {StageService} from "@/service/StageService";
import TextareaBlock from "@/components/prefab/TextareaBlock.vue";
import TextInputBlock from "@/components/prefab/TextInputBlock.vue";
import CalendarInputBlock from "@/components/prefab/CalendarInputBlock.vue";

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

const stages = ref(null);
const stageDialog = ref(false);
const stage = ref({});
const calendarStartValue = ref(null);
const calendarEndValue = ref(null);
const deleteStageDialog = ref(false);
const deleteStagesDialog = ref(false);

const selectedStages = ref(null);
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

  setTimeout(
    () =>
      stageService.getAll(lazyParams.value, parentSeason.value).then((data) => {
        stages.value = data.collection.map((val) => createStageClient(val));
        totalRecords.value = data.total;
        loading.value = false;
      }),
    500
  );
};

const createStageClient = (seasonServer) => {
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
    selectedStages.value = stages.value;
  } else {
    selectAll.value = false;
    selectedStages.value = [];
  }
};

const onRowSelect = () => {
  selectAll.value = selectedStages.value.length === totalRecords.value;
};
const onRowUnselect = () => {
  selectAll.value = false;
};

const openNew = () => {
  stage.value = {};
  submitted.value = false;
  calendarStartValue.value = null;
  calendarEndValue.value = null;
  stageDialog.value = true;
};

const hideDialog = () => {
  stageDialog.value = false;
  submitted.value = false;
};

const validateInput = () => {
  return (
    stage.value &&
    //season.value.comment &&
    stage.value.title &&
    stage.value.title.trim() !== '' &&
    //season.value.description &&
    //season.value.rules &&
    calendarStartValue.value &&
    calendarEndValue.value &&
    // && season.value.seasonResultFormula
    stage.value.seasonVisibility &&
    stage.value.scoreVisibility
  );
};

const createSeasonDto = () => {
  return {
    comment: stage.value.comment ? stage.value.comment : '',
    title: stage.value.title,
    description: stage.value.description ? stage.value.description : '',
    rules: stage.value.rules ? stage.value.rules : '',
    start: getDateTimeOffsetFromDate(calendarStartValue.value),
    end: getDateTimeOffsetFromDate(calendarEndValue.value),
    seasonResultFormula: stage.value.seasonResultFormula,
    seasonVisibility: stage.value.seasonVisibility,
    scoreVisibility: stage.value.scoreVisibility
  };
};

const saveSeason = async () => {
  submitted.value = true;

  if (validateInput()) {
    if (stage.value.id) {
      try {
        const res = await seasonService.edit(stage.value.id, createSeasonDto());
        if (res.err) {
          console.error(res);
          toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Сезон не изменен', life: 3000});
          return;
        }

        stage.value = createStageClient(res);
        stages.value[findSeasonIndexById(stage.value.id)] = stage.value;
        toast.add({severity: 'success', summary: 'Успешно', detail: 'Сезон изменен', life: 3000});
      } catch (e) {
        console.error(e);
        toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Сезон не изменен', life: 3000});
        return;
      }
    } else {
      try {
        const res = await seasonService.create(createSeasonDto());
        if (res.err) {
          console.error(res);
          toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Сезон не создан', life: 3000});
          return;
        }

        stage.value = createStageClient(res);
        stages.value.push(stage.value);
        toast.add({severity: 'success', summary: 'Успешно', detail: 'Сезон добавлен', life: 3000});
      } catch (e) {
        console.error(e);
        toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Сезон не создан', life: 3000});
        return;
      }
    }

    stageDialog.value = false;
    stage.value = {};
  } else {
    toast.add({severity: 'warn', summary: 'Внимание', detail: 'Неверное заполнение', life: 3000});
  }
};

const editSeason = (editSeason) => {
  stage.value = {...editSeason};
  calendarStartValue.value = editSeason.start;
  calendarEndValue.value = editSeason.end;

  stageDialog.value = true;
};

const findSeasonIndexById = (id) => {
  let index = -1;
  for (let i = 0; i < stages.value.length; i++) {
    if (stages.value[i].id === id) {
      index = i;
      break;
    }
  }
  return index;
};

const exportCSV = () => {
  dt.value.exportCSV();
};

const confirmDeleteSeason = (deleteSeason) => {
  stage.value = deleteSeason;
  deleteStageDialog.value = true;
};

const deleteSeason = () => {
  try {
    const res = seasonService.delete(stage.value.id);
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

  stages.value = stages.value.filter((val) => val.id !== stage.value.id);
  deleteStageDialog.value = false;
  stage.value = {};
  toast.add({severity: 'success', summary: 'Успешно', detail: 'Сезон удален', life: 3000});
};

const confirmDeleteSelected = () => {
  deleteStagesDialog.value = true;
};

const deleteSelectedSeasons = () => {
  try {
    for (const val of selectedStages.value) {
      const res = seasonService.delete(val.id);
      if (res == null || res.err) {
        console.error(res);
        toast.add({
          severity: 'error',
          summary: 'Ошибка сервера',
          detail: 'Часть сезонов не удалена',
          life: 3000
        });
        return;
      }
    }
  } catch (e) {
    console.error(e);
    toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Часть сезонов не удалена', life: 3000});
    return;
  }

  stages.value = stages.value.filter((val) => !selectedStages.value.includes(val));
  deleteStagesDialog.value = false;
  selectedStages.value = null;
  toast.add({severity: 'success', summary: 'Успешно', detail: 'Сезон удален', life: 3000});
};

const clearFilter = () => {
  initFilters();
  lazyParams.value.filters = filters.value;
  loadLazyData();
};

const viewSeason = (id) => {
  router.push({name: 'SeasonAdminView', params: {id: id}});
}
</script>

<template>
  <div class="grid">
    <div class="col-12">
      <div class="card">
        <Toolbar class="mb-4">
          <template v-slot:start>
            <div class="my-2">
              <label for="parentSeason">Родительский сезон: </label>
              <Dropdown id="parentSeason" v-model="parentSeason" :options="seasonSelectData"
                        class="p-column-filter" option-label="title" option-value="id"
                        placeholder="Выберите сезон" style="min-width: 10em; max-width: 20em;" @change="changeSeason">
                <template #option="slotProps">
                  <span>{{ slotProps.option.title }} <small>id: {{ slotProps.option.id }}</small></span>
                </template>
              </Dropdown>
            </div>
          </template>
          <template v-slot:end>
            <div>
              <Button class="mr-2 mb-2" icon="pi pi-upload" label="Экспорт" severity="help"
                      @click="exportCSV($event)"/>
              <Button class="mr-2 mb-2" icon="pi pi-plus" label="Добавить" severity="success"
                      @click="openNew"/>
              <Button :disabled="!selectedStages || !selectedStages.length" class="mb-2" icon="pi pi-trash"
                      label="Удалить" severity="danger" @click="confirmDeleteSelected"/>
            </div>
          </template>
        </Toolbar>

        <DataTable
          ref="dt"
          v-model:filters="filters"
          v-model:selection="selectedStages"
          :first="first"
          :global-filter-fields="['id', 'title']"
          :loading="loading"
          :paginator="true"
          :rows="10"
          :rowsPerPageOptions="[5, 10, 25, 50, 100, 500, 1000]"
          :selectAll="selectAll"
          :totalRecords="totalRecords"
          :value="stages"
          currentPageReportTemplate="Сезоны с {first} по {last} из {totalRecords} всего"
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
          @row-unselect="onRowUnselect"
        >
          <template #header>
            <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center">
              <h5 class="m-0">Управление этапами</h5>
              <Button icon="pi pi-filter-slash" label="Сбросить фильтры" outlined type="button"
                      @click="clearFilter()"/>
            </div>
          </template>
          <template #empty> Сезонов не найдено.</template>
          <template #loading> Загрузка. Пожалуйста, подождите.</template>

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
              <RouterLink :to="'/admin/season/' + slotProps.data.id">
                <Button class="mr-2" icon="pi pi-eye" rounded severity="success"/>
              </RouterLink>
              <Button class="mr-2" icon="pi pi-pencil" rounded severity="warning"
                      @click="editSeason(slotProps.data)"/>
              <Button class="mr-2" icon="pi pi-trash" rounded severity="danger"
                      @click="confirmDeleteSeason(slotProps.data)"/>
            </template>
          </Column>
        </DataTable>

        <Dialog v-model:visible="editDialog"
                :modal="true"
                :style="{ width: '700px' }"
                class="p-fluid"
                header="Информация об этапе">
          <div class="field">
            <TextareaBlock v-model="model.comment"
                           auto-resize
                           label="Комментарий"
                           maxlength="200"
                           rows="1"/>
          </div>

          <div class="field">
            <TextInputBlock v-model="model.title"
                            :autofocus="true"
                            :invalid="submitted && !model.title"
                            label="Название"/>
          </div>

          <div class="field">
            <TextareaBlock v-model="model.description"
                           label="Описание"/>
          </div>

          <div class="field">
            <label for="rules">Правила</label>
            <Textarea id="rules" v-model="model.rules" maxlength="2000" rows="3"/>
          </div>

          <div class="field">
            <CalendarInputBlock v-model="calendarStartValue"
                                :submitted="submitted"
                                label="Начало этапа"/>
          </div>

          <div class="field">
            <label for="end">Конец</label>
            <Calendar
              v-model="calendarEndValue"
              :invalid="submitted && !calendarEndValue"
              :showButtonBar="true"
              :showIcon="true"
              :showSeconds="true"
              :showTime="true"
              date-format="dd.mm.yy"
              mask="99.99.9999 99:99:99"
              selectionMode="single"
            ></Calendar>
            <small v-if="submitted && !calendarEndValue" class="p-invalid">Введите дату и время конца этапа.</small>
          </div>

          <div class="field">
            <label for="rules">Формула</label>
            <Textarea id="rules" v-model="model.seasonResultFormula" cols="20" disabled rows="3"/>
          </div>

          <div class="field">
            <ViewStateInput v-model="model.seasonVisibility" :is-read-view-only="true"
                            :submitted="submitted" label="Видимость этапа участниками"/>
          </div>

          <div class="field">
            <label for="scoreVisibility">Видимость оценок этапа участниками</label>
            <Dropdown id="scoreVisibility" v-model="model.scoreVisibility" :invalid="submitted && !season.scoreVisibility" :options="statuses"
                      option-value="value" optionLabel="label"
                      placeholder="Выберите ограничение">
              <template #value="slotProps">
                <span v-if="slotProps.value">{{ allowStateService.getBadgeContentFor(slotProps.value) }}</span>
                <span v-else> {{ slotProps.placeholder }} </span>
              </template>
              <template #option="slotProps">
                <Tag :severity="allowStateService.getBadgeSeverityFor(slotProps.option.value)"
                     :value="slotProps.option.label"/>
              </template>
            </Dropdown>
            <small v-if="submitted && !model.seasonVisibility" class="p-invalid">Выберите один из вариантов.</small>
          </div>

          <template #footer>
            <Button icon="pi pi-times" label="Отменить" text @click="hideDialog"/>
            <Button icon="pi pi-check" label="Сохранить" @click="saveModel"/>
          </template>
        </Dialog>

      </div>
    </div>
  </div>
</template>
