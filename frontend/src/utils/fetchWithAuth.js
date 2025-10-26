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

const fetchWithTokenRetry = async (url, options = {}, retryCount = 0) => {
  try {
    const token = getAccessTokenFromLocalStorage();
    if (token && !options.headers?.Authorization) {
      options.headers = {
        ...options.headers,
        Authorization: `Bearer ${token}`,
      };
    }

    const response = await fetch(url, options);

    if (response.ok) {
      return response;
    }

    if ((response.status === 401 || response.status === 403) && retryCount < 1) {      
      localStorage.removeItem("access_token");
      
      const refreshResult = await getAccessTokenFromRefreshToken();
      
      if (refreshResult.ok && refreshResult.access_token) {
        options.headers = {
          ...options.headers,
          Authorization: `Bearer ${refreshResult.access_token}`,
        };
        
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
    if (!error.status && error.response) {
      error.status = error.response.status;
    }
    throw error;
  }
};

export { fetchWithTokenRetry, getAccessTokenFromLocalStorage };
