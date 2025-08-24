const USER_API_URL = `${
  import.meta.env.VITE_URL_API || "/ssi1"
}/itb-mshop/v1/register`;

const createUser = async (userDetail, userType, nationalIdPhoto = null) => {
  try {
    const formData = new FormData();

    for (const [key, value] of Object.entries(userDetail)) {
      formData.append(key, value);
    }

    console.log(nationalIdPhoto);
    
    if (nationalIdPhoto) {
      for (const [key, image] of Object.entries(nationalIdPhoto)) {
        
        if (image) {
          const fieldName =
            key === "front" ? "nationalIdPhotoFront" : "nationalIdPhotoBack";

          const ext = image.name.includes(".")
            ? image.name.substring(image.name.lastIndexOf("."))
            : ""; 

          const baseName = image.name.replace(ext, ""); 
          const fileName = baseName + (key === "front" ? "_fs_" : "_bs_") + ext;
          
          formData.append("images", image, fileName);
        }
      }
    }
    

    const response = await fetch(
      `${USER_API_URL}` + "?userType=" + `${userType.toLowerCase()}`,
      {
        method: "POST",
        body: formData,
      }
    );

    if (!response.ok) {
      console.log(response);
      throw new Error(
        `Failed to create user. Server responded with status ${response.status}`
      );
    }

    return await response.json();
  } catch (error) {
    return {
      error: `Error creating user: ${error.message}`,
    };
  }
  
};

export { createUser };
