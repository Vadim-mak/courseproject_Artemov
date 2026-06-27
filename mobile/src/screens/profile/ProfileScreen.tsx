import React, { useState } from "react";
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useAuth } from "../../context/AuthContext";
import * as api from "../../api/ApiFunctions";
import InputField from "../../components/InputField";
import Button from "../../components/Button";
import { colors, spacing, typography, radius, shadow } from "../../theme/theme";
import { format } from "date-fns";
import { ru } from "date-fns/locale";

export default function ProfileScreen() {
  const { user, signOut, refreshProfile } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [fullName, setFullName] = useState(user?.fullName ?? "");
  const [phone, setPhone] = useState(user?.phone ?? "");
  const [isSaving, setIsSaving] = useState(false);
  const [isSigningOut, setIsSigningOut] = useState(false);

  if (!user) return null;

  const regDate = format(new Date(user.registeredAt), "d MMMM yyyy", { locale: ru });
  const isAdmin = user.roles.includes("ROLE_ADMIN");

  const handleSave = async () => {
    if (!fullName.trim()) {
      Alert.alert("Ошибка", "Имя не может быть пустым");
      return;
    }
    setIsSaving(true);
    try {
      await api.updateMyProfile(fullName.trim(), phone.trim() || undefined);
      await refreshProfile();
      setIsEditing(false);
    } catch (err: any) {
      Alert.alert("Ошибка", err.message);
    } finally {
      setIsSaving(false);
    }
  };

  const handleSignOut = () => {
    Alert.alert("Выход", "Вы уверены, что хотите выйти?", [
      { text: "Отмена", style: "cancel" },
      {
        text: "Выйти",
        style: "destructive",
        onPress: async () => {
          setIsSigningOut(true);
          await signOut();
        },
      },
    ]);
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: colors.background }} edges={["bottom"]}>
      <ScrollView contentContainerStyle={styles.container}>
        {/* Avatar */}
        <View style={styles.avatarWrapper}>
          <View style={styles.avatar}>
            <Text style={styles.avatarText}>
              {user.fullName.charAt(0).toUpperCase()}
            </Text>
          </View>
          {isAdmin && (
            <View style={styles.adminBadge}>
              <Ionicons name="shield-checkmark" size={12} color="#fff" />
              <Text style={styles.adminText}>Администратор</Text>
            </View>
          )}
        </View>

        {/* Info card */}
        <View style={[styles.card, shadow.card]}>
          {isEditing ? (
            <>
              <InputField label="Имя" value={fullName} onChangeText={setFullName} />
              <InputField label="Телефон" value={phone} onChangeText={setPhone} keyboardType="phone-pad" />
              <View style={styles.btnRow}>
                <Button title="Сохранить" onPress={handleSave} isLoading={isSaving} />
                <Button title="Отмена" onPress={() => setIsEditing(false)} variant="ghost" />
              </View>
            </>
          ) : (
            <>
              <InfoRow icon="person-outline" label="Имя" value={user.fullName} />
              <InfoRow icon="mail-outline" label="Email" value={user.email} />
              {user.phone ? <InfoRow icon="call-outline" label="Телефон" value={user.phone} /> : null}
              <InfoRow icon="calendar-outline" label="Дата регистрации" value={regDate} />
              <Button title="Редактировать профиль" onPress={() => setIsEditing(true)} variant="ghost" style={{ marginTop: spacing.md }} />
            </>
          )}
        </View>

        {/* Sign out */}
        <Button
          title="Выйти из аккаунта"
          onPress={handleSignOut}
          isLoading={isSigningOut}
          variant="danger"
          fullWidth
          style={{ marginTop: spacing.md }}
        />
      </ScrollView>
    </SafeAreaView>
  );
}

function InfoRow({ icon, label, value }: { icon: keyof typeof Ionicons.glyphMap; label: string; value: string }) {
  return (
    <View style={styles.infoRow}>
      <Ionicons name={icon} size={18} color={colors.primary} />
      <View style={{ marginLeft: spacing.sm, flex: 1 }}>
        <Text style={typography.caption}>{label.toUpperCase()}</Text>
        <Text style={[typography.body, { marginTop: 2 }]}>{value}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { padding: spacing.lg, gap: spacing.md },
  avatarWrapper: { alignItems: "center", marginBottom: spacing.sm },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: colors.primary,
    alignItems: "center",
    justifyContent: "center",
  },
  avatarText: { color: "#fff", fontSize: 32, fontWeight: "700" },
  adminBadge: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
    backgroundColor: colors.accent,
    paddingHorizontal: spacing.sm,
    paddingVertical: 3,
    borderRadius: radius.pill,
    marginTop: 8,
  },
  adminText: { color: "#fff", fontSize: 11, fontWeight: "700" },
  card: {
    backgroundColor: colors.surface,
    borderRadius: radius.lg,
    padding: spacing.lg,
  },
  infoRow: { flexDirection: "row", alignItems: "center", marginBottom: spacing.md },
  btnRow: { flexDirection: "row", gap: spacing.sm, marginTop: spacing.sm },
});
