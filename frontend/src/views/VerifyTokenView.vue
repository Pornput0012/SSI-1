<script setup>
import MessageModal from "@/components/MessageModal.vue";
import { verifyEmail } from "@/services/EmailService";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router =useRouter()

const errorTitle = ref('')
const errorMessage = ref('')
const showModal = ref(false)
onMounted(async () => {
  console.log(route.query.jwtToken);
  try {
    const response = await verifyEmail(route.query.jwtToken);

    if (response.error) {
      throw new Error(response.error);
    }
  } catch (error) {
    errorTitle.value = 'Failed to verify token'
    errorMessage.value =  error.message
    showModal.value = true
  }
});


function returnHome() {
    showModal.value = false
    router.push({name : 'home'});
}
</script>

<template>
  <div>
    <MessageModal
    v-if="showModal"
    :handle-ok="returnHome"
    :title="errorTitle"
    :message="errorMessage"
  />
  </div>
</template>

<style></style>
