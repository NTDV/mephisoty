<script setup>
import {FilterMatchMode, FilterOperator} from 'primevue/api';
import {onMounted, ref} from 'vue';
import {useToast} from 'primevue/usetoast';
import SelectIdByTitleBlock from "@/components/prefab/SelectIdByTitleBlock.vue";
import {ToastService} from "@/service/util/ToastService";
import UserNameIdBlock from "@/components/prefab/UserNameIdBlock.vue";
import {StageCriteriasScoreService} from "@/service/expert/StageCriteriasScoreService";
import {StageExpertService} from "@/service/expert/StageExpertService";

const toast = useToast();

const loading = ref(false);
const lazyParams = ref({});
const first = ref(0);

const parentId = ref(null);

const parentService = new StageExpertService();
const toastService = new ToastService(toast);

const userFilters = ref({});

onMounted(() => {
  loading.value = true;
  initFilters();
  lazyParams.value = {
    first: 0,
    rows: 10,
    sortField: null,
    sortOrder: null,
    filters: userFilters.value
  };
});

const initFilters = () => {
  userFilters.value = {
    'group.title': {operator: FilterOperator.OR, constraints: [{value: null, matchMode: FilterMatchMode.STARTS_WITH}]},
    id: {operator: FilterOperator.OR, constraints: [{value: null, matchMode: FilterMatchMode.EQUALS}]},
    name: {operator: FilterOperator.OR, constraints: [{value: null, matchMode: FilterMatchMode.STARTS_WITH}]},
    state: {
      operator: FilterOperator.OR,
      constraints: [{value: 'PARTICIPANT', label: 'Участник', matchMode: FilterMatchMode.EQUALS}]
    }
  };
};

const changeParent = () => {
  loadLazyData(true);
}

const loadLazyData = (event) => {
  if (loading.value && event !== true || userSort.value == null) return;
  loading.value = true;

  Object.keys(userFilters.value).forEach((key) => {
    if (key !== 'state' && userFilters.value[key].constraints[0].value == '')
      userFilters.value[key].constraints[0].value = null;
  })

  lazyParams.value = {
    ...lazyParams.value,
    first: event?.first || first.value,
    filters: userFilters.value,
    multiSortMeta: [userSort.value]
  };

  scoreService.getAll(lazyParams.value, parentId.value)
    .then(data => {
      if (toastService.checkServerError(data)) return;
      scores.value = data.scores;
      criterias.value = data.criterias;
      criteriasById = data.criterias.map(c => ({[c.id]: c})).reduce((a, b) => ({...a, ...b}));
      selectedCriterias.value = criterias.value;
      selectedCriteriasShadow.value = selectedCriterias.value;
      totalParticipants.value = data.totalParticipants;
    })
    .catch((e) => toastService.showClientError(e))
    .finally(() => loading.value = false);
};

const scores = ref();
const criterias = ref();
let criteriasById = {};
const selectedCriterias = ref();
const selectedCriteriasShadow = ref();
const totalParticipants = ref(0);
const scoreService = new StageCriteriasScoreService();
const oldValue = ref(0);
const userSort = ref({field: 'name', label: 'По имени', order: 1});

const participantStates = ref([
  {value: 'WINNER', label: 'Победитель', matchMode: FilterMatchMode.EQUALS},
  {value: 'FINAL_MEMBER', label: 'Финалист', matchMode: FilterMatchMode.EQUALS},
  {value: 'PARTICIPANT', label: 'Участник', matchMode: FilterMatchMode.EQUALS},
  {value: 'DISQUALIFIED', label: 'Дисквалифицирован', matchMode: FilterMatchMode.EQUALS},
  {value: 'NOT_PARTICIPANT', label: 'Не участник', matchMode: FilterMatchMode.EQUALS}
]);

const initInputValue = (event) => {
  const criteriaId = event.field;
  const obj = event.data.scoreById[criteriaId];
  if (obj == null) {
    oldValue.value = null;
    event.data.scoreById[criteriaId] = {
      comment: '',
      score: null
    };
  } else {
    oldValue.value = {...obj};
  }
};

const flushInputValue = (event) => {
  const criteriaId = event.field;
  const newValue = event.newData.scoreById[criteriaId];
  const participantId = event.newData.participant.id;
  if (oldValue.value?.score === newValue?.score && oldValue.value?.comment === newValue?.comment) return;

  loading.value = true;
  if (newValue?.score == null || newValue.score == '') {
    newValue.comment = '';
    scoreService.delete(criteriaId, participantId)
      .then(data => {
        if (!toastService.checkServerError(data))
          toastService.showDeletedSuccess();
      })
      .catch(e => toastService.showServerError(e))
      .finally(() => loading.value = false);
  } else {
    scoreService.setScore(criteriaId, participantId, newValue)
      .then(data => {
        if (!toastService.checkServerError(data))
          toastService.showEditedSuccess();
      })
      .catch(e => toastService.showServerError(e))
      .finally(() => loading.value = false);
  }
};

const onPage = (event) => {
  lazyParams.value = event;
  loadLazyData(event);
};

const onToggle = (val) => {
  selectedCriteriasShadow.value = criterias.value.filter(col => val.includes(col));
};

const deleteFast = (event, slotProps) => {
  event.stopPropagation();
  selectedCriteriasShadow.value = selectedCriteriasShadow.value.filter(e => e.id !== slotProps.item.id);
  selectedCriterias.value = selectedCriteriasShadow.value;
};

const isRed = (score, criteria) => {
  return score != null && (score < criteria.min || score > criteria.max);
}

const isBlue = (score) => {
  return score == null || score == '';
}

const getClassForCell = (data, criteria) => {
  const isScore = data.props.field == data.props.key;
  const isNotScore = criteria == null;
  if (isScore && isNotScore) return;

  let red, blue;
  if (isNotScore) {
    const values = data.instance.rowData.scoreById;
    let valuesCount = 0;

    red = false;
    for (const criteriaId in values) {
      if (values.hasOwnProperty(criteriaId)) {
        ++valuesCount;
        if (isRed(values[criteriaId]?.score, criteriasById[criteriaId])) {
          red = true;
          break;
        }
      }
    }

    blue = !red && values.length !== criterias.value.length;
  } else {
    const score = data.instance.rowData.scoreById[data.props.field]?.score;

    red = isRed(score, criteria);
    blue = !red && isBlue(score);
  }

  return {
    'bg-red-100': red,
    'bg-blue-50': blue
  };
};
</script>

<template>
  <div class="grid">
    <div class="col-12">
      <div class="card">
        <Toolbar class="mb-4">
          <template v-slot:start>
            <div class="my-2">
              <SelectIdByTitleBlock v-model="parentId" :crudService="parentService" _infix="stage"
                                    label="Родительский этап: " style="min-width: 10em; max-width: 20em;"
                                    @change="changeParent"/>
            </div>
          </template>
          <template v-slot:end>
            <div>
              <Button class="mr-2 mb-2" icon="pi pi-upload" label="Экспорт" severity="help" @click="exportCSV($event)"/>
            </div>
          </template>
        </Toolbar>

        <DataTable v-if="parentId && scores" :first="first" :loading="loading"
                   :pt="{table: { style: 'min-width: 10rem; height: fit-content' }, column: {headerCell: { class: 'pt-2 pb-2 pl-2 pr-2'}, bodyCell: (data) => ({class: [{ 'pt-0 pb-0 pl-2 pr-2': true, ...getClassForCell(data) }]})}}"
                   :rows="10" :rowsPerPageOptions="[5, 10, 25, 50, 100]" :totalRecords="totalParticipants"
                   :value="scores"
                   currentPageReportTemplate="Участники с {first} по {last} из {totalRecords} всего"
                   data-key="participant.id" editMode="cell" lazy paginator
                   paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                   scrollHeight="550px"
                   scrollable show-gridlines
                   @page="onPage($event)"
                   @cell-edit-complete="flushInputValue($event)"
                   @cell-edit-init="initInputValue($event)">
          <template #header>
            <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center mb-2">
              <h5 class="m-0">Баллы участников по критериям этапа</h5>
              <ButtonGroup>
                <Button icon="pi pi-search" label="Найти" outlined type="button" @click="loadLazyData"/>
                <Button icon="pi pi-filter-slash" label="Сбросить" outlined type="button"
                        @click="initFilters(); loadLazyData();"/>
              </ButtonGroup>
            </div>
            <div class="field grid">
              <label class="col-12 mb-2 md:col-2 md:mb-0 text-500 font-medium">Фильтр этапов</label>
              <div class="col-12 md:col-10">
                <MultiSelect :filter-fields="['title', 'id']" :modelValue="selectedCriteriasShadow" :options="criterias"
                             auto-filter-focus
                             class="mr-2 w-full" data-key="id" display="chip"
                             filter filter-match-mode="contains"
                             placeholder="Выберите этапы" @before-hide="selectedCriterias = selectedCriteriasShadow;"
                             @update:modelValue="onToggle">
                  <template #option="{ option }">
                    <div style="word-wrap: break-word; max-width: 86vw; text-wrap: wrap;">
                      {{ option.title }}<small class="greyid ml-2" style="display: inline-block;">id:&nbsp;{{
                        option.id
                      }}</small>
                    </div>
                  </template>
                  <template #removetokenicon="slotProps">
                    <svg aria-hidden="true" class="p-icon p-multiselect-token-icon" data-pc-section="removetokenicon"
                         fill="none" height="14"
                         viewBox="0 0 14 14" width="14" xmlns="http://www.w3.org/2000/svg"
                         @click="deleteFast($event, slotProps)">
                      <path clip-rule="evenodd"
                            d="M7 14C5.61553 14 4.26215 13.5895 3.11101 12.8203C1.95987 12.0511 1.06266 10.9579 0.532846 9.67879C0.00303296 8.3997 -0.13559 6.99224 0.134506 5.63437C0.404603 4.2765 1.07129 3.02922 2.05026 2.05026C3.02922 1.07129 4.2765 0.404603 5.63437 0.134506C6.99224 -0.13559 8.3997 0.00303296 9.67879 0.532846C10.9579 1.06266 12.0511 1.95987 12.8203 3.11101C13.5895 4.26215 14 5.61553 14 7C14 8.85652 13.2625 10.637 11.9497 11.9497C10.637 13.2625 8.85652 14 7 14ZM7 1.16667C5.84628 1.16667 4.71846 1.50879 3.75918 2.14976C2.79989 2.79074 2.05222 3.70178 1.61071 4.76768C1.16919 5.83358 1.05367 7.00647 1.27876 8.13803C1.50384 9.26958 2.05941 10.309 2.87521 11.1248C3.69102 11.9406 4.73042 12.4962 5.86198 12.7212C6.99353 12.9463 8.16642 12.8308 9.23232 12.3893C10.2982 11.9478 11.2093 11.2001 11.8502 10.2408C12.4912 9.28154 12.8333 8.15373 12.8333 7C12.8333 5.45291 12.2188 3.96918 11.1248 2.87521C10.0308 1.78125 8.5471 1.16667 7 1.16667ZM4.66662 9.91668C4.58998 9.91704 4.51404 9.90209 4.44325 9.87271C4.37246 9.84333 4.30826 9.8001 4.2544 9.74557C4.14516 9.6362 4.0838 9.48793 4.0838 9.33335C4.0838 9.17876 4.14516 9.0305 4.2544 8.92113L6.17553 7L4.25443 5.07891C4.15139 4.96832 4.09529 4.82207 4.09796 4.67094C4.10063 4.51982 4.16185 4.37563 4.26872 4.26876C4.3756 4.16188 4.51979 4.10066 4.67091 4.09799C4.82204 4.09532 4.96829 4.15142 5.07887 4.25446L6.99997 6.17556L8.92106 4.25446C9.03164 4.15142 9.1779 4.09532 9.32903 4.09799C9.48015 4.10066 9.62434 4.16188 9.73121 4.26876C9.83809 4.37563 9.89931 4.51982 9.90198 4.67094C9.90464 4.82207 9.84855 4.96832 9.74551 5.07891L7.82441 7L9.74554 8.92113C9.85478 9.0305 9.91614 9.17876 9.91614 9.33335C9.91614 9.48793 9.85478 9.6362 9.74554 9.74557C9.69168 9.8001 9.62748 9.84333 9.55669 9.87271C9.4859 9.90209 9.40996 9.91704 9.33332 9.91668C9.25668 9.91704 9.18073 9.90209 9.10995 9.87271C9.03916 9.84333 8.97495 9.8001 8.9211 9.74557L6.99997 7.82444L5.07884 9.74557C5.02499 9.8001 4.96078 9.84333 4.88999 9.87271C4.81921 9.90209 4.74326 9.91704 4.66662 9.91668Z"
                            fill="currentColor"
                            fill-rule="evenodd"></path>
                    </svg>
                  </template>
                  <template #chip="{value}">
                    {{ value.title }} <small class="greyid ml-2">id: {{ value.id }} </small>
                  </template>
                </MultiSelect>
              </div>
            </div>

            <span class="block text-500 font-medium mb-2">Фильтр участников</span>
            <div class="field grid">
              <div class="col-8 mb-2 md:col-3 md:mb-0">
                <label class="mb-2">Группа</label>
                <InputText v-model.trim="userFilters['group.title'].constraints[0].value" class="w-full"
                           type="text" @keyup.enter="loadLazyData()"/>
              </div>
              <div class="col-4 mb-2 md:col-2 md:mb-0">
                <label class="mb-2">ID</label>
                <InputText v-model.number="userFilters.id.constraints[0].value" class="w-full" min="0"
                           type="number" @keyup.enter="loadLazyData()"/>
              </div>
              <div class="col-12 mb-2 md:col-7 md:mb-0">
                <label class="mb-2">Имя</label>
                <InputText v-model.trim="userFilters.name.constraints[0].value" class="w-full"
                           type="text" @keyup.enter="loadLazyData()"/>
              </div>
              <div class="col-7 mt-2 mb-2 md:col-3 md:mb-0">
                <label class="mb-2">Сортировка</label>
                <SelectButton v-model="userSort" :options="[{field: 'name', label: 'По имени', order: 1},
                                         {field: 'group.title', label: 'По группе', order: 1}]" data-key="field"
                              option-label="label"
                              @change="() => loadLazyData()"/>
              </div>
              <div class="col-5 mb-2 md:col-2 md:mb-0 pt-2">
                <label class="mb-2">Порядок</label>
                <br>
                <ToggleButton :model-value="userSort.order === 1"
                              off-label="Убывание" on-label="Возрастание" style="word-break: break-word;"
                              @change="userSort.order = userSort.order === 1 ? 2 : 1; loadLazyData();"/>
              </div>
              <div class="col-12 mt-2 md:col-7 md:mb-0">
                <label class="mb-2">Уровень</label>
                <MultiSelect v-model="userFilters.state.constraints" :options="participantStates"
                             class="mr-2" data-key="value" display="chip"
                             option-label="label" placeholder="Выберите уровень" style="width: 100%;"
                             @change="loadLazyData()"/>
              </div>
            </div>
          </template>
          <Column :pt="{bodyCell: {style: 'min-width: min(20em,25vw); max-width: 30vw'}}" field="participant" frozen>
            <template #header>
              <div class="flex-1" style="text-align: right;">
                <b>Этапы&nbsp;&nbsp;➝</b><br><b>Участники&nbsp;↴</b>
              </div>
            </template>
            <template #body="{ data }">
              <div class="flex" style="justify-content: end;">
                <p style="text-align: right; min-width: 6em; word-wrap: break-word">
                  <b>
                    <UserNameIdBlock :user="data.participant" id-left/>
                  </b>
                </p>
              </div>
            </template>
            <template #empty>Не найдено.</template>
          </Column>
          <Column field="participant.group" header="Группа" style="width: 1%; text-align: left">
            <template #body="{data}">
              <span v-if="data.participant.group" style="text-wrap: nowrap;">{{ data.participant.group.title }}</span>
            </template>
          </Column>
          <Column v-for="criteria of selectedCriterias" :key="criteria.id"
                  :_header="criteria.title"
                  :_id="criteria.id"
                  :field="String(criteria.id)" :pt="{ bodyCell: (data) => ({class: [{ 'pt-0 pb-0 pl-2 pr-2': true, ...getClassForCell(data, criteria) }]})}" header-style="width: 5%;">
            <template #header="{column}">
              <p style="text-align: center; align-content: center; word-wrap: break-word;">
                <b><small class="greyid">id: {{ column.props._id }} </small> {{ column.props._header }}</b>
              </p>
            </template>
            <template #editor="{ data, field }">
              <ScoreEditorBlock v-model="data.scoreById[field]"/>
            </template>
            <template #body="{data, field}">
              <div v-tooltip.left="isRed(data.scoreById[field]?.score, criteria) ? 'Оценка вне диапазона критерия: от ' + criteria.min + ' до ' + criteria.max :
                                   isBlue(data.scoreById[field]?.score) ? 'Отсутствует оценка' : ''"
                   class="flex justify-content-center h-full"
                   style="align-items: center;">
                <span v-if="data.scoreById[field]?.comment" v-badge.info
                      v-tooltip.right="data.scoreById[field]?.comment"
                      class="p-1">
                  {{ data.scoreById[field]?.score }}
                </span>
                <span v-else>{{ data.scoreById[field]?.score }}</span>
              </div>
            </template>
          </Column>
        </DataTable>

      </div>
    </div>
  </div>
</template>
