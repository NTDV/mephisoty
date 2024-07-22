<script setup>
import {onMounted, ref} from "vue";

const chosenModel = defineModel();

onMounted(() => {
  if (chosenModel.value) {
    items.value.push(chosenModel.value);
    chosenModel.value = chosenModel.value.id;
  }
});

const events = defineEmits(['change']);
const change = (...args) => {
  events('change', ...args);
};

const props = defineProps({
  label: String,
  invalid: Boolean,
  crudService: Object,
  infix: String
});

const items = ref([]);
const loading = ref(false);
let offset = chosenModel.value ? 1 : 0;

const onLazyLoad = (event) => {
  //if (!event.value || event.value.length >= 4)

  let shouldLoad = items.value.length <= 1;
  if (event.first === 0 && event.last <= 1) event.last = 12;

  if (!shouldLoad) {
    for (let i = event.first; i < event.last; i++) {
      const element = items.value[i];
      if (!shouldLoad && (element === null || element === undefined)) {
        event.first = i === 0 ? 0 : i - 1;
        shouldLoad = true;
        loading.value = true;
      }
      if (shouldLoad && element !== null && element !== undefined) {
        event.last = i;
        break;
      }
    }
  }

  if (event.last - event.first < 12)
    event.last = event.first + 12 >= items.value.length ? items.value.length : event.first + 12;

  if (shouldLoad) {
    props.crudService.getAllForSelect(event).then((data) => {
      const _items = items.value.length === data.total ?
        [...items.value] : Array.from({0: items.value[0], length: data.total});

      for (let j = 0; j < data.collection.length; j++) {
        if (data.collection[j].id === chosenModel.value) {
          offset = 0;
        } else {
          _items[event.first + j + offset] = data.collection[j];
        }
      }

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
  <div class="flex">
  <Dropdown class="p-column-filter" style="min-width: 10em; max-width: 20em;"
            v-model="chosenModel" :options="items"
            filter
            option-label="title" option-value="id"
            placeholder="Выберите элемент" @change="change($event)"
            :virtualScrollerOptions="{lazy: true, onLazyLoad: onLazyLoad, delay: 20, itemSize: 38, showLoader: false, loading: loading, }">
    <template #option="slotProps">
      <span v-if="slotProps.option">{{ slotProps.option.title }} <small>id: {{ slotProps.option.id }}</small></span>
    </template>
  </Dropdown>
    <RouterLink v-if="chosenModel && props.infix" :to="'/admin/' + props.infix + '/' + chosenModel" class="ml-2">
      <Button class="mr-2" icon="pi pi-eye" rounded severity="success"/>
    </RouterLink>
  </div>
  <small v-if="props.invalid" class="p-invalid">Неверное значение.</small>
</template>