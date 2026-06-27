import React, { useCallback, useEffect, useState } from "react";
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  RefreshControl,
  TouchableOpacity,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { Ionicons } from "@expo/vector-icons";
import { useAuth } from "../../context/AuthContext";
import * as api from "../../api/ApiFunctions";
import type { Tour, Country } from "../../types";
import TourCard from "../../components/TourCard";
import EmptyState from "../../components/EmptyState";
import { colors, spacing, typography, radius, shadow } from "../../theme/theme";
import type { ToursStackParams } from "../../navigation/AppNavigator";

type Props = {
  navigation: NativeStackNavigationProp<ToursStackParams, "Home">;
};

export default function HomeScreen({ navigation }: Props) {
  const { user } = useAuth();
  const [featured, setFeatured] = useState<Tour[]>([]);
  const [countries, setCountries] = useState<Country[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(async () => {
    try {
      const [toursPage, countriesData] = await Promise.all([
        api.getAllTours(0, 5),
        api.getAllCountries(),
      ]);
      setFeatured(toursPage.content);
      setCountries(countriesData);
    } finally {
      setIsLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const onRefresh = () => { setRefreshing(true); load(); };

  const greeting = user
    ? `Привет, ${user.fullName.split(" ")[0]}! 👋`
    : "Куда летим?";

  return (
    <SafeAreaView style={styles.safe} edges={["top"]}>
      <ScrollView
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={colors.primary} />}
      >
        {/* Header */}
        <View style={styles.header}>
          <View>
            <Text style={styles.greeting}>{greeting}</Text>
            <Text style={styles.sub}>Откройте новые направления</Text>
          </View>
          <TouchableOpacity
            style={styles.searchBtn}
            onPress={() => navigation.navigate("TourList", {})}
          >
            <Ionicons name="search" size={20} color="#fff" />
          </TouchableOpacity>
        </View>

        {/* Directions */}
        <Text style={[typography.h3, styles.sectionTitle]}>Направления</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.dirRow}>
          {countries.map((c) => (
            <TouchableOpacity
              key={c.id}
              style={[styles.countryChip, shadow.card]}
              onPress={() => navigation.navigate("TourList", { countryId: c.id })}
            >
              <Text style={styles.countryFlag}>🌍</Text>
              <Text style={styles.countryName}>{c.name}</Text>
            </TouchableOpacity>
          ))}
        </ScrollView>

        {/* Featured */}
        <View style={styles.sectionHeader}>
          <Text style={typography.h3}>Популярные туры</Text>
          <TouchableOpacity onPress={() => navigation.navigate("TourList", {})}>
            <Text style={styles.seeAll}>Все туры →</Text>
          </TouchableOpacity>
        </View>

        {isLoading ? null : featured.length === 0 ? (
          <EmptyState icon="compass-outline" title="Туры не найдены" />
        ) : (
          <View style={styles.list}>
            {featured.map((t) => (
              <TourCard
                key={t.id}
                tour={t}
                onPress={() => navigation.navigate("TourDetail", { tourId: t.id })}
              />
            ))}
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: colors.background },
  header: {
    backgroundColor: colors.primary,
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.lg,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  greeting: { color: "#fff", fontSize: 22, fontWeight: "700" },
  sub: { color: "rgba(255,255,255,0.72)", fontSize: 13, marginTop: 2 },
  searchBtn: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: colors.accent,
    alignItems: "center",
    justifyContent: "center",
  },
  sectionTitle: { marginHorizontal: spacing.lg, marginTop: spacing.lg, marginBottom: spacing.sm },
  dirRow: { paddingHorizontal: spacing.lg, gap: 10, paddingBottom: spacing.sm },
  countryChip: {
    backgroundColor: colors.surface,
    borderRadius: radius.md,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    alignItems: "center",
    minWidth: 80,
  },
  countryFlag: { fontSize: 22 },
  countryName: { fontSize: 12, fontWeight: "600", color: colors.text, marginTop: 4, textAlign: "center" },
  sectionHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginHorizontal: spacing.lg,
    marginTop: spacing.lg,
    marginBottom: spacing.sm,
  },
  seeAll: { color: colors.accent, fontWeight: "600", fontSize: 13 },
  list: { paddingHorizontal: spacing.lg },
});
