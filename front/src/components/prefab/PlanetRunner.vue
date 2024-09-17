<template>
  <div>
    <img :src="`/assets/images/planet_${props.planet}.png`" alt="planet"
         class="absolute" style="height: 11.5em; width: 11.5em; left: -5.75em; top: -5.75em;" />

    <div aria-hidden="true" class="textContainer space-font relative">
      <span style="--i: 0;">{{ props.text }} {{ props.text }}</span>
      <span style="--i: 1;">{{ props.text }} {{ props.text }}</span>
      <span style="--i: 2;">{{ props.text }} {{ props.text }}</span>
      <span style="--i: 3;">{{ props.text }} {{ props.text }}</span>
      <span style="--i: 4;">{{ props.text }} {{ props.text }}</span>
      <span style="--i: 5;">{{ props.text }} {{ props.text }}</span>
      <span style="--i: 6;">{{ props.text }} {{ props.text }}</span>
      <span style="--i: 7;">{{ props.text }} {{ props.text }}</span>
      <span style="--i: 8;">{{ props.text }} {{ props.text }}</span>
      <span style="--i: 9;">{{ props.text }} {{ props.text }}</span>
      <span style="--i: 10;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 11;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 12;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 13;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 14;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 15;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 16;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 17;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 18;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 19;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 20;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 21;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 22;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 23;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 24;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 25;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 26;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 27;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 28;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 29;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 30;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 31;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 32;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 33;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 34;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 35;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
      <span style="--i: 36;">{{ props.text }} {{ props.text }} {{ props.text }} {{ props.text }}</span>
    </div>
  </div>
</template>

<style lang="scss">
.textContainer {
  perspective: 6em;
  animation: rotate 50s infinite linear;

  span {
    padding-left: 0.25em;
    position: absolute;
    width: 1em;
    white-space: nowrap;
    text-indent: calc(var(--i) * -1em);
    overflow: hidden;
    color: transparent !important;
    -webkit-text-stroke: 1.5px rgba(230, 219, 219, 0.5);
    transform: translate(-50%, -50%) rotate(calc(10deg * var(--i))) translateY(calc(-1em / sin(10deg) + 0.05em)) scaleY(200%) rotateX(-90deg);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg) rotateX(0deg) rotate(720deg);
  }
  to {
    transform: rotate(360deg) rotateX(0deg) rotate(0deg);
  }
}
</style>

<script setup>
const props = defineProps({
  planet: {
    type: String,
    default: 'mars'
  },
  text: {
    type: String,
    required: true
  }
});
</script>