import React from "react";
import { View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "../theme/theme";

interface Props {
  rating: number;
  maxStars?: number;
  size?: number;
}

export default function StarRating({ rating, maxStars = 5, size = 16 }: Props) {
  return (
    <View style={{ flexDirection: "row", gap: 2 }}>
      {Array.from({ length: maxStars }).map((_, i) => (
        <Ionicons
          key={i}
          name={i < Math.round(rating) ? "star" : "star-outline"}
          size={size}
          color={colors.warning}
        />
      ))}
    </View>
  );
}
