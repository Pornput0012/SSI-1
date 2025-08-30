const USER_API_URL = `${
  import.meta.env.VITE_URL_API || "/ssi1"
}/itb-mshop/v2/users`;

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

          const ext = image.name.includes(".")
            ? image.name.substring(image.name.lastIndexOf("."))
            : ""; 

          const baseName = image.name.replace(ext, ""); 
          const fileName = baseName + (key === "idCardImageFront" ? "_fs_" : "_bs_") + ext;
          
          formData.append("images", image, fileName);
        }
      }
    }

    formData.append('userType',userType)
    

    const response = await fetch(
      `${USER_API_URL}/register`,
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

const login = async (email, password) => {
  try {
    if (!email || !password) {
      throw new Error("Please enter your email and password!");
      }

      const response = await fetch(`${USER_API_URL}/authentications`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({email, password})
      })

      if (response.status === 200) {
        return { success: true }
      } else if (response.status === 401) {
        throw new Error("Username or Password is incorrect!")
      } else {
        throw new Error("Something went wrong!");
      }
  } catch (error) {
    return {
      success: false,
      error: error.message
    }
  }
}

export { createUser, login };
