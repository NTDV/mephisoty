<script setup>
import { ref } from 'vue';
import { AchievementTypeService } from '@/service/util/AchievementTypeService';

const visibility = defineModel();

const props = defineProps({
  label: String,
  isWithoutAll: Boolean,
  submitted: Boolean
});

const achievementTypeService = new AchievementTypeService();
const statuses = ref(props.isWithoutAll ? achievementTypeService.getBadgeContentWithoutAll() : achievementTypeService.getBadgeContentAll());
</script>

<template>
  <label>{{ props.label }}</label>
  <Dropdown v-model="visibility" :invalid="props.submitted && !visibility"
            :options="statuses" option-value="value" optionLabel="label"
            placeholder="Выберите вид">
    <template #value="slotProps">
      <Tag v-if="slotProps.value"
           :severity="achievementTypeService.getBadgeSeverityFor(slotProps.value)"
           :value="achievementTypeService.getBadgeContentFor(slotProps.value)" />
      <span v-else> {{ slotProps.placeholder }} </span>
    </template>
    <template #option="slotProps">
      <Tag :severity="achievementTypeService.getBadgeSeverityFor(slotProps.option.value)"
           :value="slotProps.option.label" />
    </template>
  </Dropdown>
  <small v-if="submitted && !visibility" class="p-invalid">Выберите один из вариантов.</small>
</template>