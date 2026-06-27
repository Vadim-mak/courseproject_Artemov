import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { ActivityIndicator, View } from "react-native";
import { useAuth } from "../context/AuthContext";
import AuthNavigator from "./AuthNavigator";
import AppNavigator from "./AppNavigator";
import { colors } from "../theme/theme";

const Root = createNativeStackNavigator();

/**
 * Корневой навигатор. Решает, показывать экраны аутентификации или
 * основное приложение в зависимости от состояния AuthContext.
 * Аналог ProtectedRoute из оригинального веб-проекта.
 */
export default function RootNavigator() {
  const { token, isLoading } = useAuth();

  if (isLoading) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center", backgroundColor: colors.background }}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  return (
    <NavigationContainer>
      <Root.Navigator screenOptions={{ headerShown: false }}>
        {!token ? (
          <Root.Screen name="Auth" component={AuthNavigator} />
        ) : (
          <Root.Screen name="App" component={AppNavigator} />
        )}
      </Root.Navigator>
    </NavigationContainer>
  );
}
