import React from "react";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";
import { AuthProvider } from "./src/context/AuthContext";
import RootNavigator from "./src/navigation/RootNavigator";
import { colors } from "./src/theme/theme";

/**
 * Точка входа мобильного приложения (Presentation-слой PCMEF).
 * Взаимодействует с backend-сервером через REST API (AsyncStorage вместо
 * localStorage из исходного Hotel-Booking веб-проекта).
 */
export default function App() {
  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaProvider>
        <AuthProvider>
          <StatusBar style="light" backgroundColor={colors.primary} />
          <RootNavigator />
        </AuthProvider>
      </SafeAreaProvider>
    </GestureHandlerRootView>
  );
}
