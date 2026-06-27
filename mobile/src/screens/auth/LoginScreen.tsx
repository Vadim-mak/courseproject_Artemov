import React, { useState } from "react";
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
  Alert,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { Ionicons } from "@expo/vector-icons";
import { useAuth } from "../../context/AuthContext";
import { colors, spacing, typography, radius } from "../../theme/theme";
import InputField from "../../components/InputField";
import Button from "../../components/Button";
import type { AuthStackParams } from "../../navigation/AuthNavigator";

type Props = {
  navigation: NativeStackNavigationProp<AuthStackParams, "Login">;
};

/**
 * Экран аутентификации. Отправляет POST /api/auth/login, сохраняет
 * JWT в AsyncStorage через AuthContext.signIn (аналог localStorage в
 * оригинальном веб-проекте).
 */
export default function LoginScreen({ navigation }: Props) {
  const { signIn } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errors, setErrors] = useState<{ email?: string; password?: string }>({});

  const validate = () => {
    const e: typeof errors = {};
    if (!email.trim()) e.email = "Введите email";
    else if (!/^\S+@\S+\.\S+$/.test(email)) e.email = "Некорректный email";
    if (!password) e.password = "Введите пароль";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleLogin = async () => {
    if (!validate()) return;
    setIsLoading(true);
    try {
      await signIn(email.trim().toLowerCase(), password);
    } catch (err: any) {
      Alert.alert("Ошибка входа", err.message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : undefined}
      style={styles.flex}
    >
      <ScrollView
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled"
      >
        {/* Header */}
        <View style={styles.header}>
          <View style={styles.logoWrapper}>
            <Ionicons name="airplane" size={38} color="#fff" />
          </View>
          <Text style={styles.brand}>TourAgency</Text>
          <Text style={styles.tagline}>Ваш идеальный отдых</Text>
        </View>

        {/* Form */}
        <View style={styles.form}>
          <Text style={typography.h2}>Вход в аккаунт</Text>
          <Text style={[typography.bodyMuted, { marginBottom: spacing.lg, marginTop: 4 }]}>
            Войдите, чтобы бронировать туры
          </Text>

          <InputField
            label="Email"
            value={email}
            onChangeText={setEmail}
            keyboardType="email-address"
            placeholder="name@example.com"
            error={errors.email}
          />

          <View style={{ position: "relative" }}>
            <InputField
              label="Пароль"
              value={password}
              onChangeText={setPassword}
              secureTextEntry={!showPassword}
              placeholder="Ваш пароль"
              error={errors.password}
            />
            <TouchableOpacity
              onPress={() => setShowPassword(!showPassword)}
              style={styles.eyeIcon}
            >
              <Ionicons
                name={showPassword ? "eye-off-outline" : "eye-outline"}
                size={20}
                color={colors.textMuted}
              />
            </TouchableOpacity>
          </View>

          <Button
            title="Войти"
            onPress={handleLogin}
            isLoading={isLoading}
            fullWidth
            style={{ marginTop: spacing.sm }}
          />

          <View style={styles.footer}>
            <Text style={typography.bodyMuted}>Нет аккаунта? </Text>
            <TouchableOpacity onPress={() => navigation.navigate("Register")}>
              <Text style={styles.link}>Зарегистрироваться</Text>
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1, backgroundColor: colors.background },
  container: { flexGrow: 1 },
  header: {
    backgroundColor: colors.primary,
    paddingTop: 70,
    paddingBottom: spacing.xxl,
    alignItems: "center",
  },
  logoWrapper: {
    width: 72,
    height: 72,
    borderRadius: 36,
    backgroundColor: colors.accent,
    alignItems: "center",
    justifyContent: "center",
    marginBottom: spacing.md,
  },
  brand: {
    color: "#fff",
    fontSize: 28,
    fontWeight: "800",
    letterSpacing: -0.4,
  },
  tagline: {
    color: "rgba(255,255,255,0.72)",
    fontSize: 14,
    marginTop: 4,
  },
  form: {
    flex: 1,
    backgroundColor: colors.background,
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    marginTop: -20,
    padding: spacing.lg,
    paddingTop: spacing.xl,
  },
  eyeIcon: {
    position: "absolute",
    right: spacing.md,
    top: 38,
    padding: 4,
  },
  footer: {
    flexDirection: "row",
    justifyContent: "center",
    marginTop: spacing.lg,
  },
  link: {
    color: colors.accent,
    fontWeight: "600",
    fontSize: 15,
  },
});
