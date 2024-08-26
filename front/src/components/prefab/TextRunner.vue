<template>
  <div class="ticker">
    <div class="ticker__in">
      <span class="ticker__item space-font uppercase text-white">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-primary-otlined">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-white">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-primary-otlined">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-white">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-primary-otlined">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-white">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-primary-otlined">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-white">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-primary-otlined">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-white">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-primary-otlined">{{ props.text }}</span>
    </div>
  </div>
</template>

<style scoped>
.text-primary-otlined {
  color: rgba(255, 255, 255, 0);
  -webkit-text-stroke: 2px var(--primary-color);
}

.ticker {
  max-width: 100%;
}

.ticker__in {
  display: flex;
  width: fit-content;
  animation: ticker 10s linear infinite;
  -webkit-transform-style: preserve-3d;
}

.ticker__item {
  padding-left: .75rem;
  padding-right: .75rem;
  white-space: nowrap;
  -webkit-backface-visibility: hidden;
}

@keyframes ticker {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}
</style>

<script setup>
const props = defineProps({
  text: {
    type: String,
    required: true
  }
});
</script>