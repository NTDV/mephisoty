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

const toast = useToast();

const toastService = new ToastService(toast);
const stageService = new StageService();
const participantsService = new ParticipantsService();
const achievementTypeService = new AchievementTypeService();
const achievementService = new AchievementService();

const parentStage = ref(null);
const parentParticipant = ref(null);
const delimiter = ref(';');
const hasHeader = ref(true);
const loadingFile = ref(false);

const filters = ref({});
const selectedModels = ref(null);
const first = ref(0);
const loading = ref(false);
const selectAll = ref(false);
const totalRecords = ref(0);
const models = ref(null);
const lazyParams = ref({});

const types = ref(achievementTypeService.getBadgeContentAll());

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
  if (parentStage.value && parentParticipant.value) loadLazyData();
};

const uploader = async (event) => {
  if (!parentStage.value) {
    toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Выберите этап', life: 3000 });
    return;
  }
  if (loadingFile.value) return;


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
            <div class="my-2">
              <div>
                <label>Импортировать достижения</label>
                <FileUpload :maxFileSize="30 * (8 * 1024 * 1024)" :multiple="false" accept=".csv"
                            cancel-label="Отмена" choose-label="Выбрать файл" customUpload mode="basic"
                            name="file" upload-label="Импортировать" @uploader="uploader" />
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
              {{ slotProps.data.totalScore }}
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

          <Column :sortable="true" field="thanksFrom" header="Благодарность" headerStyle="width:10%; min-width:10rem;">
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
              <RouterLink :to="'/admin/stage/' + slotProps.data.id">
                <Button class="mr-2" icon="pi pi-eye" rounded severity="success" />
              </RouterLink>
              <Button class="mr-2" icon="pi pi-pencil" rounded severity="warning"
                      @click="editModel(slotProps.data)" />
              <Button class="mr-2" icon="pi pi-trash" rounded severity="danger"
                      @click="confirmDeleteModel(slotProps.data)" />
            </template>
          </Column>
        </DataTable>
      </div>
    </div>
  </div>
</template>
