const USER_API_URL = `${
  import.meta.env.VITE_URL_API || "/ssi1"
}/itb-mshop/v2/users`;

const createUser = async (userDetail, userType, nationalIdPhoto = null) => {
  try {
    const formData = new FormData();

    for (const [key, value] of Object.entries(userDetail)) {
      formData.append(key, value);
    }

    if (nationalIdPhoto) {
      for (const [key, image] of Object.entries(nationalIdPhoto)) {
        if (image) {
          const ext = image.name.includes(".")
            ? image.name.substring(image.name.lastIndexOf("."))
            : "";

          const baseName = image.name.replace(ext, "");
          const fileName =
            baseName + (key === "idCardImageFront" ? "_fs_" : "_bs_") + ext;

          formData.append("images", image, fileName);
        }
      }
    }

    formData.append("userType", userType);

    const response = await fetch(`${USER_API_URL}/register`, {
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
      body: JSON.stringify({ email, password }),
      credentials: "include",
    });

    if (response.status === 200) {
      return { success: true };
    } else if (response.status === 401 || response.status === 403) {
      const { message } = await response.json();
      throw new Error(message);
    } else {
      throw new Error(
        `Failed to log in. Server responded with status ${response.status}`
      );
    }
  } catch (error) {
    return {
      success: false,
      error: error.message,
    };
  }
};

const getProfile = async () => {
  try {
    const response = await fetch(USER_API_URL, {
      method: "GET",
      credentials: "include",
    });
    if (response.status === 403) {
      return {
        logined: false,
      };
    } else if (response.status != 200) {
      throw new Error(
        `Failed to get Profile. Server responded with status ${response.status}`
      );
    }
    const data = await response.json();
    return {
      logined: true,
      data,
    };
  } catch (error) {
    return {
      success: false,
      error: error.message,
    };
  }
};

const getImageByFileName = async (fileName) => {
  try {
    if (!fileName) throw new Error("fileName is required.");
    const response = await fetch(
      `${import.meta.env.VITE_URL_API || "/ssi1"}/itb-mshop/users/${fileName}`,
      {
        mode: "cors",
      }
    );

    if (!response.ok) {
      throw new Error(
        `HTTP Error: ${response.status} - Failed to fetch image ${fileName}`
      );
    }

    const data = await response.blob();
    const url = URL.createObjectURL(data);

    return url;
  } catch (error) {
    console.error(`Error fetching image ${fileName}:`, error);
    return { error: error.message };
  }
};
const getUserById = async (id) => {
  try {
    if (!id) throw new Error("User ID is required.");
    const response = await fetch(`${USER_API_URL}/${id}`, {
      method: "GET",
      credentials: "include",
    });

    const data = await response.json();

    if (!data.id || !response.ok) {
      throw new Error(`(${data.status || response.status}) ${data.message}`);
    }

    return data;
  } catch (error) {
    return { error };
  }
};
const logout = async () => {
  try {
    const response = await fetch(`${USER_API_URL}/logout`, {
      method: "POST",
      credentials: "include",
    });

    if (response.ok) {
      console.log("Success");

      return { success: true };
    }
    throw new Error("logout fail");
  } catch (error) {
    return {
      success: false,
      error: error.message,
    };
  }
};

const updateUserById = async (id, updateUser) => {
  try {
    if (!id) throw new Error("User ID is required.");
    const response = await fetch(`${USER_API_URL}/${id}`, {
      method: "PUT",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(updateUser),
    });

    const data = await response.json();

    if (!data.id || !response.ok) {
      throw new Error(`(${data.status || response.status}) ${data.message}`);
    }

    return data;
  } catch (error) {
    return { error };
  }
};

export {
  createUser,
  login,
  getProfile,
  getImageByFileName,
  getUserById,
  logout,
  updateUserById
};
