<script setup>
import {FilterMatchMode, FilterOperator} from 'primevue/api';
import {onBeforeMount, onMounted, ref} from 'vue';
import {useToast} from 'primevue/usetoast';
import {AllowStateService} from '@/service/util/AllowStateService';
import {DateTimeService} from '@/service/util/DateTimeService';
import {StageService} from "@/service/admin/StageService";
import SelectIdByTitleBlock from "@/components/prefab/SelectIdByTitleBlock.vue";
import {CriteriaService} from "@/service/admin/CriteriaService";

const toast = useToast();

const loading = ref(false);
const lazyParams = ref({});
const first = ref(0);
const totalRecords = ref(0);
const selectAll = ref(false);

const dt = ref(null);

const parentStage = ref(null);
const stageSelectData = ref(null);

const stageService = new StageService();
const criteriaService = new CriteriaService();
const allowStateService = new AllowStateService();
const dateTimeService = new DateTimeService();

const models = ref(null);
const editDialog = ref(false);
const model = ref({});
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
  stageService.getAllForSelect().then((data) => {
    if (data && !data.err) stageSelectData.value = data;
    else toast.add({
      severity: 'error',
      summary: 'Ошибка сервера',
      detail: 'Не удалось получить информацию о критериях',
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
    max: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    min: {operator: FilterOperator.AND, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]}
  };
};

const changeSeason = () => {
  loadLazyData();
}

const loadLazyData = (event) => {
  loading.value = true;
  lazyParams.value = {...lazyParams.value, first: event?.first || first.value};

  criteriaService.getAll(lazyParams.value, parentStage.value).then((data) => {
    models.value = data.collection.map((val) => createModelClient(val));
    totalRecords.value = data.total;
    loading.value = false;
  })
};

const createModelClient = (seasonServer) => {
  return {
    ...seasonServer
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
  if (!parentStage.value) {
    toast.add({severity: 'error', summary: 'Ошибка', detail: 'Сначала выберите родительский этап', life: 3000});
    return;
  }
  model.value = {
    min: 0,
    max: 0
  };
  submitted.value = false;
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
    model.value.title && model.value.title.trim() !== ''
    //season.value.description &&
    //season.value.rules &&
    // && season.value.seasonResultFormula
    && (model.value.min !== null && model.value.min !== undefined && model.value.max !== null && model.value.max !== undefined && model.value.min <= model.value.max)
  );
};

const createModelDto = () => {
  return {
    comment: model.value.comment ? model.value.comment : '',
    title: model.value.title,
    description: model.value.description ? model.value.description : '',
    rules: model.value.rules ? model.value.rules : '',
    literal: model.value.literal,
    max: model.value.max,
    min: model.value.min
  };
};

const saveModel = async () => {
  submitted.value = true;

  if (validateInput()) {
    if (model.value.id) {
      try {
        const res = await criteriaService.edit(model.value.id, createModelDto());
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
        const res = await stageService.addCriteria(parentStage.value, createModelDto());
        if (res.err) {
          console.error(res);
          toast.add({severity: 'error', summary: 'Ошибка сервера', detail: 'Критерий не создан', life: 3000});
          return;
        }

        model.value = createModelClient(res);
        models.value.push(model.value);
        toast.add({severity: 'success', summary: 'Успешно', detail: 'Критерий добавлен', life: 3000});
      } catch (e) {
        console.error(e);
        toast.add({severity: 'error', summary: 'Ошибка клиента', detail: 'Критерий не создан', life: 3000});
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
    const res = criteriaService.delete(model.value.id);
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
      const res = criteriaService.delete(val.id);
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
              <SelectIdByTitleBlock v-model="parentStage" :crudService="stageService" infix="stage"
                                    label="Родительский этап: " style="min-width: 10em; max-width: 20em;"
                                    @change="changeSeason"/>
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
              <h5 class="m-0">Управление критериями</h5>
              <Button icon="pi pi-filter-slash" label="Сбросить фильтры" outlined type="button"
                      @click="clearFilter()"/>
            </div>
          </template>
          <template #empty> Критериев не найдено.</template>

          <Column headerStyle="width: 3rem" selectionMode="multiple"></Column>

          <Column :sortable="true" dataType="numeric" field="id" header="ID"
                  headerStyle="width:15%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.id }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" mode="decimal" @keydown.enter="filterCallback()"/>
            </template>
          </Column>

          <Column :sortable="true" field="title" header="Название" headerStyle="width:55%; min-width:10rem;">
            <template #body="slotProps">{{ slotProps.data.title }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputText v-model="filterModel.value" class="p-column-filter" placeholder="Искать по названию"
                         type="text" @keydown.enter="filterCallback()"/>
            </template>
          </Column>

          <Column :sortable="true" dataType="numeric" field="min" header="Мин."
                  headerStyle="width:15%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.min }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" mode="decimal" @keydown.enter="filterCallback()"/>
            </template>
          </Column>

          <Column :sortable="true" dataType="numeric" field="max" header="Макс."
                  headerStyle="width:15%; min-width:5rem;">
            <template #body="slotProps">{{ slotProps.data.max }}</template>
            <template #filter="{ filterModel, filterCallback }">
              <InputNumber v-model="filterModel.value" mode="decimal" @keydown.enter="filterCallback()"/>
            </template>
          </Column>

          <Column header="Действия" headerStyle="width:10%;min-width:11rem;">
            <template #body="slotProps">
              <RouterLink :to="'/admin/criteria/' + slotProps.data.id">
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
                header="Информация о критерии">
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
            <InputNumberBlock v-model="model.min" :invalid="submitted && (model.min === null || model.min === undefined || model.min > model.max)"
                              label="Минимальное значение"/>
          </div>

          <div class="field">
            <InputNumberBlock v-model="model.max" :invalid="submitted && (model.max === null || model.max === undefined || model.min > model.max)"
                              label="Максимальное значение"/>
          </div>

          <template #footer>
            <Button icon="pi pi-times" label="Отменить" text @click="hideDialog"/>
            <Button icon="pi pi-check" label="Сохранить" @click="saveModel"/>
          </template>
        </Dialog>

        <Dialog v-model:visible="deleteModelDialog" :modal="true" :style="{ width: '700px' }" header="Подтверждение">
          <div class="flex align-items-center justify-content-center">
            <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
            <span v-if="model">Вы точно хотите удалить <b>{{ model.title }} <small>ID: {{ model.id }}</small></b> и <u>все</u> связанные данные?</span>
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
