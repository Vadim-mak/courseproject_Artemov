import React, { useCallback, useEffect, useState } from "react";
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  Alert,
  TouchableOpacity,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import * as api from "../../api/ApiFunctions";
import type { Booking } from "../../types";
import EmptyState from "../../components/EmptyState";
import { colors, spacing, typography, radius, shadow } from "../../theme/theme";
import { format } from "date-fns";
import { ru } from "date-fns/locale";

const STATUS_LABELS: Record<string, { label: string; color: string }> = {
  PENDING:    { label: "Ожидает",    color: colors.warning },
  CONFIRMED:  { label: "Подтверждено", color: colors.success },
  CANCELLED:  { label: "Отменено",   color: colors.danger },
};

export default function MyBookingsScreen() {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const load = useCallback(async () => {
    setIsLoading(true);
    try {
      const data = await api.getMyBookings();
      setBookings(data);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleCancel = (id: number) => {
    Alert.alert(
      "Отмена бронирования",
      "Вы уверены, что хотите отменить эту поездку?",
      [
        { text: "Нет", style: "cancel" },
        {
          text: "Да, отменить",
          style: "destructive",
          onPress: async () => {
            try {
              await api.cancelBooking(id);
              load();
            } catch (err: any) {
              Alert.alert("Ошибка", err.message);
            }
          },
        },
      ]
    );
  };

  if (isLoading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  return (
    <FlatList
      data={bookings}
      keyExtractor={(b) => String(b.id)}
      contentContainerStyle={styles.list}
      renderItem={({ item }) => {
        const status = STATUS_LABELS[item.status] ?? { label: item.status, color: colors.textMuted };
        const date = format(new Date(item.bookingDate), "d MMM yyyy", { locale: ru });
        return (
          <View style={[styles.card, shadow.card]}>
            <View style={styles.cardHeader}>
              <Text style={[typography.h3, { flex: 1 }]} numberOfLines={1}>{item.tour.title}</Text>
              <View style={[styles.statusBadge, { backgroundColor: status.color + "22" }]}>
                <Text style={[styles.statusText, { color: status.color }]}>{status.label}</Text>
              </View>
            </View>

            <Text style={[typography.bodyMuted, { marginTop: 2 }]}>{item.tour.country.name}</Text>

            <View style={styles.infoRow}>
              <Ionicons name="people-outline" size={14} color={colors.textMuted} />
              <Text style={styles.infoText}>{item.numberOfPeople} чел.</Text>
              <Ionicons name="calendar-outline" size={14} color={colors.textMuted} style={{ marginLeft: 12 }} />
              <Text style={styles.infoText}>{date}</Text>
            </View>

            <View style={styles.footer}>
              <Text style={typography.price}>{Number(item.totalPrice).toLocaleString("ru-RU")} ₽</Text>
              {item.status === "PENDING" || item.status === "CONFIRMED" ? (
                <TouchableOpacity onPress={() => handleCancel(item.id)} style={styles.cancelBtn}>
                  <Text style={styles.cancelText}>Отменить</Text>
                </TouchableOpacity>
              ) : null}
            </View>

            <Text style={[typography.caption, { marginTop: spacing.sm, color: colors.textMuted }]}>
              #{item.confirmationCode.substring(0, 8).toUpperCase()}
            </Text>
          </View>
        );
      }}
      ListEmptyComponent={
        <EmptyState icon="calendar-outline" title="Нет поездок"
          subtitle="Забронируйте тур в каталоге" />
      }
    />
  );
}

const styles = StyleSheet.create({
  center: { flex: 1, justifyContent: "center", alignItems: "center" },
  list: { padding: spacing.md, flexGrow: 1 },
  card: {
    backgroundColor: colors.surface,
    borderRadius: radius.lg,
    padding: spacing.md,
    marginBottom: spacing.md,
  },
  cardHeader: { flexDirection: "row", alignItems: "center", gap: spacing.sm },
  statusBadge: {
    paddingHorizontal: spacing.sm,
    paddingVertical: 3,
    borderRadius: radius.pill,
  },
  statusText: { fontSize: 11, fontWeight: "700" },
  infoRow: { flexDirection: "row", alignItems: "center", marginTop: spacing.sm },
  infoText: { fontSize: 13, color: colors.textMuted, marginLeft: 4 },
  footer: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginTop: spacing.sm },
  cancelBtn: {
    backgroundColor: colors.danger + "18",
    paddingHorizontal: spacing.md,
    paddingVertical: 6,
    borderRadius: radius.pill,
  },
  cancelText: { color: colors.danger, fontSize: 13, fontWeight: "600" },
});
