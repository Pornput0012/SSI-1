<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { useUserStore } from "@/stores/UserStore";
import { getImageByFileName, getUserById } from "@/services/userService";
import { useRouter } from "vue-router";

const USER_TYPE = {
  SELLER: "SELLER",
  BUYER: "BUYER",
};

const userStore = useUserStore();

const router = useRouter()

// Original data
const frontImageUrl = ref(null);
const backImageUrl = ref(null);
const userInfo = ref(null);

// Editable form data
const formData = ref({
  nickName: '',
  email: '',
  fullName: '',
  phoneNumber: '',
  bankName: '',
  bankAccount: '',
  nationalIdNumber: ''
});

// New image uploads
const newFrontImage = ref(null);
const newBackImage = ref(null);
const frontImagePreview = ref(null);
const backImagePreview = ref(null);

// Form state
const isEditing = ref(false);
const isSaving = ref(false);
const hasChanges = ref(false);

const userId = computed(() => userStore.getUserInfo().id);
const userType = computed(() => userStore.getUserInfo().userType);

// Load user data
watch(async () => {
  if (userId.value) {
    const user = await getUserById(userId.value);
    userInfo.value = user;
    
    // Initialize form data
    formData.value = {
      nickName: user.nickName || '',
      email: user.email || '',
      fullName: user.fullName || '',
      phoneNumber: user.phoneNumber || '',
      bankName: user.bankName || '',
      bankAccount: user.bankAccount || '',
      nationalIdNumber: user.nationalIdNumber || ''
    };
    
    if (user.nationalIdPhotoFront) {
      frontImageUrl.value = await getImageByFileName(user.nationalIdPhotoFront);
    }
    if (user.nationalIdPhotoBack) {
      backImageUrl.value = await getImageByFileName(user.nationalIdPhotoBack);
    }
  }
});

// Handle image upload
const handleImageUpload = (event, type) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      if (type === 'front') {
        newFrontImage.value = file;
        frontImagePreview.value = e.target.result;
      } else {
        newBackImage.value = file;
        backImagePreview.value = e.target.result;
      }
      hasChanges.value = true;
    };
    reader.readAsDataURL(file);
  }
};

// Watch for form changes
watch(formData, () => {
  if (userInfo.value) {
    hasChanges.value = Object.keys(formData.value).some(key => 
      formData.value[key] !== (userInfo.value[key] || '')
    );
  }
}, { deep: true });

// Save changes
const saveChanges = async () => {
  isSaving.value = true;
  try {
    // Here you would call your API to save the changes
    console.log('Saving changes:', formData.value);
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    // Update userInfo with new data
    Object.assign(userInfo.value, formData.value);
    hasChanges.value = false;
  } catch (error) {
    console.error('Error saving changes:', error);
  } finally {
    isSaving.value = false;
  }
};

// Cancel editing
const cancelEdit = () => {
  if (userInfo.value) {
    formData.value = {
      nickName: userInfo.value.nickName || '',
      email: userInfo.value.email || '',
      fullName: userInfo.value.fullName || '',
      phoneNumber: userInfo.value.phoneNumber || '',
      bankName: userInfo.value.bankName || '',
      bankAccount: userInfo.value.bankAccount || '',
      nationalIdNumber: userInfo.value.nationalIdNumber || ''
    };
  }
  newFrontImage.value = null;
  newBackImage.value = null;
  frontImagePreview.value = null;
  backImagePreview.value = null;
  hasChanges.value = false;

  router.push({name : "profile"});
};
</script>

<template>
  <div class="min-h-screen bg-base-200 py-8">
    <div class="container mx-auto px-4 max-w-4xl">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4">
        <div>
          <h1 class="text-3xl font-bold text-base-content">แก้ไขโปรไฟล์</h1>
          <p class="text-base-content/70 mt-1">จัดการข้อมูลส่วนตัวของคุณ</p>
        </div>

        
        <!-- Action Buttons -->
        <div class="flex gap-2">
            
            <button 
              @click="saveChanges"
              :disabled="!hasChanges || isSaving"
              :class="['btn btn-success gap-2', { 'loading': isSaving }]"
            >
              <svg v-if="!isSaving" xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              {{ isSaving ? 'saving...' : 'save' }}
            </button>
            
            <button 
              @click="cancelEdit"
              :disabled="isSaving"
              class="btn btn-ghost gap-2"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
              cancel
            </button>
          
          <!-- <router-link to="/" class="btn btn-outline gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            กลับหน้าหลัก
          </router-link> -->
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="!userInfo" class="flex justify-center items-center py-20">
        <span class="loading loading-spinner loading-lg text-primary"></span>
      </div>

      <!-- Profile Card -->
      <div v-if="userInfo" class="card bg-base-100 shadow-xl">
        <div class="card-body p-6 md:p-8">
          <!-- Profile Header with Avatar -->
          <div class="flex flex-col sm:flex-row items-center sm:items-start gap-6 mb-8">
            <div class="avatar">
              <div class="w-24 h-24 rounded-full ring ring-primary ring-offset-base-100 ring-offset-2">
                <img
                  src="https://img.daisyui.com/images/profile/demo/yellingcat@192.webp"
                  :alt="userInfo.fullName"
                />
              </div>
            </div>
            <div class="text-center sm:text-left flex-1">
              <h2 class="text-2xl font-bold text-base-content">
                {{ formData.fullName || userInfo.fullName }}
              </h2>
              <p class="text-base-content/70 text-lg">
                @{{ formData.nickName || userInfo.nickName }}
              </p>
              <div class="badge badge-primary badge-lg mt-2">
                {{ userType === USER_TYPE.SELLER ? "Seller" : "Buyer" }}
              </div>
            </div>
          </div>

          <!-- Basic Information -->
          <div class="divider divider-primary">
            <span class="text-lg font-semibold">ข้อมูลพื้นฐาน</span>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Nickname -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">ชื่อผู้ใช้ (nickname) <span class="text-error">*</span></span>
              </label>
              <input 
            
                v-model="formData.nickName"
                type="text" 
                placeholder="กรอกชื่อผู้ใช้"
                class="input input-bordered w-full"
                required
              />
             
            </div>

            <!-- Email -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">อีเมล (email) <span class="text-error">*</span></span>
              </label>
              <input 

                v-model="formData.email"
                type="email" 
                placeholder="กรอกอีเมล"
                class="input input-bordered w-full"
                required
              />
        
            </div>

            <!-- Full Name -->
            <div class="form-control md:col-span-2">
              <label class="label">
                <span class="label-text font-semibold">ชื่อ-นามสกุล (fullname) <span class="text-error">*</span></span>
              </label>
              <input 
             
                v-model="formData.fullName"
                type="text" 
                placeholder="กรอกชื่อ-นามสกุล"
                class="input input-bordered w-full"
                required
              />
             
            </div>
          </div>

          <!-- Seller Additional Information -->
          <div v-if="userType === USER_TYPE.SELLER" class="mt-8">
            <div class="divider divider-secondary">
              <span class="text-lg font-semibold">ข้อมูลผู้ขาย</span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- Phone -->
              <div class="form-control">
                <label class="label">
                  <span class="label-text font-semibold">หมายเลขโทรศัพท์มือถือ (phone) <span class="text-error">*</span></span>
                </label>
                <input 
              
                  v-model="formData.phoneNumber"
                  type="tel" 
                  placeholder="กรอกหมายเลขโทรศัพท์"
                  class="input input-bordered w-full"
                  pattern="[0-9]{10}"
                  required
                />
               
              </div>

              <!-- Bank Name -->
              <div class="form-control">
                <label class="label">
                  <span class="label-text font-semibold">ชื่อธนาคาร (bankName) <span class="text-error">*</span></span>
                </label>
                <select 
              
                  v-model="formData.bankName"
                  class="select select-bordered w-full"
                  required
                >
                  <option disabled value="">เลือกธนาคาร</option>
                  <option value="ธนาคารไทยพาณิชย์">ธนาคารไทยพาณิชย์ (SCB)</option>
                  <option value="ธนาคารกสิกรไทย">ธนาคารกสิกรไทย (KBANK)</option>
                  <option value="ธนาคารกรุงเทพ">ธนาคารกรุงเทพ (BBL)</option>
                  <option value="ธนาคารกรุงไทย">ธนาคารกรุงไทย (KTB)</option>
                  <option value="ธนาคารทหารไทยธนชาต">ธนาคารทหารไทยธนชาต (TTB)</option>
                </select>
               
              </div>

              <!-- Bank Account -->
              <div class="form-control">
                <label class="label">
                  <span class="label-text font-semibold">หมายเลขบัญชีธนาคาร (bankAccount) <span class="text-error">*</span></span>
                </label>
                <input 
                 
                  v-model="formData.bankAccount"
                  type="text" 
                  placeholder="กรอกหมายเลขบัญชี"
                  class="input input-bordered w-full font-mono"
                  pattern="[0-9]{10,12}"
                  required
                />
               
              </div>

              <!-- ID Card Number -->
              <div class="form-control">
                <label class="label">
                  <span class="label-text font-semibold">หมายเลขบัตรประชาชน (nationalIdNumber) <span class="text-error">*</span></span>
                </label>
                <input 
                 
                  v-model="formData.nationalIdNumber"
                  type="text" 
                  placeholder="กรอกหมายเลขบัตรประชาชน"
                  class="input input-bordered w-full font-mono"
                  pattern="[0-9]{13}"
                  maxlength="13"
                  required
                />
               
              </div>
            </div>

            <!-- ID Card Images -->
            <div class="mt-6">
              <label class="label">
                <span class="label-text font-semibold">รูปถ่ายบัตรประชาชน (idCardFront, idCardBack)</span>
              </label>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <!-- Front ID Card -->
                <div class="card bg-base-200 shadow-sm">
                  <div class="card-body p-4">
                    <h4 class="font-semibold text-sm mb-3">ด้านหน้า</h4>
                    <div class="aspect-[1.6/1] bg-base-300 rounded-lg overflow-hidden relative group">
                      <img
                        v-if="frontImagePreview || frontImageUrl"
                        :src="frontImagePreview || frontImageUrl"
                        alt="บัตรประชาชนด้านหน้า"
                        class="w-full h-full object-cover"
                      />
                      <div
                        v-else
                        class="w-full h-full flex items-center justify-center text-base-content/60"
                      >
                        <span class="text-sm">ไม่มีรูปภาพ</span>
                      </div>
                      
                      <!-- Upload overlay for editing mode -->
                      <div class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                        <label class="btn btn-primary btn-sm gap-2 cursor-pointer">
                          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                          </svg>
                          อัปโหลด
                          <input 
                            type="file" 
                            class="hidden" 
                            accept="image/*"
                            @change="handleImageUpload($event, 'front')"
                          />
                        </label>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Back ID Card -->
                <div class="card bg-base-200 shadow-sm">
                  <div class="card-body p-4">
                    <h4 class="font-semibold text-sm mb-3">ด้านหลัง</h4>
                    <div class="aspect-[1.6/1] bg-base-300 rounded-lg overflow-hidden relative group">
                      <img
                        v-if="backImagePreview || backImageUrl"
                        :src="backImagePreview || backImageUrl"
                        alt="บัตรประชาชนด้านหลัง"
                        class="w-full h-full object-cover"
                      />
                      <div
                        v-else
                        class="w-full h-full flex items-center justify-center text-base-content/60"
                      >
                        <span class="text-sm">ไม่มีรูปภาพ</span>
                      </div>
                      
                      <!-- Upload overlay for editing mode -->
                      <div v-if="isEditing" class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                        <label class="btn btn-primary btn-sm gap-2 cursor-pointer">
                          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                          </svg>
                          อัปโหลด
                          <input 
                            type="file" 
                            class="hidden" 
                            accept="image/*"
                            @change="handleImageUpload($event, 'back')"
                          />
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Form Actions (Bottom) -->
          <div  class="mt-8 pt-6 border-t border-base-300">
            <div class="flex flex-col sm:flex-row gap-3 justify-end">
              <button 
                @click="cancelEdit"
                :disabled="isSaving"
                class="btn btn-ghost sm:btn-wide"
              >
                ยกเลิก
              </button>
              <button 
                @click="saveChanges"
                :disabled="!hasChanges || isSaving"
                :class="['btn btn-primary sm:btn-wide', { 'loading': isSaving }]"
              >
                {{ isSaving ? 'กำลังบันทึก...' : 'บันทึกการเปลี่ยนแปลง' }}
              </button>
            </div>
          </div>

          <!-- Change Indicator -->
          <div v-if="hasChanges" class="alert alert-info mt-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0 stroke-current" fill="none" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <span>คุณมีการเปลี่ยนแปลงที่ยังไม่ได้บันทึก</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Additional custom styles */
.divider-primary::before,
.divider-primary::after {
  background-color: hsl(var(--p));
}

.divider-secondary::before,
.divider-secondary::after {
  background-color: hsl(var(--s));
}

/* Smooth transitions for form controls */
.form-control input,
.form-control select {
  transition: all 0.2s ease;
}

.form-control input:focus,
.form-control select:focus {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Image upload hover effects */
.group:hover .group-hover\:opacity-100 {
  opacity: 1;
}

/* Required field indicator */
.text-error {
  color: hsl(var(--er));
}
</style>