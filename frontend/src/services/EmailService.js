const EMAIL_API_URL = `${
  import.meta.env.VITE_URL_API || "/ssi1"
}/itb-mshop/v2/users`;

const verifyEmail = async (token) => {
  try {
    const response = await fetch(
      `${EMAIL_API_URL}/verify-token?jwtToken=${token}`,
      {
        method: "POST",
      }
    );

    if (response.status !== 200) {
      throw new Error(
        `Failed to verify token. Server responded with status ${response.status}`
      );
    }

    const activateEmail = await fetch(
      `${EMAIL_API_URL}/verify-email?jwtToken=${token}`,
      {
        method: "POST",
      }
    );

    if (activateEmail.status !== 200) {
      throw new Error(
        `Failed to verify email. Server responded with status ${response.status}`
      );
    }


    return true;
  } catch (error) {
    return {
      error: `Error: ${error.message}`,
    };
  }
};

export { verifyEmail };
