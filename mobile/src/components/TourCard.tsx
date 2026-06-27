import React from "react";
import {
  View,
  Text,
  Image,
  TouchableOpacity,
  StyleSheet,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors, spacing, radius, typography, shadow } from "../theme/theme";
import type { Tour } from "../types";
import { format } from "date-fns";
import { ru } from "date-fns/locale";

interface Props {
  tour: Tour;
  onPress: () => void;
}

/**
 * Карточка тура — основной визуальный элемент каталога.
 * Переиспользуется в HomeScreen и TourListScreen.
 */
export default function TourCard({ tour, onPress }: Props) {
  const startDate = format(new Date(tour.startDate), "d MMM yyyy", { locale: ru });

  return (
    <TouchableOpacity onPress={onPress} activeOpacity={0.88} style={[styles.card, shadow.card]}>
      <Image
        source={{ uri: tour.imageUrl ?? "https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=600" }}
        style={styles.image}
        resizeMode="cover"
      />

      {/* Страна-метка */}
      <View style={styles.badge}>
        <Text style={styles.badgeText}>{tour.country.name}</Text>
      </View>

      <View style={styles.body}>
        <Text style={typography.h3} numberOfLines={2}>{tour.title}</Text>

        <View style={styles.row}>
          <Ionicons name="calendar-outline" size={13} color={colors.textMuted} />
          <Text style={[typography.bodyMuted, { marginLeft: 4 }]}>{startDate}</Text>
          <Text style={[typography.bodyMuted, { marginLeft: 8 }]}>·</Text>
          <Ionicons name="time-outline" size={13} color={colors.textMuted} style={{ marginLeft: 8 }} />
          <Text style={[typography.bodyMuted, { marginLeft: 4 }]}>{tour.durationDays} дн.</Text>
        </View>

        {tour.averageRating !== null && (
          <View style={styles.row}>
            <Ionicons name="star" size={13} color={colors.warning} />
            <Text style={[typography.caption, { marginLeft: 4 }]}>
              {tour.averageRating.toFixed(1)} ({tour.reviewsCount})
            </Text>
          </View>
        )}

        <View style={styles.footer}>
          <View>
            <Text style={styles.priceLabel}>от</Text>
            <Text style={typography.price}>
              {Number(tour.price).toLocaleString("ru-RU")} ₽
            </Text>
          </View>
          <View style={styles.placesChip}>
            <Ionicons name="people-outline" size={13} color={colors.primary} />
            <Text style={styles.placesText}>{tour.availablePlaces} мест</Text>
          </View>
        </View>
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: radius.lg,
    overflow: "hidden",
    marginBottom: spacing.md,
  },
  image: {
    width: "100%",
    height: 190,
  },
  badge: {
    position: "absolute",
    top: spacing.sm,
    left: spacing.sm,
    backgroundColor: colors.primary + "CC",
    paddingHorizontal: spacing.sm,
    paddingVertical: 3,
    borderRadius: radius.pill,
  },
  badgeText: {
    color: "#fff",
    fontSize: 11,
    fontWeight: "600",
  },
  body: {
    padding: spacing.md,
    gap: 6,
  },
  row: {
    flexDirection: "row",
    alignItems: "center",
  },
  footer: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "flex-end",
    marginTop: 4,
  },
  priceLabel: {
    fontSize: 11,
    color: colors.textMuted,
  },
  placesChip: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
    backgroundColor: colors.sand,
    paddingHorizontal: spacing.sm,
    paddingVertical: 4,
    borderRadius: radius.pill,
  },
  placesText: {
    fontSize: 12,
    fontWeight: "600",
    color: colors.primary,
  },
});
