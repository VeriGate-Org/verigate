const COGNITO_REGION = process.env.NEXT_PUBLIC_COGNITO_REGION || "eu-west-1";
const COGNITO_CLIENT_ID = process.env.NEXT_PUBLIC_COGNITO_CLIENT_ID || "";
const COGNITO_ENDPOINT = `https://cognito-idp.${COGNITO_REGION}.amazonaws.com/`;

export interface AuthTokens {
  accessToken: string;
  idToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface CognitoError {
  __type: string;
  message: string;
}

async function cognitoRequest(action: string, payload: Record<string, unknown>): Promise<unknown> {
  const res = await fetch(COGNITO_ENDPOINT, {
    method: "POST",
    headers: {
      "Content-Type": "application/x-amz-json-1.1",
      "X-Amz-Target": `AWSCognitoIdentityProviderService.${action}`,
    },
    body: JSON.stringify(payload),
  });

  const data = await res.json();

  if (!res.ok) {
    const err = data as CognitoError;
    const type = err.__type?.split("#").pop() || "UnknownError";
    throw new CognitoAuthError(type, err.message || "Authentication failed");
  }

  return data;
}

export class CognitoAuthError extends Error {
  constructor(
    public readonly code: string,
    message: string,
  ) {
    super(message);
    this.name = "CognitoAuthError";
  }
}

export async function signIn(email: string, password: string): Promise<AuthTokens> {
  const data = (await cognitoRequest("InitiateAuth", {
    AuthFlow: "USER_PASSWORD_AUTH",
    ClientId: COGNITO_CLIENT_ID,
    AuthParameters: {
      USERNAME: email,
      PASSWORD: password,
    },
  })) as {
    AuthenticationResult?: {
      AccessToken: string;
      IdToken: string;
      RefreshToken: string;
      ExpiresIn: number;
    };
    ChallengeName?: string;
  };

  if (data.ChallengeName) {
    throw new CognitoAuthError(
      "ChallengePending",
      `Authentication challenge required: ${data.ChallengeName}`,
    );
  }

  if (!data.AuthenticationResult) {
    throw new CognitoAuthError("NoResult", "No authentication result returned");
  }

  return {
    accessToken: data.AuthenticationResult.AccessToken,
    idToken: data.AuthenticationResult.IdToken,
    refreshToken: data.AuthenticationResult.RefreshToken,
    expiresIn: data.AuthenticationResult.ExpiresIn,
  };
}

export async function refreshSession(refreshToken: string): Promise<Omit<AuthTokens, "refreshToken">> {
  const data = (await cognitoRequest("InitiateAuth", {
    AuthFlow: "REFRESH_TOKEN_AUTH",
    ClientId: COGNITO_CLIENT_ID,
    AuthParameters: {
      REFRESH_TOKEN: refreshToken,
    },
  })) as {
    AuthenticationResult: {
      AccessToken: string;
      IdToken: string;
      ExpiresIn: number;
    };
  };

  return {
    accessToken: data.AuthenticationResult.AccessToken,
    idToken: data.AuthenticationResult.IdToken,
    expiresIn: data.AuthenticationResult.ExpiresIn,
  };
}

export async function forgotPassword(email: string): Promise<void> {
  await cognitoRequest("ForgotPassword", {
    ClientId: COGNITO_CLIENT_ID,
    Username: email,
  });
}

export async function confirmForgotPassword(
  email: string,
  code: string,
  newPassword: string,
): Promise<void> {
  await cognitoRequest("ConfirmForgotPassword", {
    ClientId: COGNITO_CLIENT_ID,
    Username: email,
    ConfirmationCode: code,
    Password: newPassword,
  });
}

export function decodeJwtPayload(token: string): Record<string, unknown> {
  const parts = token.split(".");
  if (parts.length !== 3) throw new Error("Invalid JWT");
  const payload = parts[1].replace(/-/g, "+").replace(/_/g, "/");
  return JSON.parse(atob(payload));
}
