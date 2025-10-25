const USER_API_URL = `${
  import.meta.env.VITE_URL_API || "/ssi1"
}/itb-mshop/v2/users`;

const AUTH_API_URL = `${
  import.meta.env.VITE_URL_API || "/ssi1"
}/itb-mshop/v2/auth`;

import { useUserStore } from "@/stores/UserStore";

const getAccessTokenFromLocalStorage = () => {
  // Keep function name for compatibility, but read token from Pinia user store
  try {
    const userStore = useUserStore();
    return userStore.getAccessToken();
  } catch (err) {
    // fallback to localStorage if store isn't available
    return localStorage.getItem("access_token");
  }
};

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

    const response = await fetch(`${AUTH_API_URL}/register`, {
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

    const headers = {
      "Content-Type": "application/json",
    };

    const response = await fetch(`${AUTH_API_URL}/login`, {
      method: "POST",
      headers,
      body: JSON.stringify({ email, password }),
      credentials: "include",
    });
    const data = await response.json();
    if (data.access_token) {
      try {
        const userStore = useUserStore();
        userStore.setAccessToken(data.access_token);
        localStorage.setItem("access_token", data.access_token);
      } catch (err) {
        localStorage.setItem("access_token", data.access_token);
      }
    }

    if (response.status === 200) {
      return { success: true, token: data.access_token };
    } else if (response.status === 401 || response.status === 403) {
      // response body already parsed into `data` above; reuse it instead of
      // calling response.json() again which would fail with "body stream already read"
      const message = data && data.message ? data.message : "Unauthorized";
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

const getAccessTokenFromRefreshToken = async () => {
  try {
    const response = await fetch(`${AUTH_API_URL}/refresh`, {
      method: "POST",
      credentials: "include",
    });
    const data = await response.json();
    if (data.access_token) {
      try {
        const userStore = useUserStore();
        userStore.setAccessToken(data.access_token);
        localStorage.setItem("access_token", data.access_token);
      } catch (err) {
        localStorage.setItem("access_token", data.access_token);
      }
    }

    if (response.status != 200) {
      throw new Error("get access_token not successful...");
    }
    return { ok: response.ok, access_token: data.access_token };
  } catch (err) {
    return { error: err };
  }
};

const getProfile = async ( retryCount = 0) => {
  try {
    const response = await fetch(USER_API_URL, {
      method: "GET",
      headers: {
        Authorization:
          getAccessTokenFromLocalStorage()
            ? `Bearer ${ getAccessTokenFromLocalStorage()}`
            : "",
      },
    });

    if (response.status === 403) {
      return { logined: false };
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
    if (retryCount < 1) {
      console.log("use refresh!");

      try {
        const userStore = useUserStore();
        userStore.setAccessToken(null);
        localStorage.removeItem("access_token");
      } catch (err) {
        localStorage.removeItem("access_token");
      }
      const res = await getAccessTokenFromRefreshToken();
      if (res.ok) {
        return await getProfile(token, retryCount + 1);
      }
    }

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
      headers: {
        Authorization:
          getAccessTokenFromLocalStorage()
            ? `Bearer ${ getAccessTokenFromLocalStorage()}`
            : "",
      },
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
    const response = await fetch(`${AUTH_API_URL}/logout`, {
      method: "POST",
      headers: {
        Authorization:
          getAccessTokenFromLocalStorage()
            ? `Bearer ${ getAccessTokenFromLocalStorage()}`
            : "",
      },
      credentials: "include",
    });

    if (response.ok) {
      try {
        const userStore = useUserStore();
        userStore.setAccessToken(null);
        localStorage.removeItem("access_token");
      } catch (err) {
        localStorage.removeItem("access_token");
      }
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
      headers: {
        "Content-Type": "application/json",
        Authorization:
         getAccessTokenFromLocalStorage()
            ? `Bearer ${getAccessTokenFromLocalStorage()}`
            : "",
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
const changePassword = async (id, updatePassword) => {
  try {
    if (!id) throw new Error("User ID is required.");
    const response = await fetch(`${USER_API_URL}/${id}/password`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization:
          getAccessTokenFromLocalStorage()
            ? `Bearer ${ getAccessTokenFromLocalStorage()}`
            : "",
      },
      body: JSON.stringify(updatePassword),
    });

    const data = await response.json();

    if (!data.success) {
      throw new Error(`(${data.status || response.status}) ${data.message}`);
    }

    return data;
  } catch (error) {
    return { error };
  }
};

const forgotPassword = async (email) => {
  try {
    if (!email) throw new Error("Email is required.");

    const headers = {
      "Content-Type": "application/json",
    };

    const response = await fetch(`${AUTH_API_URL}/forgot-password`, {
      method: "POST",
      headers,
      body: JSON.stringify({ email }),
    });

    const data = await response.json();

    if (response.ok) {
      return { success: true, data };
    }

    const message =
      data && data.message
        ? data.message
        : `Server responded with status ${response.status}`;
    throw new Error(message);
  } catch (error) {
    return {
      success: false,
      error: error.message,
    };
  }
};

const resetPassword = async (token, resetPassword) => {
  try {
    if (!resetPassword)
      throw new Error("Password and Confirm Password is required.");

    const headers = {
      "Content-Type": "application/json",
    };

    const response = await fetch(
      `${AUTH_API_URL}/reset-password?token=${token}`,
      {
        method: "POST",
        headers,
        body: JSON.stringify(resetPassword),
      }
    );

    const data = await response.json();

    if (response.ok) {
      return { success: true, data };
    }

    const message =
      data && data.message
        ? data.message
        : `Server responded with status ${response.status}`;
    throw new Error(message);
  } catch (error) {
    return {
      success: false,
      error: error.message,
    };
  }
};

export {
  createUser,
  login,
  getProfile,
  getImageByFileName,
  getUserById,
  logout,
  updateUserById,
  changePassword,
  forgotPassword,
  resetPassword,
  getAccessTokenFromLocalStorage,
};
