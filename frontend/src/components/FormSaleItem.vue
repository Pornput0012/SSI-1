<script setup>
import { getAllBrands } from "@/services/BrandService";
import { useBrandStore } from "@/stores/BrandStore";
import { computed, onMounted, ref, watch, watchEffect } from "vue";
import MessageModal from "./MessageModal.vue";
import noPhoto from "@/assets/No_image_available.png";
import { getImageByFileName } from "@/services/SaleItemService";

const emit = defineEmits(["onSubmit"]);
const { getBrands } = useBrandStore();
const brands = computed(() => getBrands());
const isSave = ref(true);
const hasImageChanges = ref(false);

const props = defineProps({
  saleItemDetail: {
    type: Object,
  },
  disableSaveBtn: Boolean,
  mode: {
    type: String,
    default: 'create', // 'create' หรือ 'edit'
  },
});

const formData = ref({
  brand: props.saleItemDetail?.brandName || "",
  model: props.saleItemDetail?.model || "",
  price: props.saleItemDetail?.price || null,
  description: props.saleItemDetail?.description || "",
  ramGb: props.saleItemDetail?.ramGb || null,
  screenSizeInch: props.saleItemDetail?.screenSizeInch || null,
  storageGb: props.saleItemDetail?.storageGb || null,
  color: props.saleItemDetail?.color || "",
  quantity: props.saleItemDetail?.quantity || -1,
});

const validate = ref({
  brand: { msg: "Brand must be selected.", showError: false, notNull: true },
  model: {
    msg: "Model must be 1-60 characters long.",
    notNull: true,
    maxLength: 60,
    invalid: false,
    showError: false,
  },
  color: {
    msg: "Color must be 1-40 characters long or not specified.",
    invalid: false,
    maxLength: 40,
    showError: false,
  },
  price: {
    msg: "Price must be non-negative integer.",
    invalid: false,
    notNull: true,
    nonNeg: true,
    showError: false,
  },
  description: {
    msg: "Description must be 1-16,384 characters long.",
    invalid: false,
    notNull: true,
    maxLength: 16384,
    showError: false,
  },
  quantity: {
    msg: "Quantity must be non-negative integer.",
    invalid: false,
    notNull: true,
    nonNeg: true,
    showError: false,
  },
  ramGb: {
    msg: "RAM size must be positive integer or not specified.",
    invalid: false,
    positive: true,
    showError: false,
  },
  screenSizeInch: {
    msg: "Screen size must be positive number with at most 2 decimal points or not specified.",
    invalid: false,
    positive: true,
    showError: false,
  },
  storageGb: {
    msg: "Storage size must be positive integer or not specified.",
    invalid: false,
    positive: true,
    showError: false,
  },
});

const errorTitle = ref("");
const errorMessage = ref("");
const showModal = ref(false);

// สำหรับเก็บข้อมูลรูปภาพเดิม
const originalImages = ref([]);
const images = ref([]);
const deletedImageFileNames = ref([]); // เก็บ fileName ของรูปที่ถูกลบ

const showImage = ref(noPhoto);
const showImageIndex = ref(-1);

onMounted(async () => {
  try {
    const response = await getAllBrands();
    if (response.error) {
      throw new Error(response.error);
    }

    if (props.saleItemDetail?.saleItemImages?.length > 0) {
      props.saleItemDetail.saleItemImages?.sort(
        (a, b) => a.imageViewOrder - b.imageViewOrder
      );

      for (const ele of props.saleItemDetail?.saleItemImages) {
        try {
          const response = await getImageByFileName(ele.fileName);
          if (response.error) {
            console.error(
              `Failed to load image ${ele.fileName}:`,
              response.error
            );
            continue;
          }

          const imageData = {
            id: ele.id, // เพิ่ม ID สำหรับ tracking
            imageName: ele.fileName,
            src: response,
            isOriginal: true, // มาร์คว่าเป็นรูปเดิม
            imageViewOrder: ele.imageViewOrder,
          };

          images.value.push(imageData);
          originalImages.value.push({ ...imageData }); // เก็บสำเนาข้อมูลเดิม

          if (images.value?.length == 1) {
            showImage.value = response;
            showImageIndex.value = 0;
          }
        } catch (error) {
          console.error(`Failed to load image ${ele.fileName}:`, error.message);
        }
      }
      // รีเซ็ต hasImageChanges หลังจากโหลดรูปเดิมเสร็จ
      hasImageChanges.value = false;
    }
  } catch (e) {
    errorTitle.value = "Something Wrong!";
    errorMessage.value = e.message;
    showModal.value = true;
  }
});

const requireField = ["brand", "model", "price", "description"];
const fieldCheckOldData = [
  "model",
  "price",
  "description",
  "ramGb",
  "screenSizeInch",
  "storageGb",
  "quantity",
];

const validateField = [...fieldCheckOldData, "brand", "color"];

// ปรับปรุงฟังก์ชัน handleSubmit เพื่อส่งข้อมูลรูปที่ถูกแก้ไข
const handleSubmit = () => {
  for (const field of requireField) {
    if (
      (!formData.value[field] && formData.value[field] !== 0) ||
      formData.value[field] === "" ||
      formData.value[field].id === 0
    ) {
      errorTitle.value = "Missing Required Field";
      errorMessage.value = `Please fill out the required field: ${field}`;
      showModal.value = true;
      return;
    }
  }

  // กรองเฉพาะรูปที่ยังไม่ถูกลบและจัดเรียงลำดับใหม่ให้ต่อเนื่องกัน
  const activeImages = images.value.filter(img => !img.isDeleted);
  
  const reorderedImages = [];
  let currentOrder = 1;

  // เรียงลำดับรูปที่ยังเหลืออยู่ให้ต่อเนื่องกัน
  activeImages.forEach((img) => {
    if (img.isOriginal) {
      // รูปเดิมที่ยังไม่ถูกลบ - ให้ลำดับใหม่
      reorderedImages.push({
        id: img.id,
        fileName: img.imageName,
        newOrder: currentOrder,
        originalOrder: img.imageViewOrder
      });
    } else {
      // รูปใหม่ - ให้ลำดับตามตำแหน่งปัจจุบัน
      reorderedImages.push({
        fileName: img.imageName,
        file: img.file,
        newOrder: currentOrder,
        isNew: true
      });
    }
    currentOrder++;
  });

  const imageData = {
    // รูปใหม่ที่อัปโหลด พร้อมลำดับที่ถูกต้อง
    newImages: reorderedImages.filter((img) => img.isNew), 
    // fileName ของรูปที่ถูกลบ
    deletedImageFileNames: deletedImageFileNames.value, 
    // รูปเดิมที่ยังอยู่ พร้อมลำดับใหม่
    reorderedImages: reorderedImages.filter((img) => !img.isNew),
  };
  
  emit("onSubmit", formData.value, imageData);
};

const allValid = computed(() => {
  return validateField.every(
    (field) =>
      !validate.value[field]?.showError && !validate.value[field]?.invalid
  );
});

function maximumLength(field, blur) {
  if (
    (formData.value[field] &&
      formData.value[field].length > validate.value[field].maxLength) ||
    (validate.value[field].notNull && formData.value[field].length < 1)
  ) {
    validate.value[field].invalid = true;

    if (blur) {
      validate.value[field].showError = true;
    }
  } else {
    validate.value[field].invalid = false;
    validate.value[field].showError = false;
  }
}

function brandSelected(blur) {
  if (
    !formData.value.brand ||
    formData.value.brand === "" ||
    formData.value.brand.id === 0
  ) {
    validate.value.brand.invalid = true;
    if (blur) {
      validate.value.brand.showError = true;
    }
    return;
  } else {
    validate.value.brand.invalid = false;
    validate.value.brand.showError = false;
  }
}

function nonNegative(field, blur) {
  if (
    (!formData.value[field] && formData.value[field] !== 0) ||
    (formData.value[field] && formData.value[field] < 0)
  ) {
    validate.value[field].invalid = true;

    if (blur) {
      validate.value[field].showError = true;
    }

    return;
  } else {
    validate.value[field].invalid = false;
    validate.value[field].showError = false;
  }
}

function isValidPositiveField(field, blur) {
  if (!formData.value[field] && formData.value[field] !== 0) {
    validate.value[field].showError = false;
    validate.value[field].invalid = false;
    return;
  }

  const parsed = parseFloat(formData.value[field]);

  if (isNaN(parsed) || parsed <= 0) {
    validate.value[field].invalid = true;
    if (blur) {
      validate.value[field].showError = true;
    }
    return;
  }

  if (field === "screenSizeInch") {
    const decimalPart = formData.value[field].toString().split(".")[1];
    const hasTooManyDecimals = decimalPart && decimalPart.length > 2;

    if (hasTooManyDecimals) {
      validate.value[field].invalid = true;
      if (blur) {
        validate.value[field].showError = true;
      }
    } else {
      validate.value[field].showError = false;
      validate.value[field].invalid = false;
    }

    return;
  }

  validate.value[field].showError = false;
  validate.value[field].invalid = false;
}

watchEffect(() => {
  if (brands.value && brands.value.length > 0 && formData.value.brand) {
    const foundBrand = brands.value.find(
      (brand) => brand.name === formData.value.brand
    );
    if (foundBrand) {
      formData.value.brand = foundBrand;
    }
  }
});

const normalize = (val) =>
  val === null || val === undefined ? "" : val.trim?.() ?? val;

["quantity", "price"].forEach((field) => {
  watch(
    () => formData.value[field],
    () => nonNegative(field, false)
  );
});

["model", "description"].forEach((field) => {
  watch(
    () => formData.value[field],
    () => maximumLength(field, false)
  );
});

["ramGb", "screenSizeInch", "storageGb"].forEach((field) => {
  watch(
    () => formData.value[field],
    () => isValidPositiveField(field, false)
  );
});

watch(
  () => formData.value.brand,
  () => brandSelected(false)
);

watch(
  () => formData.value.color,
  () => {
    formData.value.color = normalize(formData.value.color);
    maximumLength("color", false);
  }
);

// ฟังก์ชันตรวจสอบการเปลี่ยนแปลงของรูปภาพ
const checkImageChanges = () => {
  // กรองเฉพาะรูปที่ยังไม่ถูกลบ
  const activeImages = images.value.filter(img => !img.isDeleted);
  
  // ตรวจสอบว่ามีรูปใหม่หรือไม่
  const hasNewImages = activeImages.some((img) => !img.isOriginal);

  // ตรวจสอบว่ามีรูปถูกลบหรือไม่
  const hasDeletedImages = deletedImageFileNames.value.length > 0;

  // ตรวจสอบการเปลี่ยนลำดับรูปเดิม
  const originalImagesInCurrentOrder = activeImages.filter(
    (img) => img.isOriginal
  );

  // เช็คการเปลี่ยนลำดับ - ต้องเปรียบเทียบกับลำดับเดิม
  const hasReorderedImages = originalImagesInCurrentOrder.some(
    (img, currentIndex) => {
      const originalImg = originalImages.value.find(
        (orig) => orig.id === img.id
      );
      if (!originalImg) return false;

      // หาตำแหน่งเดิมของรูปนี้ในรายการเดิม (เรียงตาม imageViewOrder)
      const originalIndex = originalImages.value
        .sort((a, b) => a.imageViewOrder - b.imageViewOrder)
        .findIndex((orig) => orig.id === img.id);

      return originalIndex !== currentIndex;
    }
  );

  // ตรวจสอบจำนวนรูปเปลี่ยนหรือไม่ (เฉพาะรูปที่ยังใช้งานอยู่)
  const totalOriginalImages = originalImages.value.length;
  const totalCurrentActiveImages = activeImages.length;
  const hasImageCountChanged = totalOriginalImages !== totalCurrentActiveImages;

  return (
    hasNewImages ||
    hasDeletedImages ||
    hasReorderedImages ||
    hasImageCountChanged
  );
};

watchEffect(() => {
  // ตรวจสอบ validation ก่อน
  if (!allValid.value) {
    isSave.value = true;
    return;
  }

  // ตรวจสอบ required fields
  for (const field of requireField) {
    if (
      (!formData.value[field] && formData.value[field] !== 0) ||
      formData.value[field] === "" ||
      (typeof formData.value[field] === "object" &&
        formData.value[field]?.id === 0)
    ) {
      isSave.value = true;
      return;
    }
  }

  let hasFormChanges = false;

  // ตรวจสอบการเปลี่ยนแปลงของ brand
  const currentBrandName = formData.value.brand?.name || formData.value.brand;
  const originalBrandName = props.saleItemDetail?.brandName;

  if (currentBrandName !== originalBrandName) {
    hasFormChanges = true;
    console.log(
      "Form change detected: brand",
      currentBrandName,
      "vs",
      originalBrandName
    );
  }

  // ตรวจสอบการเปลี่ยนแปลงของฟิลด์อื่นๆ
  if (!hasFormChanges) {
    for (const field of fieldCheckOldData) {
      const currentValue = formData.value[field];
      const originalValue = props.saleItemDetail?.[field];

      if (currentValue !== originalValue) {
        hasFormChanges = true;
        break;
      }
    }

    // ตรวจสอบ color field แยกต่างหาก (มี normalize)
    if (!hasFormChanges) {
      const currentColor = normalize(formData.value.color);
      const originalColor = normalize(props.saleItemDetail?.color);

      if (currentColor !== originalColor) {
        hasFormChanges = true;
      }
    }
  }

  // ตรวจสอบการเปลี่ยนแปลงของรูปภาพ
  const hasImageChanges = checkImageChanges();

  // มีการเปลี่ยนแปลงหรือไม่
  const hasAnyChanges = hasFormChanges || hasImageChanges;

  isSave.value = !hasAnyChanges;
});

const uploadInput = ref(null);

const uploadImage = (event) => {
  const MAX_SIZE_BYTES = 2 * 1024 * 1024;
  const files = Array.from(event.target.files);

  // นับรูปที่ยังไม่ถูกลบ
  const activeImagesCount = images.value.filter(img => !img.isDeleted).length;
  const availableSlots = 4 - activeImagesCount;

  // ถ้าไม่มีช่องว่าง
  if (availableSlots === 0) {
    errorTitle.value = "No Available Slots";
    errorMessage.value = "All 4 image slots are occupied. Please remove some images first.";
    showModal.value = true;
    event.target.value = "";
    return;
  }

  // ถ้าจำนวนไฟล์เกินช่องว่างที่มี
  let filesToProcess = files;
  let warningMessage = "";
  
  if (files.length > availableSlots) {
    filesToProcess = files.slice(0, availableSlots);
    warningMessage = `Only ${availableSlots} images can be uploaded. ${files.length - availableSlots} files were skipped.`;
  }

  const checkSizeAndUploadFile = (file, i) => {
    if (file && file.size > MAX_SIZE_BYTES) {
      errorTitle.value = "File Size Too Large";
      errorMessage.value = `File "${file.name}" exceeds the 2MB limit.`;
      showModal.value = true;
    } else {
      const reader = new FileReader();
      reader.onload = (e) => {
        const fileName = generateUniqueFileName(file.name);

        const image = {
          imageName: fileName,
          src: e.target.result,
          file,
          isOriginal: false, // รูปใหม่
          isDeleted: false
        };

        // หาช่องว่างแรกที่ถูกลบหรือเพิ่มที่ท้าย
        let insertIndex = images.value.findIndex(img => img.isDeleted);
        
        if (insertIndex === -1) {
          // ไม่มีช่องว่างที่ถูกลบ ให้เพิ่มที่ท้าย
          images.value.push(image);
          insertIndex = images.value.length - 1;
        } else {
          // มีช่องว่างที่ถูกลบ ให้แทนที่ช่องนั้น
          images.value[insertIndex] = image;
        }

        // แสดงรูปแรกที่อัปโหลดถ้าไม่มีรูปที่แสดงอยู่
        if (showImageIndex.value === -1 || images.value[showImageIndex.value]?.isDeleted) {
          showImageIndex.value = insertIndex;
          showImage.value = e.target.result;
        }
      };
      reader.readAsDataURL(file);
    }
  };

  filesToProcess.forEach((file, i) => {
    checkSizeAndUploadFile(file, i);
  });

  // แสดงคำเตือนถ้ามีไฟล์ถูก skip
  if (warningMessage) {
    setTimeout(() => {
      errorTitle.value = "Upload Limited";
      errorMessage.value = warningMessage;
      showModal.value = true;
    }, 100);
  }

  event.target.value = "";
};

const generateUniqueFileName = (originalName) => {
  const isDuplicate = images.value.some(
    (img) => img.imageName === originalName
  );

  if (!isDuplicate) {
    return originalName;
  }

  const nameParts = originalName.split(".");
  const baseName = nameParts.slice(0, -1).join(".") || nameParts[0];
  const ext = nameParts.length > 1 ? "." + nameParts[nameParts.length - 1] : "";

  let suffix = 1;
  let newName = `${baseName}(${suffix})${ext}`;

  while (images.value.some((img) => img.imageName === newName)) {
    suffix++;
    newName = `${baseName}(${suffix})${ext}`;
  }

  return newName;
};

const swapUpImage = (i) => {
  if (i > 0) {
    let temp = images.value[i];
    images.value[i] = images.value[i - 1];
    images.value[i - 1] = temp;
    // ไม่ต้องเซ็ต hasImageChanges.value ที่นี่ เพราะ watchEffect จะจัดการให้

    // อัปเดต showImage ถ้าจำเป็น
    if (showImageIndex.value === i) {
      showImageIndex.value = i - 1;
    } else if (showImageIndex.value === i - 1) {
      showImageIndex.value = i;
    }

    // อัปเดต showImage ถ้าตำแหน่งแรกเปลี่ยน
    if (i - 1 === 0) {
      showImage.value = images.value[0].src;
    }
  }
};

const swapDownImage = (i) => {
  if (i < images.value.length - 1) {
    // อัปเดต showImage ถ้าจำเป็น
    if (showImageIndex.value === i) {
      showImageIndex.value = i + 1;
    } else if (showImageIndex.value === i + 1) {
      showImageIndex.value = i;
    }

    // อัปเดต showImage ถ้าตำแหน่งแรกเปลี่ยน
    if (i === 0) {
      showImage.value = images.value[1].src;
    }

    let temp = images.value[i];
    images.value[i] = images.value[i + 1];
    images.value[i + 1] = temp;
  }
};

// ปรับปรุงฟังก์ชัน removeImage - ไม่ให้รูปเลื่อนขึ้นมา แต่เปลี่ยนเป็นช่องว่าง (ทั้ง create และ edit)
const removeImage = (index) => {
  const imageToRemove = images.value[index];
  
  // ถ้าเป็น edit mode และเป็นรูปเดิม ให้เก็บ fileName ไว้ใน deletedImageFileNames
  if (props.mode === 'edit' && imageToRemove.isOriginal && imageToRemove.imageName) {
    deletedImageFileNames.value.push(imageToRemove.imageName);
  }

  // ทั้ง create และ edit mode: แทนที่จะลบออกจาก array ให้เปลี่ยนเป็น placeholder
  images.value[index] = {
    imageName: '',
    src: null,
    isOriginal: false,
    isDeleted: true, // มาร์คว่าถูกลบแล้ว
    originalPosition: index + 1 // เก็บตำแหน่งเดิมไว้
  };

  // อัปเดต showImage และ index
  if (index === showImageIndex.value) {
    // หารูปถัดไปที่ยังไม่ถูกลบ
    let nextValidImageIndex = -1;
    for (let i = 0; i < images.value.length; i++) {
      if (!images.value[i].isDeleted) {
        nextValidImageIndex = i;
        break;
      }
    }

    if (nextValidImageIndex !== -1) {
      showImageIndex.value = nextValidImageIndex;
      showImage.value = images.value[nextValidImageIndex].src;
    } else {
      // ไม่มีรูปเหลือ
      showImage.value = noPhoto;
      showImageIndex.value = -1;
    }
  }
};
</script>

<template>
  <div class="mt-10 lg:mt-0 lg:mx-16 mb-14">
    <div class="flex flex-col lg:flex-row">
      <div
        class="w-full lg:w-2/4 mx-auto mb-10 lg:mb-10 flex flex-col items-center gap-4"
      >
        <div
          class="border-2 rounded-2xl w-4/5 md:w-2/5 lg:w-2/3 max-h-[450px] mx-auto p-8"
        >
          <img
            :src="showImage"
            :class="images.length != 0 ? 'object-cover' : 'object-contain'"
            class="w-full h-full rounded-2xl"
            alt=""
          />
        </div>
        <div
          class="flex gap-4 mt-10 w-full justify-center items-center overflow-auto"
        >
          <div
            v-for="(image, index) in images"
            :key="`image-${index}`"
            class="relative flex flex-col items-center"
          >
            <div
              v-if="image.isDeleted"
              class="max-h-[60px] lg:max-h-[80px] h-[60px] lg:h-[80px] w-14 lg:w-18 border-2 border-dashed border-gray-300 rounded-2xl flex items-center justify-center bg-gray-50 opacity-60"
            >
              <svg class="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </div>
            <img
              v-else
              class="max-h-[60px] lg:max-h-[80px] h-[60px] lg:h-[80px] w-14 lg:w-18 border object-cover rounded-2xl cursor-pointer"
              :src="image.src"
              alt=""
              :class="
                showImageIndex == index
                  ? 'border-2 border-blue-600 hover:border-black'
                  : 'hover:border-blue-500'
              "
              @click="
                showImage = image.src;
                showImageIndex = index;
              "
            />
            <span 
              class="text-xs mt-1"
              :class="image.isDeleted ? 'text-gray-400 line-through' : 'text-gray-600'"
            >
              {{ index + 1 }}
            </span>
            <div class="text-xs mt-1">
              <span v-if="image.isDeleted" class="text-gray-500 font-semibold">
                {{ props.mode === 'create' ? 'Empty' : 'Deleted' }}
              </span>
              <span v-else-if="image.isOriginal" class="text-green-600 font-semibold">Original</span>
              <span v-else class="text-blue-600 font-semibold">New</span>
            </div>
          </div>

                    <!-- แสดงช่องว่างสำหรับรูปที่ยังไม่มี (ถ้าไม่ครบ 4 รูป) -->
          <!-- ไม่ต้องกดเพื่อ upload - เป็นแค่ preview เท่านั้น -->
          <div
            v-for="i in Math.max(0, 4 - images.length)"
            :key="`empty-${i}`"
            class="relative flex flex-col items-center"
          >
            <div
              class="max-h-[60px] lg:max-h-[80px] h-[60px] lg:h-[80px] w-14 lg:w-18 border-2 border-dashed border-gray-300 rounded-2xl flex items-center justify-center bg-gray-50"
            >
              <svg class="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
              </svg>
            </div>
            <!-- แสดงหมายเลขลำดับ -->
            <span class="text-xs text-gray-400 mt-1">{{ images.length + i }}</span>
            <!-- แสดงสถานะ -->
            <div class="text-xs mt-1">
              <span class="text-gray-400">Empty</span>
            </div>
          </div>
        </div>

        <!-- ปุ่ม Upload Image -->
        <div class="flex flex-col items-center gap-2">
          <button
            type="button"
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded cursor-pointer transition-colors duration-200"
            @click="uploadInput.click()"
          >
            Upload Images
          </button>
          <p class="text-sm text-gray-500">
            {{ images.filter(img => !img.isDeleted).length }}/4 images uploaded
          </p>
        </div>

        <!-- Hidden file input -->
        <input
          id="file-upload"
          type="file"
          class="hidden"
          ref="uploadInput"
          multiple
          accept="image/*"
          @change="uploadImage"
        />

        <!-- แสดงข้อมูลสถิติการลบรูป (เฉพาะ edit mode) -->
        <div v-if="props.mode === 'edit' && deletedImageFileNames.length > 0" class="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
          <div class="text-sm text-red-700">
            <span class="font-semibold">Deleted Images ({{ deletedImageFileNames.length }}):</span>
            <div class="mt-1">
              <span v-for="(fileName, index) in deletedImageFileNames" :key="fileName" class="inline-block">
                {{ fileName }}<span v-if="index < deletedImageFileNames.length - 1">, </span>
              </span>
            </div>
          </div>
        </div>

        <!-- แสดงรายชื่อไฟล์และปุ่มจัดการ -->
        <div class="flex flex-col gap-2 items-center justify-center w-full max-w-md">
          <!-- Header สำหรับรายการรูป -->
          <div v-if="images.length > 0" class="w-full">
            <h3 class="text-lg font-semibold text-gray-700 mb-3 text-center">
              ({{ images.filter(img => !img.isDeleted).length }}/4)
              <span v-if="props.mode === 'edit' && deletedImageFileNames.length > 0" class="text-gray-500 text-sm">
                ({{ deletedImageFileNames.length }} deleted)
              </span>
            </h3>
          </div>

          <!-- รายการรูปภาพ -->

          <div
            v-for="(image, i) in images"
            :class="'itbms-picture-file' + i"
            :key="`image-${i}-${image.imageName || 'deleted'}`"
            class="w-full"
          >
            <div v-if="image.isDeleted" class="flex items-center gap-2">
              <div class="flex-shrink-0 w-8 h-8 bg-gray-100 text-gray-500 rounded-full flex items-center justify-center text-sm font-semibold border-2 border-dashed border-gray-300">
                {{ i + 1 }}
              </div>
              
              <div class="flex-1 flex justify-between gap-2 border-2 border-dashed border-gray-300 text-gray-500 text-xs py-2 px-4 rounded bg-gray-50 opacity-60 max-w-sm">
                <div class="flex flex-col flex-1 min-w-0">
                  <p class="truncate font-medium line-through max-w-[200px]">
                    {{ props.mode === 'create' ? '[Empty Slot]' : '[Deleted Slot]' }}
                  </p>
                  <div class="flex gap-2 mt-1 flex-wrap">
                    <span class="text-gray-500 bg-gray-200 px-2 py-0.5 rounded text-xs font-semibold whitespace-nowrap">
                      {{ props.mode === 'create' ? 'Removed' : 'Deleted' }}
                    </span>
                  </div>
                </div>
                <div class="text-gray-400 text-xs self-center">
                  {{ props.mode === 'create' ? 'Can be filled' : 'Will be removed' }}
                </div>
              </div>
            </div>

            <div v-else class="flex items-center gap-2">
              <div class="flex-shrink-0 w-8 h-8 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center text-sm font-semibold">
                {{ i + 1 }}
              </div>
              
              <div
                class="flex-1 flex justify-between gap-2 border text-xs py-2 px-4 rounded transition-colors max-w-sm"
                :class="image.isOriginal 
                  ? 'border-green-500 text-green-700 bg-green-50' 
                  : 'border-blue-500 text-blue-700 bg-blue-50'"
              >
                <div class="flex flex-col flex-1 min-w-0">
                  <p class="truncate font-medium max-w-[200px]" :title="image.imageName">
                    {{ image.imageName }}
                  </p>
                  <div class="flex gap-2 mt-1 flex-wrap">
                    <span 
                      :class="image.isOriginal ? 'text-green-600 bg-green-100' : 'text-blue-600 bg-blue-100'"
                      class="px-2 py-0.5 rounded text-xs font-semibold whitespace-nowrap"
                    >
                      {{ image.isOriginal ? 'Original' : 'New' }}
                    </span>
                  </div>
                </div>
                <button
                  class="text-red-500 cursor-pointer hover:text-red-700 hover:bg-red-100 p-1 rounded transition-colors"
                  :class="'itbms-picture-file' + i + '-clear'"
                  @click="removeImage(i)"
                  title="Delete image"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                  </svg>
                </button>
              </div>
              
              <div
                class="flex flex-col gap-1"
                :class="
                  i == 0
                    ? 'self-end mb-2'
                    : i == images.length - 1
                    ? 'self-start mt-2'
                    : ''
                "
              >
                <button
                  class="cursor-pointer hover:opacity-70 p-1 bg-gray-100 rounded hover:bg-gray-200 transition-colors"
                  :class="'itbms-picture-file' + i + '-up'"
                  v-if="i != 0"
                  @click="swapUpImage(i)"
                  title="Move up"
                >
                  <img
                    class="w-3 h-3"
                    src="../assets/icons/bxs--up-arrow.svg"
                    alt=""
                  />
                </button>
                <button
                  class="cursor-pointer hover:opacity-70 p-1 bg-gray-100 rounded hover:bg-gray-200 transition-colors"
                  :class="'itbms-picture-file' + i + '-down'"
                  v-if="i < images.length - 1"
                  @click="swapDownImage(i)"
                  title="Move down"
                >
                  <img
                    class="w-3 h-3"
                    src="../assets/icons/bxs--down-arrow.svg"
                    alt=""
                  />
                </button>
              </div>
            </div>
          </div>
          
          <!-- แสดงช่องว่างที่ยังเหลือ -->
          <!-- ไม่แสดงช่องว่างใหม่หลังจากลบรูป -->
          <div v-if="images.length < 4" class="w-full mt-2">
            <div class="flex flex-col gap-2">
              <div
                v-for="emptySlot in Math.max(0, 4 - images.length)"
                :key="`empty-slot-${emptySlot}`"
                class="flex items-center gap-2 opacity-50"
              >
                <div class="flex-shrink-0 w-8 h-8 bg-gray-100 text-gray-400 rounded-full flex items-center justify-center text-sm border-2 border-dashed">
                  {{ images.length + emptySlot }}
                </div>
                <div class="flex-1 border-2 border-dashed border-gray-300 text-gray-400 text-xs py-2 px-4 rounded bg-gray-50 max-w-sm">
                  <p class="truncate max-w-[200px]">Empty slot - Click "Upload Images" to add</p>
                  <span class="text-gray-400 text-xs whitespace-nowrap">Available position</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div
        class="flex flex-col items-center lg:items-start justify-center lg:justify-start xl:w-2/4"
      >
        <div
          class="grid grid-cols-[auto_1fr] md:grid-cols-[auto_260px] xl:grid-cols-[auto_320px] items-center gap-x- md:gap-x-6 gap-y-4 px-4"
        >
          <label class="text-sm md:text-base" for="brand"
            >Brand<span class="text-red-500"> *</span></label
          >
          <div class="flex-col">
            <select
              id="brand"
              class="itbms-brand select"
              v-model="formData.brand"
              @blur="brandSelected(true)"
            >
              <option value="">Select Brand</option>
              <option
                v-for="(brand, i) in brands
                  ?.sort((a, b) => a.name.localeCompare(b.name))
                  ?.filter((b) => b.isActive === true)"
                :value="brand"
                :key="i"
              >
                {{ brand.name }}
              </option>
            </select>
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.brand.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.brand.msg
              }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="model"
            >Model<span class="text-red-500"> *</span></label
          >

          <div class="flex-col">
            <input
              type="text"
              id="model"
              class="itbms-model input"
              v-model.trim="formData.model"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.model.showError,
              }"
              @blur="maximumLength('model', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.model.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.model.msg
              }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="price"
            >Price(Bath)<span class="text-red-500"> *</span></label
          >
          <div class="flex-col">
            <input
              type="number"
              id="price"
              class="itbms-price input"
              v-model="formData.price"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.price.showError,
              }"
              @blur="nonNegative('price', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.price.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.price.msg
              }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="description"
            >Description<span class="text-red-500"> *</span></label
          >
          <div class="flex-col">
            <textarea
              id="description"
              class="itbms-description textarea resize-y"
              v-model.trim="formData.description"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.description.showError,
              }"
              @blur="maximumLength('description', true)"
            ></textarea>
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.description.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.description.msg
              }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="ramGb">Ram (GB)</label>

          <div class="flex-col">
            <input
              type="number"
              id="ramGb"
              class="itbms-ramGb input"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.ramGb.showError,
              }"
              min="1"
              v-model="formData.ramGb"
              @blur="isValidPositiveField('ramGb', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.ramGb.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.ramGb.msg
              }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base max-w-[110px]" for="screenSizeInch"
            >Screen Size (Inch)</label
          >
          <div class="flex-col">
            <input
              type="number"
              id="screenSizeInch"
              class="itbms-screenSizeInch input"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.screenSizeInch.showError,
              }"
              step="0.01"
              min="1"
              v-model="formData.screenSizeInch"
              @blur="isValidPositiveField('screenSizeInch', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.screenSizeInch.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.screenSizeInch.msg
              }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="storageGb"
            >Storage (GB)</label
          >
          <div class="flex-col">
            <input
              type="number"
              id="storageGb"
              class="itbms-storageGb input"
              min="1"
              v-model="formData.storageGb"
              @blur="isValidPositiveField('storageGb', true)"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.storageGb.showError,
              }"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.storageGb.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.storageGb.msg
              }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="color">Color</label>
          <div class="flex-col">
            <input
              type="text"
              id="color"
              class="itbms-color input"
              v-model.trim="formData.color"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.color.showError,
              }"
              @blur="maximumLength('color', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.color.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.color.msg
              }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="quantity"
            >Quantity<span class="text-red-500"> *</span></label
          >
          <div class="flex-col">
            <input
              type="number"
              id="quantity"
              class="itbms-quantity input"
              min="1"
              value="1"
              v-model="formData.quantity"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.quantity.showError,
              }"
              @blur="nonNegative('quantity', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.quantity.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.quantity.msg
              }}</span>
            </div>
          </div>

          <div
            class="flex items- jus w-full gap-4 col-span-2 justify-center mt-4 lg:justify-end"
          >
            <button
              @click="handleSubmit"
              class="itbms-save-button btn"
              :class="isSave || disableSaveBtn ? '' : 'btn-primary'"
              :disabled="isSave || disableSaveBtn"
            >
              Save
            </button>

            <button @click="$router.back()" class="itbms-cancel-button btn">
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <MessageModal
    v-if="showModal"
    :handle-ok="() => (showModal = false)"
    :title="errorTitle"
    :message="errorMessage"
  />
</template>

<style scoped></style>
