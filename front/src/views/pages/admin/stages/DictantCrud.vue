<script setup>
import { FilterMatchMode, FilterOperator } from 'primevue/api';
import { onBeforeMount, onMounted, ref } from 'vue';
import { DateTimeService } from '@/service/util/DateTimeService';
import { DictantStageService } from '@/service/admin/stages/DictantStageService';

const loading = ref(false);
const lazyParams = ref({});
const first = ref(0);
const totalRecords = ref(0);
const selectAll = ref(false);

const dt = ref(null);

const dictantStageService = new DictantStageService();
const dateTimeService = new DateTimeService();

const models = ref(null);

const selectedModels = ref(null);
const filters = ref({});

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

const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    id: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
    fullName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
    'group.title': {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }]
    },
    state: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
    prettyVkNick: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }]
    },
    tgNick: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
    phoneNumber: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] }
  };
};
const loadLazyData = (event) => {
  loading.value = true;
  lazyParams.value = { ...lazyParams.value, first: event?.first || first.value };

  dictantStageService.getAll(lazyParams.value).then((data) => {
    models.value = data.collection.map((val) => createModelClient(val));
    totalRecords.value = data.total;
    loading.value = false;
  });
};

const createModelClient = (seasonServer) => {
  return {
    ...seasonServer,
    'group.title': seasonServer.group,
    dateTimePretty: seasonServer.dateTime == null ? undefined : dateTimeService.formatDateTimeFromDate(dateTimeService.getDateFromTimestamp(seasonServer.dateTime)),
    prettyVkNick: seasonServer.vkNick == '' ? undefined : 'https://vk.com/' + seasonServer.vkNick,
    prettyTgNick: seasonServer.tgNick == '' ? undefined : 'https://t.me/' + seasonServer.tgNick
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
const exportCSV = () => {
  dt.value.exportCSV();
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
          <template v-slot:end>
            <div>
              <Button class="mr-2 mb-2" icon="pi pi-upload" label="Экспорт" severity="help"
                      @click="exportCSV($event)" />
            </div>
          </template>
        </Toolbar>

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
          csvSeparator=";"
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
              <h5 class="m-0">Управление участниками Диктанта победы</h5>
              <Button icon="pi pi-filter-slash" label="Сбросить фильтры" outlined type="button"
                      @click="clearFilter()" />
            </div>
          </template>
          <template #empty> Участников не найдено.</template>

          <Column :sortable="true" dataType="numeric" field="id" header="ID"
                  headerStyle="width:10%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.id }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" mode="decimal" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="group.title" header="Группа" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.group ?? 'БЕЗ ГРУППЫ' }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по группе"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="fullName" header="Имя" headerStyle="width:30%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.fullName }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по имени"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column field="dateTimePretty" header="Дата" headerStyle="width:20%; min-width:10rem;">
            <template #body="slotProps">
              {{ slotProps.data.dateTimePretty }}
            </template>
          </Column>

          <Column field="prettyVkNick" header="Ник ВК" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps"><a :href="slotProps.data.prettyVkNick">{{ slotProps.data.vkNick }}</a>
            </template>
          </Column>

          <Column field="prettyTgNick" header="ТГ-контакт" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps"><a :href="slotProps.data.prettyTgNick">{{ slotProps.data.tgNick }}</a>
            </template>
          </Column>

          <Column :sortable="true" field="phoneNumber" header="Номер телефона"
                  headerStyle="width:10%; min-width:12rem;">
            <template #body="slotProps">{{ slotProps.data.phoneNumber }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputMask v-model="filterModel.value" class="p-column-filter" mask="+7 (999) 999-99-99"
                         placeholder="+7 (123) 456-78-90"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>
        </DataTable>
      </div>
    </div>
  </div>
</template>
