<script setup>
import {ref} from "vue";
import {AllowStateService} from "@/service/AllowStateService";

const visibility = defineModel();

const props = defineProps({
  label: String,
  isReadViewOnly: Boolean,
  submitted: Boolean
})

const allowStateService = new AllowStateService();
const statuses = ref(props.isReadViewOnly ? allowStateService.getBadgeContentViewOnly() : allowStateService.getBadgeConentAll());
</script>

<template>
  <label>{{ props.label }}</label>
  <Dropdown v-model="visibility" :invalid="props.submitted && !visibility"
            :options="statuses" option-value="value" optionLabel="label"
            placeholder="Выберите ограничение">
    <template #value="slotProps">
      <Tag v-if="slotProps.value"
           :severity="allowStateService.getBadgeSeverityFor(slotProps.value)"
           :value="allowStateService.getBadgeContentFor(slotProps.value)"/>
      <span v-else> {{ slotProps.placeholder }} </span>
    </template>
    <template #option="slotProps">
      <Tag :severity="allowStateService.getBadgeSeverityFor(slotProps.option.value)" :value="slotProps.option.label"/>
    </template>
  </Dropdown>
  <small v-if="submitted && !visibility" class="p-invalid">Выберите один из вариантов.</small>
</template>