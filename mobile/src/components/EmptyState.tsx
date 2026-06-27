import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors, spacing, typography } from "../theme/theme";

interface Props {
  icon?: keyof typeof Ionicons.glyphMap;
  title: string;
  subtitle?: string;
}

export default function EmptyState({
  icon = "search-outline",
  title,
  subtitle,
}: Props) {
  return (
    <View style={styles.container}>
      <Ionicons name={icon} size={56} color={colors.border} />
      <Text style={[typography.h3, { marginTop: spacing.md, textAlign: "center", color: colors.textMuted }]}>
        {title}
      </Text>
      {subtitle ? (
        <Text style={[typography.bodyMuted, { marginTop: 6, textAlign: "center" }]}>
          {subtitle}
        </Text>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: spacing.xxl,
  },
});
