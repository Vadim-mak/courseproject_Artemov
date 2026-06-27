import React, { useCallback, useEffect, useState } from "react";
import {
  View,
  Text,
  ScrollView,
  Image,
  StyleSheet,
  ActivityIndicator,
  Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { RouteProp } from "@react-navigation/native";
import { Ionicons } from "@expo/vector-icons";
import { format } from "date-fns";
import { ru } from "date-fns/locale";
import * as api from "../../api/ApiFunctions";
import type { Tour, Review } from "../../types";
import Button from "../../components/Button";
import StarRating from "../../components/StarRating";
import { colors, spacing, typography, radius, shadow } from "../../theme/theme";
import type { ToursStackParams } from "../../navigation/AppNavigator";

type Props = {
  navigation: NativeStackNavigationProp<ToursStackParams, "TourDetail">;
  route: RouteProp<ToursStackParams, "TourDetail">;
};

/**
 * Экран детальной информации о туре — аналог RoomDetails из
 * оригинального веб-проекта. Включает описание, галерею, отзывы
 * и кнопку бронирования.
 */
export default function TourDetailScreen({ navigation, route }: Props) {
  const { tourId } = route.params;
  const [tour, setTour] = useState<Tour | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const load = useCallback(async () => {
    try {
      const [tourData, reviewsData] = await Promise.all([
        api.getTourById(tourId),
        api.getReviewsByTour(tourId),
      ]);
      setTour(tourData);
      setReviews(reviewsData);
    } catch (err: any) {
      Alert.alert("Ошибка", err.message);
    } finally {
      setIsLoading(false);
    }
  }, [tourId]);

  useEffect(() => { load(); }, [load]);

  if (isLoading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  if (!tour) return null;

  const startDate = format(new Date(tour.startDate), "d MMM yyyy", { locale: ru });
  const endDate = format(new Date(tour.endDate), "d MMM yyyy", { locale: ru });

  return (
    <SafeAreaView style={styles.safe} edges={["bottom"]}>
      <ScrollView>
        <Image
          source={{ uri: tour.imageUrl ?? "https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=800" }}
          style={styles.hero}
        />

        <View style={styles.body}>
          {/* Country badge */}
          <View style={styles.badge}>
            <Ionicons name="location-outline" size={13} color={colors.primary} />
            <Text style={styles.badgeText}>{tour.country.name}</Text>
          </View>

          <Text style={[typography.h1, { marginBottom: spacing.sm }]}>{tour.title}</Text>

          {/* Key stats */}
          <View style={styles.statsRow}>
            <StatBox icon="calendar-outline" label="Начало" value={startDate} />
            <StatBox icon="flag-outline" label="Конец" value={endDate} />
            <StatBox icon="time-outline" label="Дней" value={String(tour.durationDays)} />
            <StatBox icon="people-outline" label="Мест" value={String(tour.availablePlaces)} />
          </View>

          {/* Description */}
          {tour.description ? (
            <>
              <Text style={[typography.h3, styles.sectionLabel]}>О туре</Text>
              <Text style={[typography.body, { color: colors.textMuted }]}>{tour.description}</Text>
            </>
          ) : null}

          {/* Reviews */}
          <Text style={[typography.h3, styles.sectionLabel]}>
            Отзывы{reviews.length > 0 ? ` (${reviews.length})` : ""}
          </Text>
          {reviews.length === 0 ? (
            <Text style={typography.bodyMuted}>Отзывов пока нет. Будьте первым!</Text>
          ) : (
            reviews.slice(0, 3).map((r) => (
              <View key={r.id} style={[styles.reviewCard, shadow.card]}>
                <View style={styles.reviewHeader}>
                  <Text style={styles.reviewAuthor}>{r.userFullName}</Text>
                  <StarRating rating={r.rating} size={13} />
                </View>
                {r.comment ? <Text style={[typography.bodyMuted, { marginTop: 4 }]}>{r.comment}</Text> : null}
              </View>
            ))
          )}
        </View>
      </ScrollView>

      {/* Booking footer */}
      <View style={[styles.footer, shadow.card]}>
        <View>
          <Text style={[typography.caption, { color: colors.textMuted }]}>Стоимость за человека</Text>
          <Text style={typography.price}>{Number(tour.price).toLocaleString("ru-RU")} ₽</Text>
        </View>
        <Button
          title="Забронировать"
          onPress={() => navigation.navigate("Booking", { tourId: tour.id })}
          disabled={tour.availablePlaces === 0}
        />
      </View>
    </SafeAreaView>
  );
}

function StatBox({ icon, label, value }: { icon: keyof typeof Ionicons.glyphMap; label: string; value: string }) {
  return (
    <View style={styles.statBox}>
      <Ionicons name={icon} size={18} color={colors.primary} />
      <Text style={styles.statValue}>{value}</Text>
      <Text style={styles.statLabel}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: colors.background },
  center: { flex: 1, justifyContent: "center", alignItems: "center" },
  hero: { width: "100%", height: 260 },
  body: { padding: spacing.lg, paddingBottom: spacing.xxl },
  badge: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
    marginBottom: spacing.sm,
  },
  badgeText: { fontSize: 13, fontWeight: "600", color: colors.primary },
  statsRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: spacing.lg,
    marginTop: spacing.md,
  },
  statBox: {
    flex: 1,
    alignItems: "center",
    backgroundColor: colors.surface,
    borderRadius: radius.md,
    paddingVertical: spacing.sm,
    marginHorizontal: 3,
  },
  statValue: { fontWeight: "700", color: colors.text, fontSize: 13, marginTop: 4 },
  statLabel: { fontSize: 10, color: colors.textMuted, marginTop: 1 },
  sectionLabel: { marginTop: spacing.lg, marginBottom: spacing.sm },
  reviewCard: {
    backgroundColor: colors.surface,
    borderRadius: radius.md,
    padding: spacing.md,
    marginBottom: spacing.sm,
  },
  reviewHeader: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  reviewAuthor: { fontWeight: "600", color: colors.text, fontSize: 14 },
  footer: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: spacing.lg,
    backgroundColor: colors.surface,
    borderTopWidth: 1,
    borderTopColor: colors.border,
  },
});
