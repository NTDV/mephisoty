<script setup>

const score = defineModel({
  required: true,
  type: Object
});

</script>

<template>
  <div class="pt-2 pd-2">
    <InputText v-model.number="score.score" autofocus class="mb-2 w-full" max="100" min="0"
               placeholder="Оценка" type="number"/>
    <Textarea v-model.trim="score.comment" :disabled="!score.score && score.score !== 0"
              auto-resize cols="10" placeholder="Заметка"
              rows="1" style="min-width: 4em; max-width: 9em;"/>
  </div>
</template>