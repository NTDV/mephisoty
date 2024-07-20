<script setup>
import {onMounted, ref} from "vue";
import {SeasonService} from "@/service/SeasonService";

const chosenModel = defineModel();
const indexDefault = 0;

onMounted(() => {
  if (chosenModel.value) {
    items.value.push(chosenModel.value);
    chosenModel.value = chosenModel.value.id;
  }
})

const events = defineEmits(['change']);
const change = (...args) => {
  console.log(chosenModel.value);
  events('change', ...args);
}

const props = defineProps({
  label: String,
  invalid: Boolean
})

const seasonService = new SeasonService();

const items = ref([]);
const loading = ref(false);
const onLazyLoad = (event) => {
  // todo Make it work!!!
  //if (!event.value || event.value.length >= 4)

  const {first, last} = event;
  let shouldLoad = items.value.length <= 1;
  if (!shouldLoad) {
    for (let i = first; i < last; i++) {
      const element = items.value[i];
      if (element === null || element === undefined) {
        shouldLoad = true;
        loading.value = true;
        break;
      }
    }
  }

  if (shouldLoad) {
    seasonService.getAllForSelect(event).then((data) => {
      const _items = items.value.length === data.total + 1 ?
        [...items.value] : Array.from({length: data.total + 1});
      if (!_items[0]) _items[0] = items.value[0];

      for (let j = 0; j < data.collection.length; j++)
        _items[first + j] = data.collection[j];

      items.value = _items;
      loading.value = false;
    });
  } else {
    loading.value = false;
  }
};

//const onFilter = (args) => {
//  onLazyLoad(args);
//}
</script>

<template>
  <label v-if="props.label">{{ props.label }}</label>
  <Dropdown class="p-column-filter" style="min-width: 10em; max-width: 20em;"
            v-model="chosenModel" :options="items"
            filter
            option-label="title" option-value="id"
            placeholder="Выберите элемент" @change="change($event)"
            :virtualScrollerOptions="{lazy: true, onLazyLoad: onLazyLoad, delay: 20, itemSize: 38, showLoader: false, loading: loading, }">
    <template #option="slotProps">
      <span>{{ slotProps.option.title }} <small>id: {{ slotProps.option.id }}</small></span>
    </template>
  </Dropdown>
  <small v-if="props.invalid" class="p-invalid">Неверное значение.</small>
</template>