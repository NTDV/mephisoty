<script setup>
const number = defineModel();

const props = defineProps({
  label: String,
  min: {
    type: Number,
    default: 0
  },
  max: {
    type: Number,
    default: 100
  },
  integer: Boolean,
  invalid: Boolean
})
</script>

<template>
  <label v-if="props.label">{{ props.label }}</label>
  <InputNumber v-model.number="number" :invalid="props.invalid"
               :max="props.max" :maxFractionDigits="integer ? 0 : 2"
               :min="props.min" show-buttons/>
  <small v-if="props.invalid"
         class="p-invalid">Неверное заполнение.</small>
</template>