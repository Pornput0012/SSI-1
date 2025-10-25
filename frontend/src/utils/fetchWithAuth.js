const AUTH_API_URL = `${
  import.meta.env.VITE_URL_API || "/ssi1"
}/itb-mshop/v2/auth`;

const getAccessTokenFromLocalStorage = () => {
  return localStorage.getItem("access_token");
};

const getAccessTokenFromRefreshToken = async () => {
  try {
    const response = await fetch(`${AUTH_API_URL}/refresh`, {
      method: "POST",
      credentials: "include",
    });
    const data = await response.json();
    if (data.access_token) {
      localStorage.setItem("access_token", data.access_token);
    }

    if (response.status != 200) {
      throw new Error("get access_token not successful...");
    }
    return { ok: response.ok, access_token: data.access_token };
  } catch (err) {
    return { error: err };
  }
};

// Utility function สำหรับจัดการ token refresh อัตโนมัติ
const fetchWithTokenRetry = async (url, options = {}, retryCount = 0) => {
  try {
    // เพิ่ม Authorization header ถ้ามี access token
    const token = getAccessTokenFromLocalStorage();
    if (token && !options.headers?.Authorization) {
      options.headers = {
        ...options.headers,
        Authorization: `Bearer ${token}`,
      };
    }

    const response = await fetch(url, options);

    // ถ้า response สำเร็จ ให้ return response
    if (response.ok) {
      return response;
    }

    // ถ้าได้ 401 หรือ 403 และยังไม่เคย retry ให้ลอง refresh token
    if ((response.status === 401 || response.status === 403) && retryCount < 1) {
      console.log("Access token expired, refreshing...");
      
      // ลบ access token เก่า
      localStorage.removeItem("access_token");
      
      // ขอ access token ใหม่
      const refreshResult = await getAccessTokenFromRefreshToken();
      
      if (refreshResult.ok && refreshResult.access_token) {
        // อัพเดท Authorization header ด้วย token ใหม่
        options.headers = {
          ...options.headers,
          Authorization: `Bearer ${refreshResult.access_token}`,
        };
        
        // ลองยิง API อีกรอบ
        return await fetchWithTokenRetry(url, options, retryCount + 1);
      } else {
        localStorage.removeItem("access_token");
        const error = new Error("Session expired. Please login again.");
        error.status = 401;
        throw error;
      }
    }

    return response;
  } catch (error) {
    // ถ้า error ยังไม่มี status ให้เพิ่ม status เข้าไป
    if (!error.status && error.response) {
      error.status = error.response.status;
    }
    throw error;
  }
};

export { fetchWithTokenRetry, getAccessTokenFromLocalStorage };
