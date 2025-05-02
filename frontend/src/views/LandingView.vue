<script setup>
import { ref, onMounted } from "vue";

// สร้างตัวแปรเพื่อควบคุม hero slider
const currentSlide = ref(0);
const totalSlides = 3;

// รูปภาพสำหรับ hero slider
const heroImages = [
  "https://picsum.photos/1280/720?random=1",
  "https://picsum.photos/1280/720?random=2",
  "https://picsum.photos/1280/720?random=3",
];

// ฟังก์ชันสำหรับเลื่อน slide
const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % totalSlides;
};

const prevSlide = () => {
  currentSlide.value = (currentSlide.value - 1 + totalSlides) % totalSlides;
};

onMounted(() => {
  setInterval(nextSlide, 5000);
});
</script>

<template>
  <div class="overflow-hidden">
    <div class="relative h-[calc(100vh-154px)] w-full">
      <div class="carousel w-full h-full">
        <div class="carousel-item relative w-full">
          <div
            class="w-full h-full bg-no-repeat bg-cover bg-center"
            style="background-image: url('https://picsum.photos/id/1/1280/720')"
          >
            <div class="absolute inset-0 bg-black/40">
              <div
                class="flex flex-col items-center justify-center h-full text-center px-4"
              >
                <div class="mb-4">
                  <div
                    class="mask mask-squircle bg-white w-20 h-20 flex items-center justify-center mb-4 mx-auto"
                  >
                    <span class="text-xl font-bold">ITB</span>
                  </div>
                </div>

                <h1
                  class="text-4xl md:text-6xl font-bold text-white mb-2 tracking-wider"
                >
                  ITB MSHOP
                </h1>
                <hr class="w-14 bg-black h-[2px] my-2" />
                <p class="text-xl md:text-2xl text-white mb-8 max-w-md">
                  แหล่งรวมสินค้าคุณภาพเพื่อคุณ
                </p>

                <router-link
                  :to="{ name: 'sale-items' }"
                  class="btn btn-lg px-10 text-lg"
                >
                  SHOP NOW
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-6 w-6 ml-2"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M14 5l7 7m0 0l-7 7m7-7H3"
                    />
                  </svg>
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* เพิ่ม animation และ transition ต่างๆ */
.carousel-item {
  transition: all 0.5s ease;
}

.btn {
  transition: all 0.3s ease;
}

.btn:hover {
  transform: translateY(-3px);
}

.card {
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
}
</style>
