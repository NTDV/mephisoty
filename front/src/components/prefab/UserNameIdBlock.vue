<script setup>
const props = defineProps({
  user: Object,
  idLeft: Boolean
});
</script>

<template>
  <small v-if="props.idLeft" class="greyid mr-2"
         style="display: inline-block;">id:&nbsp;{{ props.user.id }}</small>{{ props.user.secondName }} {{ props.user.firstName }}
  {{ props.user.thirdName }}<small v-if="!props.idLeft" class="greyid ml-2" style="display: inline-block;">id:
  {{ props.user.id }}</small>
</template>