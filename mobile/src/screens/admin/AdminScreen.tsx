import React, { useCallback, useEffect, useState } from "react";
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  Alert,
  TouchableOpacity,
  ScrollView,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import * as api from "../../api/ApiFunctions";
import type { Booking, UserProfile } from "../../types";
import EmptyState from "../../components/EmptyState";
import { colors, spacing, typography, radius, shadow } from "../../theme/theme";

type Tab = "bookings" | "users";

const STATUS_COLORS: Record<string, string> = {
  PENDING: colors.warning,
  CONFIRMED: colors.success,
  CANCELLED: colors.danger,
};

/**
 * Панель администратора: список всех бронирований с возможностью
 * подтверждения/отмены, и список пользователей системы.
 * Отображается только для пользователей с ролью ROLE_ADMIN.
 */
export default function AdminScreen() {
  const [activeTab, setActiveTab] = useState<Tab>("bookings");
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [users, setUsers] = useState<UserProfile[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const load = useCallback(async () => {
    setIsLoading(true);
    try {
      const [b, u] = await Promise.all([api.getAllBookings(), api.getAllUsers()]);
      setBookings(b);
      setUsers(u);
    } catch (err: any) {
      Alert.alert("Ошибка", err.message);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleConfirm = async (id: number) => {
    try {
      await api.confirmBooking(id);
      load();
    } catch (err: any) { Alert.alert("Ошибка", err.message); }
  };

  const handleCancelAdmin = async (id: number) => {
    Alert.alert("Отмена бронирования", "Подтвердите отмену", [
      { text: "Нет", style: "cancel" },
      { text: "Да", style: "destructive", onPress: async () => {
        try { await api.cancelBooking(id); load(); }
        catch (err: any) { Alert.alert("Ошибка", err.message); }
      }},
    ]);
  };

  if (isLoading) {
    return <View style={styles.center}><ActivityIndicator size="large" color={colors.primary} /></View>;
  }

  return (
    <View style={styles.container}>
      {/* Stats header */}
      <View style={styles.statsRow}>
        <StatCard icon="calendar" label="Бронирований" value={bookings.length} />
        <StatCard icon="people" label="Пользователей" value={users.length} />
        <StatCard icon="checkmark-circle" label="Подтверждено"
          value={bookings.filter(b => b.status === "CONFIRMED").length} />
      </View>

      {/* Tabs */}
      <View style={styles.tabs}>
        {(["bookings", "users"] as Tab[]).map((tab) => (
          <TouchableOpacity
            key={tab}
            style={[styles.tab, activeTab === tab && styles.tabActive]}
            onPress={() => setActiveTab(tab)}
          >
            <Text style={[styles.tabText, activeTab === tab && styles.tabTextActive]}>
              {tab === "bookings" ? "Бронирования" : "Пользователи"}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {activeTab === "bookings" ? (
        <FlatList
          data={bookings}
          keyExtractor={(b) => String(b.id)}
          contentContainerStyle={styles.list}
          renderItem={({ item }) => (
            <View style={[styles.card, shadow.card]}>
              <View style={styles.row}>
                <Text style={[typography.h3, { flex: 1 }]} numberOfLines={1}>{item.tour.title}</Text>
                <View style={[styles.badge, { backgroundColor: STATUS_COLORS[item.status] + "22" }]}>
                  <Text style={[styles.badgeText, { color: STATUS_COLORS[item.status] }]}>
                    {item.status === "PENDING" ? "Ожидает" : item.status === "CONFIRMED" ? "OK" : "Отменено"}
                  </Text>
                </View>
              </View>
              <Text style={typography.bodyMuted}>{item.userFullName} · {item.numberOfPeople} чел.</Text>
              <Text style={[typography.price, { marginTop: 4 }]}>{Number(item.totalPrice).toLocaleString("ru-RU")} ₽</Text>
              {item.status === "PENDING" && (
                <View style={styles.actionsRow}>
                  <TouchableOpacity style={[styles.actionBtn, { backgroundColor: colors.success + "22" }]}
                    onPress={() => handleConfirm(item.id)}>
                    <Ionicons name="checkmark" size={16} color={colors.success} />
                    <Text style={[styles.actionText, { color: colors.success }]}>Подтвердить</Text>
                  </TouchableOpacity>
                  <TouchableOpacity style={[styles.actionBtn, { backgroundColor: colors.danger + "22" }]}
                    onPress={() => handleCancelAdmin(item.id)}>
                    <Ionicons name="close" size={16} color={colors.danger} />
                    <Text style={[styles.actionText, { color: colors.danger }]}>Отменить</Text>
                  </TouchableOpacity>
                </View>
              )}
            </View>
          )}
          ListEmptyComponent={<EmptyState title="Нет бронирований" />}
        />
      ) : (
        <FlatList
          data={users}
          keyExtractor={(u) => String(u.id)}
          contentContainerStyle={styles.list}
          renderItem={({ item }) => (
            <View style={[styles.card, shadow.card]}>
              <View style={styles.row}>
                <Ionicons name="person-circle-outline" size={32} color={colors.primary} />
                <View style={{ marginLeft: spacing.sm, flex: 1 }}>
                  <Text style={typography.h3}>{item.fullName}</Text>
                  <Text style={typography.bodyMuted}>{item.email}</Text>
                </View>
                {item.roles.includes("ROLE_ADMIN") && (
                  <Ionicons name="shield-checkmark" size={18} color={colors.accent} />
                )}
              </View>
              {!item.active && (
                <Text style={{ color: colors.danger, fontSize: 12, marginTop: 4 }}>⚠ Деактивирован</Text>
              )}
            </View>
          )}
          ListEmptyComponent={<EmptyState title="Нет пользователей" />}
        />
      )}
    </View>
  );
}

function StatCard({ icon, label, value }: { icon: keyof typeof Ionicons.glyphMap; label: string; value: number }) {
  return (
    <View style={[styles.statCard, shadow.card]}>
      <Ionicons name={icon} size={22} color={colors.primary} />
      <Text style={styles.statValue}>{value}</Text>
      <Text style={styles.statLabel}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  center: { flex: 1, alignItems: "center", justifyContent: "center" },
  statsRow: { flexDirection: "row", padding: spacing.md, gap: spacing.sm },
  statCard: {
    flex: 1,
    backgroundColor: colors.surface,
    borderRadius: radius.md,
    padding: spacing.md,
    alignItems: "center",
    gap: 4,
  },
  statValue: { fontSize: 22, fontWeight: "800", color: colors.text },
  statLabel: { fontSize: 11, color: colors.textMuted, textAlign: "center" },
  tabs: { flexDirection: "row", marginHorizontal: spacing.md, marginBottom: spacing.sm, borderRadius: radius.md, overflow: "hidden", borderWidth: 1, borderColor: colors.border },
  tab: { flex: 1, paddingVertical: spacing.sm, alignItems: "center", backgroundColor: colors.surface },
  tabActive: { backgroundColor: colors.primary },
  tabText: { fontWeight: "600", color: colors.textMuted },
  tabTextActive: { color: "#fff" },
  list: { padding: spacing.md, flexGrow: 1 },
  card: { backgroundColor: colors.surface, borderRadius: radius.lg, padding: spacing.md, marginBottom: spacing.sm },
  row: { flexDirection: "row", alignItems: "center", gap: spacing.sm },
  badge: { paddingHorizontal: 8, paddingVertical: 3, borderRadius: radius.pill },
  badgeText: { fontSize: 11, fontWeight: "700" },
  actionsRow: { flexDirection: "row", gap: spacing.sm, marginTop: spacing.sm },
  actionBtn: { flexDirection: "row", alignItems: "center", gap: 4, paddingHorizontal: spacing.sm, paddingVertical: 5, borderRadius: radius.pill },
  actionText: { fontSize: 13, fontWeight: "600" },
});
