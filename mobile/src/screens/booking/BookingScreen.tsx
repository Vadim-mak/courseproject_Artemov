import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  Alert,
  TouchableOpacity,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { RouteProp } from "@react-navigation/native";
import { Ionicons } from "@expo/vector-icons";
import * as api from "../../api/ApiFunctions";
import type { Tour } from "../../types";
import Button from "../../components/Button";
import { colors, spacing, typography, radius, shadow } from "../../theme/theme";
import type { ToursStackParams } from "../../navigation/AppNavigator";

type Props = {
  navigation: NativeStackNavigationProp<ToursStackParams, "Booking">;
  route: RouteProp<ToursStackParams, "Booking">;
};

/**
 * Экран бронирования тура. Аналог BookingForm из исходного проекта.
 * POST /api/bookings/tour/{tourId}
 */
export default function BookingScreen({ navigation, route }: Props) {
  const { tourId } = route.params;
  const [tour, setTour] = useState<Tour | null>(null);
  const [people, setPeople] = useState(1);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    api.getTourById(tourId).then(setTour);
  }, [tourId]);

  const totalPrice = tour ? Number(tour.price) * people : 0;

  const handleBook = async () => {
    if (!tour) return;
    if (people > tour.availablePlaces) {
      Alert.alert("Недостаточно мест", `Доступно только ${tour.availablePlaces} мест`);
      return;
    }
    setIsLoading(true);
    try {
      const booking = await api.bookTour(tourId, people);
      Alert.alert(
        "Бронирование создано! 🎉",
        `Код подтверждения:\n${booking.confirmationCode}`,
        [{ text: "OK", onPress: () => navigation.popToTop() }]
      );
    } catch (err: any) {
      Alert.alert("Ошибка", err.message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: colors.background }} edges={["bottom"]}>
      <ScrollView contentContainerStyle={styles.container}>
        {/* Tour summary */}
        {tour && (
          <View style={[styles.tourCard, shadow.card]}>
            <Text style={typography.h3}>{tour.title}</Text>
            <Text style={[typography.bodyMuted, { marginTop: 4 }]}>{tour.country.name}</Text>
            <Text style={[typography.price, { marginTop: spacing.sm }]}>
              {Number(tour.price).toLocaleString("ru-RU")} ₽ / чел.
            </Text>
          </View>
        )}

        {/* People counter */}
        <View style={styles.section}>
          <Text style={[typography.h3, { marginBottom: spacing.md }]}>Количество туристов</Text>
          <View style={styles.counter}>
            <TouchableOpacity
              style={[styles.counterBtn, people <= 1 && styles.counterBtnDisabled]}
              onPress={() => setPeople((p) => Math.max(1, p - 1))}
              disabled={people <= 1}
            >
              <Ionicons name="remove" size={22} color={people <= 1 ? colors.border : colors.primary} />
            </TouchableOpacity>
            <Text style={styles.counterValue}>{people}</Text>
            <TouchableOpacity
              style={[styles.counterBtn, people >= (tour?.availablePlaces ?? 1) && styles.counterBtnDisabled]}
              onPress={() => setPeople((p) => Math.min(tour?.availablePlaces ?? 99, p + 1))}
              disabled={people >= (tour?.availablePlaces ?? 99)}
            >
              <Ionicons name="add" size={22} color={colors.primary} />
            </TouchableOpacity>
          </View>
          {tour && (
            <Text style={[typography.caption, { textAlign: "center", marginTop: 6 }]}>
              Доступно мест: {tour.availablePlaces}
            </Text>
          )}
        </View>

        {/* Price breakdown */}
        <View style={[styles.section, styles.priceBox]}>
          <Text style={typography.h3}>Стоимость</Text>
          <View style={styles.priceRow}>
            <Text style={typography.body}>{people} × {Number(tour?.price ?? 0).toLocaleString("ru-RU")} ₽</Text>
            <Text style={typography.price}>{totalPrice.toLocaleString("ru-RU")} ₽</Text>
          </View>
        </View>

        <Button
          title="Подтвердить бронирование"
          onPress={handleBook}
          isLoading={isLoading}
          fullWidth
          style={{ marginTop: spacing.md }}
        />

        <Text style={[typography.bodyMuted, { textAlign: "center", marginTop: spacing.md, fontSize: 12 }]}>
          Код подтверждения придёт на экран после оформления.
          Отменить бронирование можно в разделе «Поездки».
        </Text>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { padding: spacing.lg, gap: spacing.md },
  tourCard: {
    backgroundColor: colors.surface,
    borderRadius: radius.lg,
    padding: spacing.lg,
  },
  section: {
    backgroundColor: colors.surface,
    borderRadius: radius.lg,
    padding: spacing.lg,
  },
  counter: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: spacing.lg,
  },
  counterBtn: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: colors.sand,
    alignItems: "center",
    justifyContent: "center",
  },
  counterBtnDisabled: { opacity: 0.4 },
  counterValue: { fontSize: 28, fontWeight: "700", color: colors.text, minWidth: 40, textAlign: "center" },
  priceBox: { borderWidth: 1, borderColor: colors.accent + "44" },
  priceRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginTop: spacing.sm },
});
