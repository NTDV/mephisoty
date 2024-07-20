<script setup>
const text = defineModel();

const props = defineProps({
  label: String,
  autoResize: Boolean,
  rows: {
    type: Number,
    default: 3
  },
  maxlength: {
    type: Number,
    default: 50000
  },
  invalid: Boolean,
  disabled: Boolean
})
</script>

<template>
  <label v-if="props.label">{{ props.label }}</label>
  <Textarea v-model.trim="text"
            :autoResize="props.autoResize"
            :maxlength="props.maxlength"
            :rows="props.rows"
            :disabled="props.disabled"/>
  <small v-if="props.invalid"
         class="p-invalid">Неверное заполнение.</small>
</template>