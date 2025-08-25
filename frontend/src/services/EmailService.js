const EMAIL_API_URL = `${
  import.meta.env.VITE_URL_API || "/ssi1"
}/itb-mshop/verify-email`;

const verifyEmail = async (token) => {
  try {

    const response = await fetch(`${EMAIL_API_URL}?jwtToken=${token}`, {
      method: "GET",
    });

    if (response.status !== 200) {
      throw new Error(
        `Failed to verify token. Server responded with status ${response.status}`
      );
    }

    const data = await response.json();

    return data;
  } catch (error) {
    return {
      error: `Error: ${error.message}`,
    };
  }
};

export {
    verifyEmail
}
