const USER_API_URL = `${
  import.meta.env.VITE_URL_API || "/ssi1"
}/itb-mshop/v1/user`;

const createUser = async (userDetail, nationalIdPhoto = null,userType) => {
  try {
    const formData = new FormData();

    for (const [key, value] of Object.entries(userDetail)) {
      formData.append(key, value);
    }

    if (nationalIdPhoto) {
      for (const [key, value] of Object.entries(nationalIdPhoto)) {
        if (value != null) {
          formData.append(key == "front" ? "frontSide" : "backSide", value);
        }
      }
    }

    const response = await fetch(`${USER_API_URL}`, {
      method: "POST",
      body: formData,
    });

    if (!response.ok) {
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


export {
  createUser
};