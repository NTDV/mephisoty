<script setup>
import { FilterMatchMode, FilterOperator } from 'primevue/api';
import { onBeforeMount, onMounted, ref } from 'vue';
import { ParticipantsStageService } from '@/service/admin/stages/ParticipantsStageService';
import { ParticipantStateService } from '@/service/util/ParticipantStateService';
import SelectIdByTitleBlock from '@/components/prefab/SelectIdByTitleBlock.vue';
import { StageService } from '@/service/admin/StageService';

const loading = ref(false);
const lazyParams = ref({});
const first = ref(0);
const totalRecords = ref(0);
const selectAll = ref(false);

const dt = ref(null);

const participantsStageService = new ParticipantsStageService();
const participantStateService = new ParticipantStateService();

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
    phoneNumber: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
    chosenStages: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.CONTAINS }] }
  };
};
const loadLazyData = (event) => {
  loading.value = true;
  lazyParams.value = { ...lazyParams.value, first: event?.first || first.value };

  participantsStageService.getAll(lazyParams.value, parentStage.value).then((data) => {
    models.value = data.collection.map((val) => createModelClient(val));
    totalRecords.value = data.total;
    loading.value = false;
  });
};

const createModelClient = (seasonServer) => {
  return {
    ...seasonServer,
    prettyState: participantStateService.getContentFor(seasonServer.state),
    prettyVkNick: seasonServer.vkNick == '' ? undefined : 'https://vk.com/' + seasonServer.vkNick,
    prettyTgNick: seasonServer.tgNick == '' ? undefined : 'https://t.me/' + seasonServer.tgNick,
    prettyChosenStages: seasonServer.chosenStages.map(stage => stage.title).join(', ')
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

const parentStage = ref(null);
const stageService = new StageService();
const changeSeason = () => {
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
              <SelectIdByTitleBlock v-model="parentStage" :crudService="stageService" infix="stage"
                                    label="Родительский этап: " style="min-width: 10em; max-width: 20em;"
                                    @change="changeSeason" />
            </div>
          </template>
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
          :global-filter-fields="['id', 'title']"
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
              <h5 class="m-0">Управление участниками</h5>
              <Button icon="pi pi-filter-slash" label="Сбросить фильтры" outlined type="button"
                      @click="clearFilter()" />
            </div>
          </template>
          <template #empty> Участников не найдено.</template>

          <Column :sortable="true" dataType="numeric" field="id" header="ID"
                  headerStyle="width:5%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.id }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" mode="decimal" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="group.title" header="Группа" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.group?.title ?? 'БЕЗ ГРУППЫ' }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по группе"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="fullName" header="Имя" headerStyle="width:20%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.fullName }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по имени"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column field="prettyChosenStages" header="Выбранные этапы" headerStyle="width: 20%; min-width:10rem;">
            <template #body="slotProps">
              <template v-for="stage in slotProps.data.chosenStages">
                <span class="text-nowrap">{{ stage.title }}</span> <br>
              </template>
            </template>
          </Column>

          <Column field="prettyState" header="Статус" headerStyle="width:15%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.prettyState }}</template>
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
