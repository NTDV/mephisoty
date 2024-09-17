<script setup>
import { FilterMatchMode } from 'primevue/api';
import { onBeforeMount, onMounted, ref } from 'vue';
import { DateTimeService } from '@/service/util/DateTimeService';
import { HackathonStageService } from '@/service/admin/stages/HackathonStageService';

const loading = ref(false);
const lazyParams = ref({});
const first = ref(0);
const totalRecords = ref(0);
const selectAll = ref(false);

const dt = ref(null);

const hackathonStageService = new HackathonStageService();
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
    global: { value: null, matchMode: FilterMatchMode.CONTAINS }
  };
};
const loadLazyData = (event) => {
  loading.value = true;
  lazyParams.value = { ...lazyParams.value, first: event?.first || first.value };

  hackathonStageService.getAll(lazyParams.value).then((data) => {
    models.value = data.collection.map((val) => createModelClient(val));
    totalRecords.value = data.total;
    loading.value = false;
  });
};

const createModelClient = (modelServer) => {
  return {
    ...modelServer,
    fullName: (modelServer.secondName + ' ' + modelServer.firstName + ' ' + modelServer.thirdName).trim(),
    prettyTgNick: modelServer.tg == '' ? undefined : 'https://t.me/' + modelServer.tg
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
              <h5 class="m-0">Управление участниками Хакатона</h5>
              <Button icon="pi pi-filter-slash" label="Сбросить фильтры" outlined type="button"
                      @click="clearFilter()" />
            </div>
          </template>
          <template #empty> Участников не найдено.</template>

          <Column dataType="numeric" field="userId" header="ID"
                  headerStyle="width:10%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.userId }}</template>
          </Column>
          <Column field="fullName" header="Имя" headerStyle="width:30%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.fullName }}</template>
          </Column>

          <Column field="groupTitle" header="Группа" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.groupTitle }}</template>
          </Column>
          <Column field="filial" header="Филиал" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.filial }}</template>
          </Column>

          <Column field="prettyTgNick" header="ТГ-контакт" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps"><a :href="slotProps.data.prettyTgNick">{{ slotProps.data.tg }}</a>
            </template>
          </Column>
          <Column field="email" header="Почта" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps"><a :href="'mailto:' + slotProps.data.email">{{ slotProps.data.email }}</a>
            </template>
          </Column>

          <Column field="task" header="Задача" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.task }}</template>
          </Column>
          <Column field="commandTitle" header="Команда" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.commandTitle }}</template>
          </Column>
        </DataTable>
      </div>
    </div>
  </div>
</template>
