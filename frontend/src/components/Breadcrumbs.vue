<script setup>
const props = defineProps({
  crumbs: {
    type: Array,
    default: () => [],
  },
  home: {
    type: Object,
    default: null,
  },
});
</script>

<template>
  <div class="breadcrumbs text-xs md:text-sm px-6 md:px-20 my-2 md:my-10">
    <ul class="flex gap-2">
      <li class="">
        <router-link
          v-if="home"
          :to="{ name: home.name }"
          :class="home.class"
          >{{ home.label }}</router-link
        >
        <router-link
          v-else
          :to="{ name: 'sale-items' }"
          class="itbms-home-button"
          >Home</router-link
        >
      </li>
      <li v-for="(crumb, index) in crumbs" :key="index">
        <span v-if="crumb.name && crumb.params">
          <router-link
            :to="{ name: crumb.name, params: { id: crumb.params } }"
            :class="crumb.class"
          >
            {{ crumb.label }}
          </router-link>
        </span>
        <span v-else-if="crumb.name">
          <router-link :to="{ name: crumb.name }" :class="crumb.class">
            {{ crumb.label }}
          </router-link>
        </span>
        <span v-else :class="crumb.class">{{ crumb.label }}</span>
      </li>
    </ul>
  </div>
</template>
