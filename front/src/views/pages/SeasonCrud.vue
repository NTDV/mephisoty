<script setup>
import {FilterMatchMode, FilterOperator} from 'primevue/api';
import {onBeforeMount, onMounted, ref} from 'vue';
import {useToast} from 'primevue/usetoast';
import {SeasonService} from '@/service/SeasonService';
import {AllowStateService} from '@/service/AllowStateService';
import {DateTimeService} from '@/service/DateTimeService';
import TextareaBlock from "@/components/prefab/TextareaBlock.vue";

const toast = useToast();

const loading = ref(false);
const lazyParams = ref({});
const first = ref(0);
const totalRecords = ref(0);
const selectAll = ref(false);

const dt = ref(null);

const seasonService = new SeasonService();
const allowStateService = new AllowStateService();
const dateTimeService = new DateTimeService();

const seasons = ref(null);
const seasonDialog = ref(false);
const season = ref({});
const calendarStartValue = ref(null);
const calendarEndValue = ref(null);
const deleteSeasonDialog = ref(false);
const deleteSeasonsDialog = ref(false);

const selectedSeasons = ref(null);
const filters = ref({});
const submitted = ref(false);

const statuses = ref(allowStateService.getBadgeContentViewOnly());

const getBadgeSeverityForState = (state) => allowStateService.getBadgeSeverityFor(state);
const getBadgeContentForState = (state) => allowStateService.getBadgeContentFor(state);
const getDateTimeOffsetFromDate = dateTimeService.getDateTimeOffsetFromDate;
const formatDateTimeFromDate = dateTimeService.formatDateTimeFromDate;
const getDateFromTimestamp = dateTimeService.getDateFromTimestamp;

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
  loadLazyData();
});

const loadLazyData = (event) => {
  loading.value = true;
  lazyParams.value = {...lazyParams.value, first: event?.first || first.value};

  seasonService.getAll(lazyParams.value).then((data) => {
    seasons.value = data.collection.map((val) => createSeasonClient(val));
    totalRecords.value = data.total;
    loading.value = false;
  });
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
    selectedSeasons.value = seasons.value;
  } else {
    selectAll.value = false;
    selectedSeasons.value = [];
  }
};

const onRowSelect = () => {
  selectAll.value = selectedSeasons.value.length === totalRecords.value;
};
const onRowUnselect = () => {
  selectAll.value = false;
};

const openNew = () => {
  season.value = {
    seasonVisibility: allowStateService.defaultState,
    scoreVisibility: allowStateService.defaultState,
  };
  submitted.value = false;
  calendarStartValue.value = dateTimeService.getDateNow();
  calendarEndValue.value = dateTimeService.getDateNow();
  seasonDialog.value = true;
};

const hideDialog = () => {
  seasonDialog.value = false;
  submitted.value = false;
};

const validateInput = () => {
  return (
    season.value &&
    //season.value.comment &&
    season.value.title &&
    season.value.title.trim() !== '' &&
    //season.value.description &&
    //season.value.rules &&
    calendarStartValue.value &&
    calendarEndValue.value &&
    // && season.value.seasonResultFormula
    season.value.seasonVisibility &&
    season.value.scoreVisibility
  );
};

const createSeasonDto = () => {
  return {
    comment: season.value.comment ? season.value.comment : '',
    title: season.value.title,
    description: season.value.description ? season.value.description : '',
    rules: season.value.rules ? season.value.rules : '',
    start: getDateTimeOffsetFromDate(calendarStartValue.value),
    end: getDateTimeOffsetFromDate(calendarEndValue.value),
    seasonResultFormula: season.value.seasonResultFormula,
    seasonVisibility: season.value.seasonVisibility,
    scoreVisibility: season.value.scoreVisibility
  };
};

const createSeasonClient = (seasonServer) => {
  return {
    ...seasonServer,
    start: getDateFromTimestamp(seasonServer.start),
    end: getDateFromTimestamp(seasonServer.end)
  };
};

const saveSeason = async () => {
  submitted.value = true;

  if (validateInput()) {
    if (season.value.id) {
      try {
        const res = await seasonService.edit(season.value.id, createSeasonDto());
        if (res.err) {
          console.error(res);
          toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Сезон не изменен', life: 3000});
          return;
        }

        season.value = createSeasonClient(res);
        seasons.value[findSeasonIndexById(season.value.id)] = season.value;
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

        season.value = createSeasonClient(res);
        seasons.value.push(season.value);
        toast.add({severity: 'success', summary: 'Успешно', detail: 'Сезон добавлен', life: 3000});
      } catch (e) {
        console.error(e);
        toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Сезон не создан', life: 3000});
        return;
      }
    }

    seasonDialog.value = false;
    season.value = {};
  } else {
    toast.add({severity: 'warn', summary: 'Внимание', detail: 'Неверное заполнение', life: 3000});
  }
};

const editSeason = (editSeason) => {
  season.value = {...editSeason};
  calendarStartValue.value = editSeason.start;
  calendarEndValue.value = editSeason.end;

  seasonDialog.value = true;
};

const findSeasonIndexById = (id) => {
  let index = -1;
  for (let i = 0; i < seasons.value.length; i++) {
    if (seasons.value[i].id === id) {
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
  season.value = deleteSeason;
  deleteSeasonDialog.value = true;
};

const deleteSeason = () => {
  try {
    const res = seasonService.delete(season.value.id);
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

  seasons.value = seasons.value.filter((val) => val.id !== season.value.id);
  deleteSeasonDialog.value = false;
  season.value = {};
  toast.add({severity: 'success', summary: 'Успешно', detail: 'Сезон удален', life: 3000});
};

const confirmDeleteSelected = () => {
  deleteSeasonsDialog.value = true;
};

const deleteSelectedSeasons = () => {
  try {
    for (const val of selectedSeasons.value) {
      const res = seasonService.delete(val.id);
      if (res == null || res.err) {
        console.error(res);
        toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Часть сезонов не удалена', life: 3000});
        return;
      }
    }
  } catch (e) {
    console.error(e);
    toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Часть сезонов не удалена', life: 3000});
    return;
  }

  seasons.value = seasons.value.filter((val) => !selectedSeasons.value.includes(val));
  deleteSeasonsDialog.value = false;
  selectedSeasons.value = null;
  toast.add({severity: 'success', summary: 'Успешно', detail: 'Сезон удален', life: 3000});
};

const clearFilter = () => {
  initFilters();
  lazyParams.value.filters = filters.value;
  loadLazyData();
};

const initFilters = () => {
  filters.value = {
    global: {value: null, matchMode: FilterMatchMode.CONTAINS},
    id: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    title: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.STARTS_WITH}]},
    start: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.DATE_IS}]},
    end: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.DATE_IS}]},
    seasonVisibility: {operator: FilterOperator.OR, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    scoreVisibility: {operator: FilterOperator.OR, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]}
  };
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

          </template>

          <template v-slot:end>
            <div>
              <Button class="mr-2 mb-2" icon="pi pi-upload" label="Экспорт" severity="help" @click="exportCSV($event)"/>
              <Button class="mr-2 mb-2" icon="pi pi-plus" label="Добавить" severity="success" @click="openNew"/>
              <Button :disabled="!selectedSeasons || !selectedSeasons.length" class="mb-2" icon="pi pi-trash" label="Удалить" severity="danger"
                      @click="confirmDeleteSelected"/>
            </div>
          </template>
        </Toolbar>

        <DataTable
          ref="dt"
          v-model:filters="filters"
          v-model:selection="selectedSeasons"
          :first="first"
          :global-filter-fields="['id', 'title']"
          :loading="loading"
          :paginator="true"
          :rows="10"
          :rowsPerPageOptions="[5, 10, 25, 50, 100, 500, 1000]"
          :selectAll="selectAll"
          :totalRecords="totalRecords"
          :value="seasons"
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
              <h5 class="md:m-0 mb-2">Управление сезонами</h5>
              <Button icon="pi pi-filter-slash" label="Сбросить фильтры" outlined type="button" @click="clearFilter()"/>
            </div>
          </template>
          <template #empty> Сезонов не найдено.</template>
          <template #loading> Загрузка. Пожалуйста, подождите.</template>

          <Column headerStyle="width: 3rem" selectionMode="multiple"></Column>

          <Column :sortable="true" dataType="numeric" field="id" header="ID" headerStyle="width:10%; min-width:5rem;">
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
              {{ formatDateTimeFromDate(slotProps.data.start) }}
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

          <Column :sortable="true" dataType="date" field="end" header="Конец" headerStyle="width:16%; min-width:10rem;">
            <template #body="slotProps">
              {{ formatDateTimeFromDate(slotProps.data.end) }}
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

          <Column :showFilterMatchModes="false" :sortable="true" field="seasonVisibility"
                  header="Видимость сезона участниками" headerStyle="width:16%; min-width:13rem;">
            <template #body="slotProps">
              <Tag :severity="getBadgeSeverityForState(slotProps.data.seasonVisibility)">
                {{ getBadgeContentForState(slotProps.data.seasonVisibility) }}
              </Tag>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <Dropdown v-model="filterModel.value" :options="statuses" class="p-column-filter" option-value="value"
                        optionLabel="label" placeholder="Выберите один" showClear @keydown.enter="filterCallback()">
                <template #option="slotProps">
                  <Tag :key="slotProps.option.value" :severity="getBadgeSeverityForState(slotProps.option.value)"
                       :value="slotProps.option.label"/>
                </template>
              </Dropdown>
            </template>
          </Column>

          <Column :showFilterMatchModes="false" :sortable="true" field="scoreVisibility"
                  header="Видимость оценок участниками" headerStyle="width:16%; min-width:13rem;">
            <template #body="slotProps">
              <Tag :severity="getBadgeSeverityForState(slotProps.data.scoreVisibility)">
                {{ getBadgeContentForState(slotProps.data.scoreVisibility) }}
              </Tag>
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <Dropdown v-model="filterModel.value" :options="statuses" class="p-column-filter" option-value="value"
                        optionLabel="label" placeholder="Выберите один" showClear @keydown.enter="filterCallback()">
                <template #option="slotProps">
                  <Tag :key="slotProps.option.value" :severity="getBadgeSeverityForState(slotProps.option.value)"
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
              <Button class="mr-2" icon="pi pi-pencil" rounded severity="warning" @click="editSeason(slotProps.data)"/>
              <Button class="mr-2" icon="pi pi-trash" rounded severity="danger"
                      @click="confirmDeleteSeason(slotProps.data)"/>
            </template>
          </Column>
        </DataTable>

        <Dialog v-model:visible="seasonDialog" :modal="true"
                :style="{ width: '700px' }" class="p-fluid"
                header="Информация о сезоне">
          <div class="field">
            <TextareaBlock v-model="season.comment" auto-resize
                           label="Комментарий" maxlength="200" rows="1"/>
          </div>

          <div class="field">
            <TextInputBlock v-model="season.title" :autofocus="true"
                            :invalid="submitted && !season.title"
                            label="Название"/>
          </div>

          <div class="field">
            <TextareaBlock v-model="season.description" label="Описание"/>
          </div>

          <div class="field">
            <TextareaBlock v-model="season.description" label="Правила"/>
          </div>

          <div class="field">
            <CalendarInputBlock v-model="calendarStartValue" :submitted="submitted" label="Начало сезона"/>
          </div>

          <div class="field">
            <CalendarInputBlock v-model="calendarEndValue" :submitted="submitted" label="Конец сезона"/>
          </div>

          <div class="field">
            <TextareaBlock v-model="season.seasonResultFormula" label="Формула" disabled />
          </div>

          <div class="field">
            <ViewStateInputBlock v-model="season.seasonVisibility" :is-read-view-only="true"
                                 :submitted="submitted" label="Видимость сезона участниками"/>
          </div>

          <div class="field">
            <ViewStateInputBlock v-model="season.scoreVisibility" :is-read-view-only="true"
                                 :submitted="submitted" label="Видимость итоговых оценок участниками"/>
          </div>

          <template #footer>
            <Button icon="pi pi-times" label="Отменить" text @click="hideDialog"/>
            <Button icon="pi pi-check" label="Сохранить" @click="saveSeason"/>
          </template>
        </Dialog>

        <Dialog v-model:visible="deleteSeasonDialog" :modal="true" :style="{ width: '700px' }" header="Подтверждение">
          <div class="flex align-items-center justify-content-center">
            <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
            <span v-if="season"
            >Вы точно хотите удалить сезон <b>{{ season.title }}</b> и <u>все связанные данные</u>?</span
            >
          </div>
          <template #footer>
            <Button icon="pi pi-times" label="Нет" @click="deleteSeasonDialog = false"/>
            <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteSeason"/>
          </template>
        </Dialog>

        <Dialog v-model:visible="deleteSeasonsDialog" :modal="true" :style="{ width: '700px' }" header="Подтверждение">
          <div class="flex align-items-center justify-content-center">
            <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
            <span v-if="seasons">Вы точно хотите удалить <b>выбранные сезоны</b> и <u>все связанные данные</u>?</span>
          </div>
          <template #footer>
            <Button icon="pi pi-times" label="Нет" @click="deleteSeasonsDialog = false"/>
            <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteSelectedSeasons"/>
          </template>
        </Dialog>
      </div>
    </div>
  </div>
</template>
