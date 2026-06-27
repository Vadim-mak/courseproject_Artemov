import React, { useCallback, useEffect, useState } from "react";
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TextInput,
  ActivityIndicator,
  TouchableOpacity,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { RouteProp } from "@react-navigation/native";
import * as api from "../../api/ApiFunctions";
import type { Tour } from "../../types";
import TourCard from "../../components/TourCard";
import EmptyState from "../../components/EmptyState";
import { colors, spacing, radius } from "../../theme/theme";
import type { ToursStackParams } from "../../navigation/AppNavigator";

type Props = {
  navigation: NativeStackNavigationProp<ToursStackParams, "TourList">;
  route: RouteProp<ToursStackParams, "TourList">;
};

export default function TourListScreen({ navigation, route }: Props) {
  const { countryId, keyword: initialKeyword } = route.params ?? {};

  const [tours, setTours] = useState<Tour[]>([]);
  const [keyword, setKeyword] = useState(initialKeyword ?? "");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingMore, setIsLoadingMore] = useState(false);

  const fetchTours = useCallback(async (currentPage: number, reset = false) => {
    if (currentPage === 0) setIsLoading(true);
    else setIsLoadingMore(true);
    try {
      const result = await api.searchTours({
        keyword: keyword || undefined,
        countryId,
        page: currentPage,
        size: 10,
      });
      setTours((prev) => reset ? result.content : [...prev, ...result.content]);
      setTotalPages(result.totalPages);
    } finally {
      setIsLoading(false);
      setIsLoadingMore(false);
    }
  }, [keyword, countryId]);

  useEffect(() => {
    setPage(0);
    fetchTours(0, true);
  }, [fetchTours]);

  const loadMore = () => {
    if (!isLoadingMore && page + 1 < totalPages) {
      const next = page + 1;
      setPage(next);
      fetchTours(next);
    }
  };

  return (
    <View style={styles.container}>
      {/* Search bar */}
      <View style={styles.searchBar}>
        <Ionicons name="search-outline" size={18} color={colors.textMuted} />
        <TextInput
          style={styles.searchInput}
          placeholder="Поиск туров..."
          placeholderTextColor={colors.textMuted}
          value={keyword}
          onChangeText={setKeyword}
          returnKeyType="search"
        />
        {keyword.length > 0 && (
          <TouchableOpacity onPress={() => setKeyword("")}>
            <Ionicons name="close-circle" size={18} color={colors.textMuted} />
          </TouchableOpacity>
        )}
      </View>

      {isLoading ? (
        <ActivityIndicator size="large" color={colors.primary} style={{ marginTop: 40 }} />
      ) : (
        <FlatList
          data={tours}
          keyExtractor={(t) => String(t.id)}
          contentContainerStyle={styles.list}
          renderItem={({ item }) => (
            <TourCard
              tour={item}
              onPress={() => navigation.navigate("TourDetail", { tourId: item.id })}
            />
          )}
          ListEmptyComponent={
            <EmptyState icon="compass-outline" title="Туры не найдены"
              subtitle="Попробуйте изменить параметры поиска" />
          }
          onEndReached={loadMore}
          onEndReachedThreshold={0.4}
          ListFooterComponent={
            isLoadingMore ? <ActivityIndicator color={colors.primary} style={{ marginVertical: 16 }} /> : null
          }
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  searchBar: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    margin: spacing.md,
    backgroundColor: colors.surface,
    borderRadius: radius.pill,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm + 2,
    borderWidth: 1,
    borderColor: colors.border,
  },
  searchInput: { flex: 1, fontSize: 15, color: colors.text },
  list: { paddingHorizontal: spacing.md, paddingBottom: spacing.xl },
});
