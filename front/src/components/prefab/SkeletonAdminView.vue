<script setup>
const props = defineProps({
  title: String
});
</script>

<template>
  <div class="col-12">
    <div class="card">
      <h5>{{ props.title }}</h5>
      <div class="p-fluid formgrid grid">
        <div class="field col-12">
          <Skeleton class="mb-2" width="3em"/>
          <Skeleton class="mb-2" width="20em"/>
          <Skeleton class="mb-2" width="13em"/>
        </div>

        <div class="field col-12">
          <Skeleton class="mb-2" width="15em"/>
          <div class="flex">
            <Skeleton height="2.5em" width="30%"/>
            <Skeleton class="ml-2" height="2.5em" width="40px"/>
          </div>
        </div>

        <div class="field col-12">
          <Skeleton class="mb-2" width="11em"/>
          <Skeleton height="2.5em" width="100%"></Skeleton>
        </div>

        <div class="field col-12">
          <Skeleton class="mb-2" width="10em"/>
          <Skeleton height="2.5em" width="100%"></Skeleton>
        </div>

        <div class="field col-12">
          <Skeleton class="mb-2" width="11em"/>
          <Skeleton height="7.5em" width="100%"></Skeleton>
        </div>

        <div class="field col-12 md:col-6">
          <Skeleton class="mb-2" width="14em"/>
          <Skeleton height="2.5em" width="100%"></Skeleton>
        </div>

        <div class="field col-12 md:col-6">
          <Skeleton class="mb-2" width="14em"/>
          <Skeleton height="2.5em" width="100%"></Skeleton>
        </div>

        <div class="field col-12">
          <Skeleton class="mb-2" width="12em"/>
          <Skeleton height="9em" width="100%"></Skeleton>
        </div>

        <div class="field col-12 md:col-4">
          <Skeleton height="2.5em" width="100%"></Skeleton>
        </div>

        <div class="field col-12 md:col-4">
          <Skeleton height="2.5em" width="100%"></Skeleton>
        </div>

        <div class="field col-12 md:col-4">
          <Skeleton height="2.5em" width="100%"></Skeleton>
        </div>
      </div>
    </div>
  </div>
</template>