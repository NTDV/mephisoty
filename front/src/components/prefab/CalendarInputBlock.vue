<script setup>
const calendarValue = defineModel();

const props = defineProps({
  label: String,
  submitted: Boolean
})
</script>

<template>
  <label>{{ props.label }}</label>
  <Calendar
    v-model="calendarValue"
    :invalid="props.submitted && !calendarValue"
    :showButtonBar="true"
    :showIcon="true"
    :showSeconds="true"
    :showTime="true"
    date-format="dd.mm.yy"
    hour-format="24"
    mask="99.99.9999 99:99:99"
    selectionMode="single"
  ></Calendar>
  <small v-if="props.submitted && !calendarValue" class="p-invalid">Введите дату и время.</small>
</template>