import React from "react";
import {
  TouchableOpacity,
  Text,
  ActivityIndicator,
  StyleSheet,
  ViewStyle,
} from "react-native";
import { colors, radius, spacing, typography } from "../theme/theme";

interface Props {
  title: string;
  onPress: () => void;
  variant?: "primary" | "secondary" | "danger" | "ghost";
  isLoading?: boolean;
  disabled?: boolean;
  style?: ViewStyle;
  fullWidth?: boolean;
}

export default function Button({
  title,
  onPress,
  variant = "primary",
  isLoading = false,
  disabled = false,
  style,
  fullWidth = false,
}: Props) {
  const bgColor = {
    primary: colors.primary,
    secondary: colors.sand,
    danger: colors.danger,
    ghost: "transparent",
  }[variant];

  const textColor = {
    primary: "#fff",
    secondary: colors.primary,
    danger: "#fff",
    ghost: colors.primary,
  }[variant];

  const borderColor = variant === "ghost" ? colors.primary : "transparent";

  return (
    <TouchableOpacity
      onPress={onPress}
      disabled={disabled || isLoading}
      activeOpacity={0.82}
      style={[
        styles.base,
        { backgroundColor: bgColor, borderColor, borderWidth: variant === "ghost" ? 1.5 : 0 },
        fullWidth && styles.fullWidth,
        (disabled || isLoading) && styles.disabled,
        style,
      ]}
    >
      {isLoading ? (
        <ActivityIndicator size="small" color={textColor} />
      ) : (
        <Text style={[typography.button, { color: textColor }]}>{title}</Text>
      )}
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  base: {
    paddingVertical: spacing.sm + 4,
    paddingHorizontal: spacing.lg,
    borderRadius: radius.pill,
    alignItems: "center",
    justifyContent: "center",
    alignSelf: "flex-start",
  },
  fullWidth: {
    alignSelf: "stretch",
  },
  disabled: {
    opacity: 0.52,
  },
});
