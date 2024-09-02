<template>
  <div ref="ticker" class="ticker">
    <div ref="ticker__in" class="ticker__in">
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
      <span class="ticker__item space-font uppercase text-white">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-primary-otlined">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-white">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-primary-otlined">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-white">{{ props.text }}</span>
      <span class="ticker__item space-font uppercase text-primary-otlined">{{ props.text }}</span>
    </div>
  </div>
</template>

<style>
.text-primary-otlined {
  color: transparent !important;
  -webkit-text-stroke: 2px var(--primary-color);
}

.ticker {
  max-width: 100%;
}

.ticker__in {
  display: flex;
  width: fit-content;
  will-change: transform, animation;
  animation: v-bind('animation');
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
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { debounce, isSmallOrMobileOrTablet } from '@/service/util/UtilsService';

const ticker = ref(null);
const ticker__in = ref(null);
const animation = ref('none');

onMounted(() => {
  setDuration();
  window.addEventListener('resize', handleResize, false);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize, false);
});

const handleResize = debounce(() => setDuration(), 100);

const setDuration = () => {
  if (!props.alwaysRun && isSmallOrMobileOrTablet()) {
    animation.value = 'none';
  } else {
    const speed = 200;
    const computedStyle = getComputedStyle(ticker.value);
    const distancePx =
      ticker.value.offsetWidth
      - parseInt(computedStyle.paddingLeft)
      - parseInt(computedStyle.paddingRight)
      - ticker__in.value.offsetWidth;

    animation.value = `ticker ${Math.abs(distancePx / speed)}s linear infinite`;
  }
};


const props = defineProps({
  alwaysRun: {
    type: Boolean,
    default: false
  },
  text: {
    type: String,
    required: true
  }
});
</script>