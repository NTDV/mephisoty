<script setup>
import { FilterMatchMode, FilterOperator } from 'primevue/api';
import { ref, onMounted, onBeforeMount } from 'vue';
import { useToast } from 'primevue/usetoast';
import { SeasonService } from '@/service/SeasonService';
import { AllowStateService } from '@/service/AllowStateService';
import { DateTimeService } from '@/service/DateTimeService';

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
    lazyParams.value = { ...lazyParams.value, first: event?.first || first.value };

    setTimeout(
        () =>
            seasonService.getSeasons(lazyParams.value).then((data) => {
                seasons.value = data.collection.map((val) => createSeasonClient(val));
                totalRecords.value = data.total;
                loading.value = false;
            }),
        500
    );
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
        CustomerService.getCustomers().then((data) => {
            selectAll.value = true;
            selectedCustomers.value = data.customers;
        });
    } else {
        selectAll.value = false;
        selectedCustomers.value = [];
    }
};
const onRowSelect = () => {
    //selectAll.value = selectedSeasons.value.length === totalRecords.value;
};
const onRowUnselect = () => {
    selectAll.value = false;
};

const openNew = () => {
    season.value = {};
    submitted.value = false;
    calendarStartValue.value = null;
    calendarEndValue.value = null;
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
                const res = await seasonService.editSeason(season.value.id, createSeasonDto());
                if (res.err) {
                    console.error(res);
                    toast.add({ severity: 'error', summary: 'Ошибка сервера', detail: 'Сезон не изменен', life: 3000 });
                    return;
                }

                season.value = createSeasonClient(res);
                seasons.value[findSeasonIndexById(season.value.id)] = season.value;
                toast.add({ severity: 'success', summary: 'Успешно', detail: 'Сезон изменен', life: 3000 });
            } catch (e) {
                console.error(e);
                toast.add({ severity: 'error', summary: 'Ошибка клиента', detail: 'Сезон не изменен', life: 3000 });
                return;
            }
        } else {
            try {
                const res = await seasonService.createSeason(createSeasonDto());
                if (res.err) {
                    console.error(res);
                    toast.add({ severity: 'error', summary: 'Ошибка сервера', detail: 'Сезон не создан', life: 3000 });
                    return;
                }

                season.value = createSeasonClient(res);
                seasons.value.push(season.value);
                toast.add({ severity: 'success', summary: 'Успешно', detail: 'Сезон добавлен', life: 3000 });
            } catch (e) {
                console.error(e);
                toast.add({ severity: 'error', summary: 'Ошибка клиента', detail: 'Сезон не создан', life: 3000 });
                return;
            }
        }

        seasonDialog.value = false;
        season.value = {};
    } else {
        toast.add({ severity: 'warn', summary: 'Внимание', detail: 'Неверное заполнение', life: 3000 });
    }
};

const editSeason = (editSeason) => {
    season.value = { ...editSeason };
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
        const res = seasonService.deleteSeason(season.value.id);
        if (res == null || res.err) {
            console.error(res);
            toast.add({ severity: 'error', summary: 'Ошибка сервера', detail: 'Сезон не удален', life: 3000 });
            return;
        }
    } catch (e) {
        console.error(e);
        toast.add({ severity: 'error', summary: 'Ошибка клиента', detail: 'Сезон не удален', life: 3000 });
        return;
    }

    seasons.value = seasons.value.filter((val) => val.id !== season.value.id);
    deleteSeasonDialog.value = false;
    season.value = {};
    toast.add({ severity: 'success', summary: 'Успешно', detail: 'Сезон удален', life: 3000 });
};

const confirmDeleteSelected = () => {
    deleteSeasonsDialog.value = true;
};

const deleteSelectedSeasons = () => {
    try {
        for (const val of selectedSeasons.value) {
            const res = seasonService.deleteSeason(val.id);
            if (res == null || res.err) {
                console.error(res);
                toast.add({ severity: 'error', summary: 'Ошибка сервера', detail: 'Часть сезонов не удалена', life: 3000 });
                return;
            }
        }
    } catch (e) {
        console.error(e);
        toast.add({ severity: 'error', summary: 'Ошибка клиента', detail: 'Часть сезонов не удалена', life: 3000 });
        return;
    }

    seasons.value = seasons.value.filter((val) => !selectedSeasons.value.includes(val));
    deleteSeasonsDialog.value = false;
    selectedSeasons.value = null;
    toast.add({ severity: 'success', summary: 'Успешно', detail: 'Сезон удален', life: 3000 });
};

const clearFilter = () => {
    initFilters();
};

const initFilters = () => {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        id: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        title: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        start: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] },
        end: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] },
        seasonVisibility: { operator: FilterOperator.OR, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        scoreVisibility: { operator: FilterOperator.OR, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] }
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
                            <Button label="Добавить" icon="pi pi-plus" class="mr-2" severity="success" @click="openNew" />
                            <Button label="Удалить" icon="pi pi-trash" severity="danger" @click="confirmDeleteSelected" :disabled="!selectedSeasons || !selectedSeasons.length" />
                        </div>
                    </template>

                    <template v-slot:end>
                        <Button label="Экспорт" icon="pi pi-upload" severity="help" @click="exportCSV($event)" />
                    </template>
                </Toolbar>

                <DataTable
                    ref="dt"
                    lazy
                    :value="seasons"
                    v-model:selection="selectedSeasons"
                    :selectAll="selectAll"
                    @select-all-change="onSelectAllChange"
                    @row-select="onRowSelect"
                    @row-unselect="onRowUnselect"
                    dataKey="id"
                    :paginator="true"
                    :rows="10"
                    v-model:filters="filters"
                    :first="first"
                    :totalRecords="totalRecords"
                    :loading="loading"
                    @page="onPage($event)"
                    @sort="onSort($event)"
                    @filter="onFilter($event)"
                    filter-display="menu"
                    :global-filter-fields="['id', 'title']"
                    paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                    :rowsPerPageOptions="[5, 10, 25, 50, 100, 500, 1000]"
                    currentPageReportTemplate="Сезоны с {first} по {last} из {totalRecords} загруженных"
                    sortMode="multiple"
                >
                    <template #header>
                        <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center">
                            <h5 class="m-0">Управление сезонами</h5>
                            <IconField iconPosition="left" class="block mt-2 md:mt-0">
                                <InputIcon class="pi pi-search" />
                                <InputText class="w-full sm:w-auto" v-model="filters['global'].value" placeholder="Быстрый поиск..." />
                            </IconField>
                        </div>
                    </template>
                    <template #empty> Сезонов не найдено. </template>
                    <template #loading> Загрузка. Пожалуйста, подождите. </template>

                    <Column selectionMode="multiple" headerStyle="width: 3rem"></Column>

                    <Column field="id" header="ID" :sortable="true" dataType="numeric" headerStyle="width:10%; min-width:5rem;">
                        <template #body="slotProps">
                            {{ slotProps.data.id }}
                        </template>
                        <template #filter="{ filterModel, filterCallback }">
                            <InputNumber v-model="filterModel.value" @keydown.enter="filterCallback()" mode="decimal" />
                        </template>
                    </Column>

                    <Column field="title" header="Название" :sortable="true" headerStyle="width:14%; min-width:10rem;">
                        <template #body="slotProps">
                            {{ slotProps.data.title }}
                        </template>
                        <template #filter="{ filterModel, filterCallback }">
                            <InputText v-model="filterModel.value" @keydown.enter="filterCallback()" type="text" class="p-column-filter" placeholder="Искать по названию" />
                        </template>
                    </Column>

                    <Column field="start" dataType="date" header="Начало" :sortable="true" headerStyle="width:16%; min-width:10rem;">
                        <template #body="slotProps">
                            {{ formatDateTimeFromDate(slotProps.data.start) }}
                        </template>
                        <template #filter="{ filterModel, filterCallback }">
                            <Calendar
                                v-model="filterModel.value"
                                selectionMode="single"
                                date-format="dd.mm.yy"
                                :showTime="true"
                                hour-format="24"
                                :showSeconds="true"
                                :showIcon="true"
                                :showButtonBar="true"
                                placeholder="dd.mm.yyyy hh:mm:ss"
                                mask="99.99.9999 99:99:99"
                                @keydown.enter="filterCallback()"
                            />
                        </template>
                    </Column>

                    <Column field="end" dataType="date" header="Конец" :sortable="true" headerStyle="width:16%; min-width:10rem;">
                        <template #body="slotProps">
                            {{ formatDateTimeFromDate(slotProps.data.end) }}
                        </template>
                        <template #filter="{ filterModel, filterCallback }">
                            <Calendar
                                v-model="filterModel.value"
                                selectionMode="single"
                                date-format="dd.mm.yy"
                                :showTime="true"
                                hour-format="24"
                                :showSeconds="true"
                                :showIcon="true"
                                :showButtonBar="true"
                                placeholder="dd.mm.yyyy hh:mm:ss"
                                mask="99.99.9999 99:99:99"
                                @keydown.enter="filterCallback()"
                            />
                        </template>
                    </Column>

                    <Column field="seasonVisibility" header="Видимость сезона участниками" :sortable="true" :showFilterMatchModes="false" headerStyle="width:16%; min-width:13rem;">
                        <template #body="slotProps">
                            <Tag :severity="getBadgeSeverityForState(slotProps.data.seasonVisibility)">{{ getBadgeContentForState(slotProps.data.seasonVisibility) }}</Tag>
                        </template>
                        <template #filter="{ filterModel, filterCallback }">
                            <Dropdown v-model="filterModel.value" :options="statuses" optionLabel="label" option-value="value" placeholder="Выберите один" class="p-column-filter" showClear @keydown.enter="filterCallback()">
                                <template #option="slotProps">
                                    <Tag :key="slotProps.option.value" :value="slotProps.option.label" :severity="getBadgeSeverityForState(slotProps.option.value)" />
                                </template>
                            </Dropdown>
                        </template>
                    </Column>

                    <Column field="scoreVisibility" header="Видимость оценок участниками" :sortable="true" :showFilterMatchModes="false" headerStyle="width:16%; min-width:13rem;">
                        <template #body="slotProps">
                            <Tag :severity="getBadgeSeverityForState(slotProps.data.scoreVisibility)">{{ getBadgeContentForState(slotProps.data.scoreVisibility) }}</Tag>
                        </template>
                        <template #filter="{ filterModel, filterCallback }">
                            <Dropdown v-model="filterModel.value" :options="statuses" optionLabel="label" option-value="value" placeholder="Выберите один" class="p-column-filter" showClear @keydown.enter="filterCallback()">
                                <template #option="slotProps">
                                    <Tag :key="slotProps.option.value" :value="slotProps.option.label" :severity="getBadgeSeverityForState(slotProps.option.value)" />
                                </template>
                            </Dropdown>
                        </template>
                    </Column>

                    <Column header="Действия" headerStyle="width:10%;min-width:11rem;">
                        <template #body="slotProps">
                            <Button icon="pi pi-eye" class="mr-2" severity="success" rounded @click="viewSeason(slotProps.data.id)" />
                            <Button icon="pi pi-pencil" class="mr-2" severity="warning" rounded @click="editSeason(slotProps.data)" />
                            <Button icon="pi pi-trash" class="mr-2" severity="danger" rounded @click="confirmDeleteSeason(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>

                <Dialog v-model:visible="seasonDialog" :style="{ width: '700px' }" header="Информация о сезоне" :modal="true" class="p-fluid">
                    <div class="field">
                        <label for="description">Комментарий</label>
                        <Textarea id="description" v-model="season.comment" autoResize rows="1" maxlength="200" />
                    </div>

                    <div class="field">
                        <label for="title">Название</label>
                        <InputText id="title" v-model.trim="season.title" required="true" autofocus :invalid="submitted && !season.title" maxlength="120" />
                        <small class="p-invalid" v-if="submitted && (!season.title || season.title.trim() === '')">Введите название.</small>
                    </div>

                    <div class="field">
                        <label for="description">Описание</label>
                        <Textarea id="description" v-model="season.description" rows="3" cols="30" maxlength="2000" />
                    </div>

                    <div class="field">
                        <label for="rules">Правила</label>
                        <Textarea id="rules" v-model="season.rules" rows="3" maxlength="2000" />
                    </div>

                    <div class="field">
                        <label for="start">Начало</label>
                        <Calendar
                            selectionMode="single"
                            date-format="dd.mm.yy"
                            :invalid="submitted && !calendarStartValue"
                            :showTime="true"
                            hour-format="24"
                            :showSeconds="true"
                            :showIcon="true"
                            :showButtonBar="true"
                            v-model="calendarStartValue"
                            mask="99.99.9999 99:99:99"
                        ></Calendar>
                        <small class="p-invalid" v-if="submitted && !calendarStartValue">Введите дату и время начала сезона.</small>
                    </div>

                    <div class="field">
                        <label for="end">Конец</label>
                        <Calendar
                            selectionMode="single"
                            date-format="dd.mm.yy"
                            :invalid="submitted && !calendarEndValue"
                            :showTime="true"
                            :showSeconds="true"
                            :showIcon="true"
                            :showButtonBar="true"
                            v-model="calendarEndValue"
                            mask="99.99.9999 99:99:99"
                        ></Calendar>
                        <small class="p-invalid" v-if="submitted && !calendarEndValue">Введите дату и время конца сезона.</small>
                    </div>

                    <div class="field">
                        <label for="rules">Формула</label>
                        <Textarea id="rules" disabled v-model="season.seasonResultFormula" rows="3" cols="20" />
                    </div>

                    <div class="field">
                        <label for="seasonVisibility">Видимость сезона участниками</label>
                        <Dropdown id="seasonVisibility" v-model="season.seasonVisibility" :options="statuses" optionLabel="label" option-value="value" :invalid="submitted && !season.seasonVisibility" placeholder="Выберите ограничение">
                            <template #value="slotProps">
                                <span v-if="slotProps.value">{{ getBadgeContentForState(slotProps.value) }}</span>
                                <span v-else> {{ slotProps.placeholder }} </span>
                            </template>
                            <template #option="slotProps">
                                <Tag :value="slotProps.option.label" :severity="getBadgeSeverityForState(slotProps.option.value)" />
                            </template>
                        </Dropdown>
                        <small class="p-invalid" v-if="submitted && !season.seasonVisibility">Выберите один из вариантов.</small>
                    </div>

                    <div class="field">
                        <label for="scoreVisibility">Видимость оценок участниками</label>
                        <Dropdown id="scoreVisibility" v-model="season.scoreVisibility" :options="statuses" optionLabel="label" option-value="value" :invalid="submitted && !season.scoreVisibility" placeholder="Выберите ограничение">
                            <template #value="slotProps">
                                <span v-if="slotProps.value">{{ getBadgeContentForState(slotProps.value) }}</span>
                                <span v-else> {{ slotProps.placeholder }} </span>
                            </template>
                            <template #option="slotProps">
                                <Tag :value="slotProps.option.label" :severity="getBadgeSeverityForState(slotProps.option.value)" />
                            </template>
                        </Dropdown>
                        <small class="p-invalid" v-if="submitted && !season.seasonVisibility">Выберите один из вариантов.</small>
                    </div>

                    <template #footer>
                        <Button label="Отменить" icon="pi pi-times" text @click="hideDialog" />
                        <Button label="Сохранить" icon="pi pi-check" @click="saveSeason" />
                    </template>
                </Dialog>

                <Dialog v-model:visible="deleteSeasonDialog" :style="{ width: '700px' }" header="Подтверждение" :modal="true">
                    <div class="flex align-items-center justify-content-center">
                        <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
                        <span v-if="season"
                            >Вы точно хотите удалить сезон <b>{{ season.title }}</b> и <u>все связанные данные</u>?</span
                        >
                    </div>
                    <template #footer>
                        <Button label="Нет" icon="pi pi-times" @click="deleteSeasonDialog = false" />
                        <Button label="Да" icon="pi pi-trash" severity="danger" text @click="deleteSeason" />
                    </template>
                </Dialog>

                <Dialog v-model:visible="deleteSeasonsDialog" :style="{ width: '700px' }" header="Подтверждение" :modal="true">
                    <div class="flex align-items-center justify-content-center">
                        <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
                        <span v-if="seasons">Вы точно хотите удалить <b>выбранные сезоны</b> и <u>все связанные данные</u>?</span>
                    </div>
                    <template #footer>
                        <Button label="Нет" icon="pi pi-times" @click="deleteSeasonsDialog = false" />
                        <Button label="Да" icon="pi pi-trash" severity="danger" text @click="deleteSelectedSeasons" />
                    </template>
                </Dialog>
            </div>
        </div>
    </div>
</template>
