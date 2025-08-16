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
      console.log(props.saleItemDetail?.saleItemImages);
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

  const imageData = {
    newImages: images.value.filter((img) => !img.isOriginal), // รูปใหม่ที่อัปโหลด
    deletedImageFileNames: deletedImageFileNames.value, // fileName ของรูปที่ถูกลบ
    reorderedImages: images.value
      .filter((img) => img.isOriginal)
      .map((img, index) => ({
        id: img.id,
        fileName: img.imageName,
        newOrder: index + 1,
      })),
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
  // ตรวจสอบว่ามีรูปใหม่หรือไม่
  const hasNewImages = images.value.some((img) => !img.isOriginal);

  // ตรวจสอบว่ามีรูปถูกลบหรือไม่
  const hasDeletedImages = deletedImageFileNames.value.length > 0;

  // ตรวจสอบการเปลี่ยนลำดับรูปเดิม
  const originalImagesInCurrentOrder = images.value.filter(
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

  // ตรวจสอบจำนวนรูปเปลี่ยนหรือไม่
  const totalOriginalImages = originalImages.value.length;
  const totalCurrentImages = images.value.length;
  const hasImageCountChanged = totalOriginalImages !== totalCurrentImages;

  console.log("Image Change Detection:", {
    hasNewImages,
    hasDeletedImages,
    hasReorderedImages,
    hasImageCountChanged,
    originalCount: totalOriginalImages,
    currentCount: totalCurrentImages,
  });

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
    console.log("Button disabled: validation failed");
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
      console.log("Button disabled: required field missing -", field);
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
        console.log(
          "Form change detected:",
          field,
          currentValue,
          "vs",
          originalValue
        );
        break;
      }
    }

    // ตรวจสอบ color field แยกต่างหาก (มี normalize)
    if (!hasFormChanges) {
      const currentColor = normalize(formData.value.color);
      const originalColor = normalize(props.saleItemDetail?.color);

      if (currentColor !== originalColor) {
        hasFormChanges = true;
        console.log(
          "Form change detected: color",
          currentColor,
          "vs",
          originalColor
        );
      }
    }
  }

  // ตรวจสอบการเปลี่ยนแปลงของรูปภาพ
  const hasImageChanges = checkImageChanges();

  // มีการเปลี่ยนแปลงหรือไม่
  const hasAnyChanges = hasFormChanges || hasImageChanges;

  isSave.value = !hasAnyChanges;

  console.log("Save button state:", {
    isSave: isSave.value,
    hasFormChanges,
    hasImageChanges,
    hasAnyChanges,
  });
});

const uploadInput = ref(null);

const uploadImage = (event) => {
  const MAX_SIZE_BYTES = 2 * 1024 * 1024;
  const files = Array.from(event.target.files);

  // เช็คว่าจำนวนไฟล์ที่มีอยู่แล้ว + ไฟล์ใหม่ เกิน 4 หรือไม่
  if (images.value.length + files.length > 4) {
    errorTitle.value = "File Length Too much";
    errorMessage.value = `Maximum 4 pictures are allowed.`;
    showModal.value = true;
    event.target.value = "";
    return;
  }

  const checkSizeAndUploadFile = (file, i) => {
    if (file && file.size > MAX_SIZE_BYTES) {
      console.log("เกิน 2MB");
      errorTitle.value = "File Size Too Large";
      errorMessage.value = `File "${file.name}" exceeds the 2MB limit.`;
      showModal.value = true;
    } else {
      const reader = new FileReader();
      reader.onload = (e) => {
        if (images.value.length < 4) {
          const fileName = generateUniqueFileName(file.name);
          // ไม่ต้องเซ็ต hasImageChanges.value ที่นี่ เพราะ watchEffect จะจัดการให้

          const image = {
            imageName: fileName,
            src: e.target.result,
            file,
            isOriginal: false, // รูปใหม่
          };
          images.value.push(image);

          if (images.value.length === 1) {
            showImageIndex.value = 0;
            showImage.value = e.target.result;
          }

          console.log("Upload successful, total images:", images.value.length);
          console.log(
            "New images count:",
            images.value.filter((img) => !img.isOriginal).length
          );
        }
      };
      reader.readAsDataURL(file);
    }
  };

  files.forEach((file, i) => {
    checkSizeAndUploadFile(file, i);
  });

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

    console.log("Swapped up, image moved from position", i, "to", i - 1);
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

    // ไม่ต้องเซ็ต hasImageChanges.value ที่นี่ เพราะ watchEffect จะจัดการให้
    console.log("Swapped down, image moved from position", i, "to", i + 1);
  }
};

// ปรับปรุงฟังก์ชัน removeImage
const removeImage = (index) => {
  const imageToRemove = images.value[index];

  console.log("remove active");
  console.log("imageToRemove:", imageToRemove);
  
  // ถ้าเป็นรูปเดิม ให้เก็บ fileName ไว้ใน deletedImageFileNames
  if (imageToRemove.isOriginal && imageToRemove.imageName) {
    console.log("Adding to deletedImageFileNames:", imageToRemove.imageName);
    deletedImageFileNames.value.push(imageToRemove.imageName);
  } else {
    console.log("Not adding to deletedImageFileNames - isOriginal:", imageToRemove.isOriginal, "imageName:", imageToRemove.imageName);
  }

  // ลบรูปออกจาก array
  images.value.splice(index, 1);
  // ไม่ต้องเซ็ต hasImageChanges.value ที่นี่ เพราะ watchEffect จะจัดการให้

  console.log("Removed image at index:", index);
  console.log("Deleted image fileNames:", deletedImageFileNames.value);
  console.log("Remaining images:", images.value.length);

  // อัปเดต showImage และ index
  if (index === showImageIndex.value) {
    if (images.value.length > 0) {
      if (index >= images.value.length) {
        // ถ้า index เกินขอบเขต ให้ใช้รูปสุดท้าย
        showImageIndex.value = images.value.length - 1;
        showImage.value = images.value[showImageIndex.value].src;
      } else {
        // ใช้รูปในตำแหน่งเดิม
        showImage.value = images.value[index].src;
      }
    } else {
      // ไม่มีรูปเหลือ
      showImage.value = noPhoto;
      showImageIndex.value = -1;
    }
  } else if (index < showImageIndex.value) {
    // ถ้าลบรูปที่อยู่ก่อนรูปที่กำลังแสดง ต้องปรับ index
    showImageIndex.value--;
  }

  // ถ้าลบรูปแรกและยังมีรูปเหลือ
  if (index === 0 && images.value.length > 0) {
    showImage.value = images.value[0].src;
    showImageIndex.value = 0;
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
          <img
            v-if="images.length != 0"
            v-for="(image, index) in images"
            class="max-h-[60px] lg:max-h-[80px] h-[60px] lg:h-[80px] w-14 lg:w-18 border object-cover rounded-2xl cursor-pointer"
            :key="index"
            :src="image.src"
            alt=""
            :class="
              showImageIndex == index
                ? 'border-2 hover:border-black'
                : 'hover:border-blue-500'
            "
            @click="
              showImage = image.src;
              showImageIndex = index;
            "
          />
          <img
            v-else
            v-for="i in 4"
            class="max-h-[60px] lg:max-h-[80px] h-full w-14 lg:w-18 border object-cover rounded-2xl cursor-pointer hover:border-blue-500"
            :src="noPhoto"
            alt=""
          />
        </div>
        <!-- upload -->
        <div>
          <label
            for="file-upload"
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded cursor-pointer"
          >
            Upload Pictures
          </label>
          <input
            id="file-upload"
            type="file"
            class="hidden"
            ref="uploadInput"
            multiple
            accept="image/*"
            @change="uploadImage"
          />
        </div>

        <!-- แสดงรายชื่อไฟล์และปุ่มจัดการ -->
        <div class="flex flex-col gap-2 items-center justify-center">
          <div
            v-for="(image, i) in images"
            :class="'itbms-picture-file' + i"
            :key="image.imageName || i"
          >
            <div class="flex items-center gap-2" v-if="image.imageName != ''">
              <div
                class="w-48 flex justify-between gap-2 border border-blue-500 text-blue-500 text-xs py-2 px-4 rounded"
                :class="{ 'border-green-500 text-green-500': image.isOriginal }"
              >
                <p class="mt-0.5 truncate" :title="image.imageName">
                  {{ image.imageName }}
                  <span v-if="image.isOriginal" class="text-green-600 text-xs"
                    >(Original)</span
                  >
                </p>
                <button
                  class="text-red-400 cursor-pointer hover:text-red-600"
                  :class="'itbms-picture-file' + i + '-clear'"
                  @click="removeImage(i)"
                >
                  x
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
                  class="cursor-pointer hover:opacity-70"
                  :class="'itbms-picture-file' + i + '-up'"
                  v-if="i != 0"
                  @click="swapUpImage(i)"
                >
                  <img
                    class="w-2 h-2"
                    src="../assets/icons/bxs--up-arrow.svg"
                    alt=""
                  />
                </button>
                <button
                  class="cursor-pointer hover:opacity-70"
                  :class="'itbms-picture-file' + i + '-down'"
                  v-if="i < images.length - 1"
                  @click="swapDownImage(i)"
                >
                  <img
                    class="w-2 h-2"
                    src="../assets/icons/bxs--down-arrow.svg"
                    alt=""
                  />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ฟอร์มข้อมูลสินค้า (เหมือนเดิม) -->
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
