import axios from "axios";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { Platform } from "react-native";

/**
 * Базовый адрес backend. Android-эмулятор обращается к хост-машине через
 * 10.0.2.2, iOS-симулятор и web — через localhost. Для физического
 * устройства замените на реальный IP сервера в локальной сети.
 */
const resolveBaseUrl = () => {
  if (Platform.OS === "android") {
    return "http://10.0.2.2:8080/api";
  }
  return "http://localhost:8080/api";
};

export const TOKEN_KEY = "touragency_token";

export const apiClient = axios.create({
  baseURL: resolveBaseUrl(),
  timeout: 15000,
  headers: { "Content-Type": "application/json" },
});

// Подставляем JWT в каждый исходящий запрос (аналог getHeader() из исходного проекта)
apiClient.interceptors.request.use(async (config) => {
  const token = await AsyncStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Единая точка извлечения текста ошибки из ответа сервера (ApiError DTO)
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const message =
      error?.response?.data?.message ||
      error?.message ||
      "Не удалось выполнить запрос. Проверьте подключение к серверу.";
    return Promise.reject(new Error(message));
  }
);

export async function saveToken(token: string) {
  await AsyncStorage.setItem(TOKEN_KEY, token);
}

export async function clearToken() {
  await AsyncStorage.removeItem(TOKEN_KEY);
}

export async function getToken() {
  return AsyncStorage.getItem(TOKEN_KEY);
}
