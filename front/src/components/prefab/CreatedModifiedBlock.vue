<script setup>
import {DateTimeService} from "@/service/DateTimeService";

const season = defineModel();
const dateTimeService = new DateTimeService();
</script>

<template>
    <small v-if="season.id">ID: {{ season.id }}</small>
    <br>
    <small v-if="season.createdBy && season.createdBy.name">
        Создано:
        <RouterLink :to="'/admin/creds/' + season.createdBy.id">
            <b>{{ season.createdBy.name }}</b>
        </RouterLink>
        {{ dateTimeService.formatDateTimeFromDate(season.createdAt) }}
    </small>
    <br>
    <small v-if="season.modifiedBy && season.modifiedBy.name">
        Изменено:
        <RouterLink :to="'/admin/creds/' + season.modifiedBy.id">
            <b>{{ season.modifiedBy.name }}</b>
        </RouterLink>
        {{ dateTimeService.formatDateTimeFromDate(season.modifiedAt) }}
    </small>
</template>