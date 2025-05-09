<script setup>
defineProps({
  crumbs: {
    type: Array,
    default: () => [],
  },
});
</script>

<template>
  <div class="breadcrumbs text-xs md:text-sm px-6 md:px-20 my-2 md:my-10">
    <ul class="flex gap-2">
      <li>
        <router-link to="/" class="itbms-home-button">Home</router-link>
      </li>
      <li v-for="(crumb, index) in crumbs" :key="index">
        <span v-if="index !== crumbs.length - 1">
          <router-link class="itbms-back-button" :to="crumb.to">{{
            crumb.label
          }}</router-link>
        </span>
        <span v-else class="itbms-back-button">{{ crumb.label }}</span>
      </li>
    </ul>
  </div>
</template>
