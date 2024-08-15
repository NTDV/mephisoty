<script setup>
import { onMounted, ref } from 'vue';

const chosenModelId = defineModel();
const chosenModel = ref(null);

onMounted(() => {
  if (chosenModelId.value) {
    chosenModel.value = chosenModelId.value;
    items.value.push(chosenModel.value);
    chosenModelId.value = chosenModel.value.id;
  }
});

const events = defineEmits(['change']);
const change = (...args) => {
  console.log(args);
  events('change', ...args);
};

const props = defineProps({
  label: String,
  invalid: Boolean,
  crudService: Object,
  infix: String,
  lazyFilter: {
    type: Boolean,
    default: false
  }
});

const items = ref([]);
const loading = ref(false);
let offset = chosenModelId.value ? 1 : 0;
let filterValue = null;

const onFilter = (event) => {
  if (loading.value) return;
  loading.value = true;
  filterValue = event.value;
  if (chosenModelId.value) {
    offset = 1;
    if (chosenModel.value?.id !== chosenModelId.value) {
      chosenModel.value = items.value.find(model => model.id === chosenModelId.value);
    }
    items.value = [];
    items.value.push(chosenModel.value);
  } else {
    items.value = [];
  }
  onLazyLoad({ first: 0, last: 1 }, true);
};

const onLazyLoad = (event, force) => {
  if (!force && loading.value) return;
  if (props.lazyFilter) event = { ...event, value: filterValue };

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
        if (data.collection[j].id === chosenModelId.value) {
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
  <div class="p-dropdown" style="border: none; box-shadow: none; cursor: default;">
    <Dropdown v-model="chosenModelId" :filter-match-mode="props.lazyFilter ? 'skip' : 'contains'"
              :options="items"
              :virtualScrollerOptions="{lazy: true, onLazyLoad: onLazyLoad,
              delay: 20, itemSize: 38, showLoader: false, loading: loading,
              orientation: 'vertical', step: 12 }"
              class="p-column-filter mr-2 min-w-full max-w-fit" filter
              option-label="title" option-value="id"
              placeholder="Выберите элемент"
              @change="change($event)" @filter="onFilter($event)">
      <template #option="slotProps">
        <span v-if="slotProps.option">{{ slotProps.option.title }}
          <small class="greyid">id:&nbsp;{{ slotProps.option.id }}</small>
        </span>
      </template>
    </Dropdown>
    <RouterLink v-if="chosenModelId && props.infix" :to="'/admin/' + props.infix + '/' + chosenModelId">
      <Button icon="pi pi-eye" rounded severity="success"/>
    </RouterLink>
  </div>
  <small v-if="props.invalid" class="p-invalid">Неверное значение.</small>
</template>