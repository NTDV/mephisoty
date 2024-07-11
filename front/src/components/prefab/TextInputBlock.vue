<script setup>
const text = defineModel();

const props = defineProps({
  label: String,
  invalid: Boolean,
  notRequired: Boolean,
  maxLength: {
    type: Number,
    default: 120
  },
  autofocus: Boolean
})
</script>

<template>
  <label v-if="props.label">{{ props.label }}</label>
  <InputText v-model.trim="text"
             :autofocus="props.autofocus"
             :invalid="props.invalid"
             :maxlength="props.maxLength"
             :required="!props.notRequired"/>
  <small v-if="props.invalid"
         class="p-invalid">Неверное заполнение.</small>
</template>