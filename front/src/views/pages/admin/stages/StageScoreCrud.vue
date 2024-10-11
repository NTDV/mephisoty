<script setup>
import { FilterMatchMode, FilterOperator } from 'primevue/api';
import { onBeforeMount, onMounted, ref } from 'vue';
import { ParticipantStateService } from '@/service/util/ParticipantStateService';
import SelectIdByTitleBlock from '@/components/prefab/SelectIdByTitleBlock.vue';
import { StageService } from '@/service/admin/StageService';
import { StageScoreService } from '@/service/admin/stages/StageScoreService';
import { useToast } from 'primevue/usetoast';

const loading = ref(false);
const lazyParams = ref({});
const first = ref(0);
const totalRecords = ref(0);
const selectAll = ref(false);

const dt = ref(null);

const stageScoreService = new StageScoreService();
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

    'participant.fullName': {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }]
    },
    'participant.group.title': {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }]
    },
    'participant.state': {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }]
    },

    initialScore: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.GREATER_THAN_OR_EQUAL_TO }]
    },
    scoreByStageFormula: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.GREATER_THAN_OR_EQUAL_TO }]
    },

    'participant.vkNick': {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }]
    },
    'participant.tgNick': {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }]
    },
    'participant.phoneNumber': {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }]
    },

    place: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] }
  };
};
const loadLazyData = (event) => {
  loading.value = true;
  lazyParams.value = { ...lazyParams.value, first: event?.first || first.value };

  stageScoreService.getAll(lazyParams.value, parentStage.value).then((data) => {
    models.value = data.collection.map((val) => createModelClient(val));
    totalRecords.value = data.total;
    loading.value = false;
  });
};

const createModelClient = (seasonServer) => {
  return {
    ...seasonServer,
    participant: {
      ...seasonServer.participant,
      fullName: (seasonServer.participant.secondName + ' ' + seasonServer.participant.firstName + ' ' + seasonServer.participant.thirdName).trim(),
      prettyVkNick: seasonServer.participant.vkNick,
      vkNick: seasonServer.participant.vkNick == '' ? undefined : 'https://vk.com/' + seasonServer.participant.vkNick,
      prettyTgNick: seasonServer.participant.tgNick,
      tgNick: seasonServer.participant.tgNick == '' ? undefined : 'https://t.me/' + seasonServer.participant.tgNick
    },
    prettyState: participantStateService.getContentFor(seasonServer.participant.state)
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

const toast = useToast();
const evaluate = () => {
  if (!parentStage.value) {
    toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Не выбран этап', life: 3000 });
    return;
  }

  stageScoreService.evaluate(parentStage.value).then(() => {
    toast.add({
      severity: 'success',
      summary: 'Успешно',
      detail: 'Оценки пересчитываются. Обновите страницу через несколько минут.',
      life: 5000
    });
  });
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

          <template v-slot:center>
            <div class="my-2">
              <Button class="mr-2 mb-2" icon="pi pi-reload" label="Пересчитать" severity="warning"
                      @click="evaluate" />
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
              <h5 class="m-0">Управление оценками за этап</h5>
              <Button icon="pi pi-filter-slash" label="Сбросить фильтры" outlined type="button"
                      @click="clearFilter()" />
            </div>
          </template>
          <template #empty> Оценок не найдено.</template>

          <Column :sortable="true" dataType="numeric" field="id" header="ID"
                  headerStyle="width:5%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.id }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" mode="decimal" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="participant.group.title" header="Группа"
                  headerStyle="width:5%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.participant?.group?.title ?? 'БЕЗ ГРУППЫ' }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по группе"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="participant.fullName" header="Имя" headerStyle="width:20%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.participant.fullName }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по имени"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" data-type="numeric" field="place" header="Место"
                  headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.place }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" mode="decimal" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" data-type="numeric" field="scoreByStageFormula" header="Втор. балл"
                  headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">{{ Math.round((slotProps.data.scoreByStageFormula + Number.EPSILON) * 100) / 100
              }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" max-fraction-digits="2" mode="decimal"
                           @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" data-type="numeric" field="initialScore" header="Перв. балл"
                  headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">{{ Math.round((slotProps.data.initialScore + Number.EPSILON) * 100) / 100 }}
            </template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" max-fraction-digits="2" mode="decimal"
                           @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column :sortable="true" field="participant.vkNick" header="Ник ВК" headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">
              <a :href="slotProps.data.participant.vkNick">{{ slotProps.data.participant.prettyVkNick }}</a>
            </template>
          </Column>

          <Column :sortable="true" field="participant.tgNick" header="ТГ-контакт"
                  headerStyle="width:10%; min-width:10rem;">
            <template #body="slotProps">
              <a :href="slotProps.data.participant.tgNick">{{ slotProps.data.participant.prettyTgNick }}</a>
            </template>
          </Column>

          <Column :sortable="true" field="participant.phoneNumber" header="Номер телефона"
                  headerStyle="width:10%; min-width:12rem;">
            <template #body="slotProps">{{ slotProps.data.participant.phoneNumber }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputMask v-model="filterModel.value" class="p-column-filter" mask="+7 (999) 999-99-99"
                         placeholder="+7 (123) 456-78-90"
                         type="text" @keydown.enter="filterCallback()" />
            </template>
          </Column>

          <Column field="prettyState" header="Статус" headerStyle="width:15%; min-width:15rem;">
            <template #body="slotProps">{{ slotProps.data.prettyState }}</template>
          </Column>
        </DataTable>
      </div>
    </div>
  </div>
</template>
