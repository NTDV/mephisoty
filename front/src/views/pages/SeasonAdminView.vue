<script setup>
import {ref, onMounted} from 'vue';
import {useToast} from 'primevue/usetoast';
import {SeasonService} from '@/service/SeasonService';
import {AllowStateService} from '@/service/AllowStateService';
import {DateTimeService} from '@/service/DateTimeService';
import {useRoute} from "vue-router";
import {CredsService} from "@/service/CredsService";
import CreatedModifiedBlock from "@/components/CreatedModifiedBlock.vue";

const toast = useToast();

const loading = ref(false);

const dt = ref(null);

const seasonService = new SeasonService();
const allowStateService = new AllowStateService();
const dateTimeService = new DateTimeService();

const season = ref({});
const calendarStartValue = ref(null);
const calendarEndValue = ref(null);
const seasonDialog = ref(false);
const deleteSeasonDialog = ref(false);

const submitted = ref(false);

const statuses = ref(allowStateService.getBadgeContentViewOnly());

const route = useRoute();

onMounted(() => {
    loading.value = true;
    setTimeout(() => seasonService.get(route.params.id)
        .then((data) => {
            season.value = createSeasonClient(data);
            const creatorIsEditor = season.value.createdBy === season.value.modifiedBy;

            setTimeout(() => credsService.getFullName(season.value.createdBy)
                .then((data) => {
                    if (!data.err) season.value.createdBy = data;
                    else console.error(data);

                    if (creatorIsEditor) {
                        season.value.modifiedBy = data;
                        loading.value = false;
                    }
                }), 100);

            if (!creatorIsEditor) {
                setTimeout(() => credsService.getFullName(season.value.modifiedBy)
                    .then((data) => {
                        if (!data.err) season.value.lastModifiedBy = data;
                        else console.error(data);

                        loading.value = false;
                    }), 100);
            }
        }), 200);
});


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
        start: dateTimeService.getDateTimeOffsetFromDate(calendarStartValue.value),
        end: dateTimeService.getDateTimeOffsetFromDate(calendarEndValue.value),
        seasonResultFormula: season.value.seasonResultFormula,
        seasonVisibility: season.value.seasonVisibility,
        scoreVisibility: season.value.scoreVisibility
    };
};

const createSeasonClient = (seasonServer) => {
    calendarStartValue.value = dateTimeService.getDateFromTimestamp(seasonServer.start);
    calendarEndValue.value = dateTimeService.getDateFromTimestamp(seasonServer.end);

    return {
        ...seasonServer,
        createdAt: dateTimeService.getDateFromTimestamp(seasonServer.createdAt),
        modifiedAt: dateTimeService.getDateFromTimestamp(seasonServer.modifiedAt),
        start: calendarStartValue.value,
        end: calendarEndValue.value
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

    deleteSeasonDialog.value = false;
    season.value = {};
    toast.add({severity: 'success', summary: 'Успешно', detail: 'Сезон удален', life: 3000});
};

const credsService = new CredsService();
</script>

<template>
    <div v-if="loading" class="grid">
        <Skeleton class="mb-2" height="2.5em" width="100%"></Skeleton>
    </div>
    <div v-else>
        <div class="col-12">
            <div class="card">
                <h5>Редактирование сезона</h5>
                <div class="p-fluid formgrid grid">
                    <div class="field col-12">
                        <CreatedModifiedBlock v-model="season"/>
                    </div>

                    <div class="field col-12">
                        <label for="comment">Комментарий</label>
                        <Textarea id="comment" v-model="season.comment" autoResize maxlength="200" rows="1"/>
                    </div>

                    <div class="field col-12">
                        <label for="title">Название</label>
                        <InputText id="title" v-model.trim="season.title" :invalid="submitted && !season.title" autofocus
                                   maxlength="120" required="true"/>
                        <small v-if="submitted && (!season.title || season.title.trim() === '')" class="p-invalid">Введите
                            название.</small>
                    </div>

                    <div class="field col-12">
                        <label for="description">Описание</label>
                        <Textarea id="description" v-model="season.description" class="fixed-resizable" cols="30"
                                  maxlength="2000" rows="3"/>
                    </div>

                    <div class="field col-12">
                        <label for="rules">Правила</label>
                        <Textarea id="rules" v-model="season.rules" class="fixed-resizable" maxlength="2000" rows="3"/>
                    </div>

                    <div class="field col-12 md:col-6">
                        <label for="start">Начало</label>
                        <Calendar
                            v-model="calendarStartValue"
                            :invalid="submitted && !calendarStartValue"
                            :showButtonBar="true"
                            :showIcon="true"
                            :showSeconds="true"
                            :showTime="true"
                            date-format="dd.mm.yy"
                            hour-format="24"
                            mask="99.99.9999 99:99:99"
                            selectionMode="single"
                        ></Calendar>
                        <small v-if="submitted && !calendarStartValue" class="p-invalid">Введите дату и время начала
                            сезона.</small>
                    </div>

                    <div class="field col-12 md:col-6">
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
                        <small v-if="submitted && !calendarEndValue" class="p-invalid">Введите дату и время конца
                            сезона.</small>
                    </div>

                    <div class="field col-12">
                        <label for="rules">Формула</label>
                        <Textarea id="rules" v-model="season.seasonResultFormula" cols="20" disabled rows="3"/>
                    </div>

                    <div class="field col-12 md:col-6">
                        <label for="seasonVisibility">Видимость сезона участниками</label>
                        <Dropdown id="seasonVisibility" v-model="season.seasonVisibility" :invalid="submitted && !season.seasonVisibility"
                                  :options="statuses" option-value="value"
                                  optionLabel="label" placeholder="Выберите ограничение">
                            <template #value="slotProps">
                                <span v-if="slotProps.value">{{
                                        allowStateService.getBadgeContentFor(slotProps.value)
                                    }}</span>
                                <span v-else> {{ slotProps.placeholder }} </span>
                            </template>
                            <template #option="slotProps">
                                <Tag :severity="allowStateService.getBadgeSeverityFor(slotProps.option.value)"
                                     :value="slotProps.option.label"/>
                            </template>
                        </Dropdown>
                        <small v-if="submitted && !season.seasonVisibility" class="p-invalid">Выберите один из
                            вариантов.</small>
                    </div>

                    <div class="field col-12 md:col-6">
                        <label for="scoreVisibility">Видимость оценок участниками</label>
                        <Dropdown id="scoreVisibility" v-model="season.scoreVisibility" :invalid="submitted && !season.scoreVisibility"
                                  :options="statuses" option-value="value"
                                  optionLabel="label" placeholder="Выберите ограничение">
                            <template #value="slotProps">
                                <span v-if="slotProps.value">{{
                                        allowStateService.getBadgeContentFor(slotProps.value)
                                    }}</span>
                                <span v-else> {{ slotProps.placeholder }} </span>
                            </template>
                            <template #option="slotProps">
                                <Tag :severity="allowStateService.getBadgeSeverityFor(slotProps.option.value)"
                                     :value="slotProps.option.label"/>
                            </template>
                        </Dropdown>
                        <small v-if="submitted && !season.seasonVisibility" class="p-invalid">Выберите один из
                            вариантов.</small>
                    </div>

                    <div class="field col-12">
                        <label for="comment">Этапы</label>
                    </div>

                    <div class="field col-12 md:col-4">
                        <Button class="sm:mb-2" icon="pi pi-check" label="Сохранить"/>
                    </div>
                    <div class="field col-12 md:col-4">
                        <Button class="mr-2 sm:mb-2" icon="pi pi-refresh" label="Обновить" severity="warning"/>
                    </div>
                    <div class="field col-12 md:col-4">
                        <Button class="mr-2 sm:mb-2" icon="pi pi-trash" label="Удалить" severity="danger"/>
                    </div>

                    <Dialog v-model:visible="deleteSeasonDialog" :modal="true" :style="{ width: '700px' }"
                            header="Подтверждение">
                        <div class="flex align-items-center justify-content-center">
                            <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem"/>
                            <span v-if="season"
                            >Вы точно хотите удалить сезон <b>{{
                                    season.title
                                }}</b> и <u>все связанные данные</u>?</span
                            >
                        </div>
                        <template #footer>
                            <Button icon="pi pi-times" label="Нет" @click="deleteSeasonDialog = false"/>
                            <Button icon="pi pi-trash" label="Да" severity="danger" text @click="deleteSeason"/>
                        </template>
                    </Dialog>
                </div>
            </div>
        </div>
    </div>
</template>
