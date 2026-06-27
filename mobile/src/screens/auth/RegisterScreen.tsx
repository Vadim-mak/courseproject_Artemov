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
import { useAuth } from "../../context/AuthContext";
import { colors, spacing, typography } from "../../theme/theme";
import InputField from "../../components/InputField";
import Button from "../../components/Button";
import type { AuthStackParams } from "../../navigation/AuthNavigator";

type Props = {
  navigation: NativeStackNavigationProp<AuthStackParams, "Register">;
};

export default function RegisterScreen({ navigation }: Props) {
  const { signUp } = useAuth();
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = () => {
    const e: Record<string, string> = {};
    if (!fullName.trim()) e.fullName = "Введите имя";
    if (!email.trim()) e.email = "Введите email";
    else if (!/^\S+@\S+\.\S+$/.test(email)) e.email = "Некорректный email";
    if (password.length < 6) e.password = "Минимум 6 символов";
    if (password !== confirm) e.confirm = "Пароли не совпадают";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleRegister = async () => {
    if (!validate()) return;
    setIsLoading(true);
    try {
      await signUp(fullName.trim(), email.trim().toLowerCase(), password, phone.trim() || undefined);
    } catch (err: any) {
      Alert.alert("Ошибка регистрации", err.message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : undefined}
      style={{ flex: 1, backgroundColor: colors.background }}
    >
      <ScrollView
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled"
      >
        <View style={styles.topBar}>
          <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backBtn}>
            <Text style={styles.backText}>← Войти</Text>
          </TouchableOpacity>
        </View>

        <Text style={[typography.h1, { marginBottom: 4 }]}>Регистрация</Text>
        <Text style={[typography.bodyMuted, { marginBottom: spacing.xl }]}>
          Создайте аккаунт, чтобы бронировать туры
        </Text>

        <InputField label="Полное имя" value={fullName} onChangeText={setFullName}
          placeholder="Иван Иванов" error={errors.fullName} />
        <InputField label="Email" value={email} onChangeText={setEmail}
          keyboardType="email-address" placeholder="name@example.com" error={errors.email} />
        <InputField label="Телефон (необязательно)" value={phone} onChangeText={setPhone}
          keyboardType="phone-pad" placeholder="+7 (999) 000-00-00" />
        <InputField label="Пароль" value={password} onChangeText={setPassword}
          secureTextEntry placeholder="Минимум 6 символов" error={errors.password} />
        <InputField label="Подтверждение пароля" value={confirm} onChangeText={setConfirm}
          secureTextEntry placeholder="Повторите пароль" error={errors.confirm} />

        <Button
          title="Создать аккаунт"
          onPress={handleRegister}
          isLoading={isLoading}
          fullWidth
          style={{ marginTop: spacing.sm }}
        />
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flexGrow: 1, padding: spacing.lg, paddingTop: spacing.xxl },
  topBar: { marginBottom: spacing.lg },
  backBtn: { padding: 4, alignSelf: "flex-start" },
  backText: { color: colors.accent, fontSize: 15, fontWeight: "600" },
});
