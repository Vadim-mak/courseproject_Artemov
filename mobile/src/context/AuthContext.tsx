import React, {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { TOKEN_KEY } from "../api/client";
import * as api from "../api/ApiFunctions";
import type { UserProfile } from "../types";

interface AuthContextValue {
  user: UserProfile | null;
  token: string | null;
  isLoading: boolean;
  isAdmin: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signUp: (
    fullName: string,
    email: string,
    password: string,
    phone?: string
  ) => Promise<void>;
  signOut: () => Promise<void>;
  refreshProfile: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

/**
 * Провайдер аутентификации. При запуске проверяет, есть ли сохранённый
 * JWT в AsyncStorage, и при наличии — загружает профиль. Аналог
 * localStorage.getItem("token") из оригинального Hotel-Booking проекта.
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Восстановление сессии при запуске
  useEffect(() => {
    (async () => {
      try {
        const savedToken = await AsyncStorage.getItem(TOKEN_KEY);
        if (savedToken) {
          setToken(savedToken);
          const profile = await api.getMyProfile();
          setUser(profile);
        }
      } catch {
        // токен устарел — очищаем хранилище
        await AsyncStorage.removeItem(TOKEN_KEY);
      } finally {
        setIsLoading(false);
      }
    })();
  }, []);

  const signIn = async (email: string, password: string) => {
    const result = await api.login(email, password);
    setToken(result.token);
    const profile = await api.getMyProfile();
    setUser(profile);
  };

  const signUp = async (
    fullName: string,
    email: string,
    password: string,
    phone?: string
  ) => {
    const result = await api.register(fullName, email, password, phone);
    setToken(result.token);
    const profile = await api.getMyProfile();
    setUser(profile);
  };

  const signOut = async () => {
    await api.logout();
    setToken(null);
    setUser(null);
  };

  const refreshProfile = async () => {
    const profile = await api.getMyProfile();
    setUser(profile);
  };

  const isAdmin = user?.roles.includes("ROLE_ADMIN") ?? false;

  return (
    <AuthContext.Provider
      value={{ user, token, isLoading, isAdmin, signIn, signUp, signOut, refreshProfile }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside <AuthProvider>");
  return ctx;
}
