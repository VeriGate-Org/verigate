"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import {
  signIn as cognitoSignIn,
  refreshSession,
  decodeJwtPayload,
  CognitoAuthError,
  type AuthTokens,
} from "./cognito-client";

const AUTH_DISABLED = process.env.NEXT_PUBLIC_AUTH_DISABLED !== "false";
const STORAGE_KEY = "verigate-auth";

export interface UserInfo {
  email: string;
  partnerId: string;
  partnerName: string;
  role: "admin" | "partner";
  groups: string[];
}

interface AuthState {
  user: UserInfo | null;
  accessToken: string | null;
  loading: boolean;
}

interface AuthContextValue extends AuthState {
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function extractUserInfo(idToken: string): UserInfo {
  const claims = decodeJwtPayload(idToken);
  const groups = (claims["cognito:groups"] as string[]) || [];
  return {
    email: (claims.email as string) || "",
    partnerId: (claims["custom:partnerId"] as string) || "",
    partnerName: (claims["custom:partnerName"] as string) || "",
    role: groups.includes("admin") ? "admin" : "partner",
    groups,
  };
}

function saveTokens(tokens: AuthTokens): void {
  try {
    sessionStorage.setItem(
      STORAGE_KEY,
      JSON.stringify({
        accessToken: tokens.accessToken,
        idToken: tokens.idToken,
        refreshToken: tokens.refreshToken,
        expiresAt: Date.now() + tokens.expiresIn * 1000,
      }),
    );
  } catch {
    // sessionStorage may be unavailable
  }
}

function loadTokens(): {
  accessToken: string;
  idToken: string;
  refreshToken: string;
  expiresAt: number;
} | null {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

function clearTokens(): void {
  try {
    sessionStorage.removeItem(STORAGE_KEY);
  } catch {
    // ignore
  }
}

const MOCK_USER: UserInfo = {
  email: "dev@verigate.com",
  partnerId: "partner-portal",
  partnerName: "Development Partner",
  role: "admin",
  groups: ["admin"],
};

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<AuthState>({
    user: null,
    accessToken: null,
    loading: true,
  });

  // Restore session on mount
  useEffect(() => {
    if (AUTH_DISABLED) {
      setState({ user: MOCK_USER, accessToken: null, loading: false });
      return;
    }

    const stored = loadTokens();
    if (!stored) {
      setState((s) => ({ ...s, loading: false }));
      return;
    }

    // If token is expired or near expiry (< 5 min), try refresh
    if (stored.expiresAt - Date.now() < 5 * 60 * 1000) {
      refreshSession(stored.refreshToken)
        .then((fresh) => {
          const tokens: AuthTokens = {
            accessToken: fresh.accessToken,
            idToken: fresh.idToken,
            refreshToken: stored.refreshToken,
            expiresIn: fresh.expiresIn,
          };
          saveTokens(tokens);
          setState({
            user: extractUserInfo(fresh.idToken),
            accessToken: fresh.accessToken,
            loading: false,
          });
        })
        .catch(() => {
          clearTokens();
          setState({ user: null, accessToken: null, loading: false });
        });
    } else {
      setState({
        user: extractUserInfo(stored.idToken),
        accessToken: stored.accessToken,
        loading: false,
      });
    }
  }, []);

  // Schedule token refresh before expiry
  useEffect(() => {
    if (AUTH_DISABLED || !state.accessToken) return;

    const stored = loadTokens();
    if (!stored) return;

    const msUntilRefresh = Math.max(0, stored.expiresAt - Date.now() - 5 * 60 * 1000);
    const timer = setTimeout(() => {
      refreshSession(stored.refreshToken)
        .then((fresh) => {
          const tokens: AuthTokens = {
            accessToken: fresh.accessToken,
            idToken: fresh.idToken,
            refreshToken: stored.refreshToken,
            expiresIn: fresh.expiresIn,
          };
          saveTokens(tokens);
          setState((s) => ({
            ...s,
            user: extractUserInfo(fresh.idToken),
            accessToken: fresh.accessToken,
          }));
        })
        .catch(() => {
          clearTokens();
          setState({ user: null, accessToken: null, loading: false });
        });
    }, msUntilRefresh);

    return () => clearTimeout(timer);
  }, [state.accessToken]);

  const signIn = useCallback(async (email: string, password: string) => {
    const tokens = await cognitoSignIn(email, password);
    saveTokens(tokens);
    setState({
      user: extractUserInfo(tokens.idToken),
      accessToken: tokens.accessToken,
      loading: false,
    });
  }, []);

  const signOut = useCallback(() => {
    clearTokens();
    setState({ user: null, accessToken: null, loading: false });
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      ...state,
      signIn,
      signOut,
      isAuthenticated: AUTH_DISABLED || !!state.user,
    }),
    [state, signIn, signOut],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}

export function useUser(): UserInfo | null {
  const { user } = useAuth();
  return user;
}

export { CognitoAuthError };
