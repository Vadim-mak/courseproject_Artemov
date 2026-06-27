import React from "react";
import {
  View,
  Text,
  TextInput,
  TextInputProps,
  StyleSheet,
} from "react-native";
import { colors, spacing, radius, typography } from "../theme/theme";

interface Props extends TextInputProps {
  label: string;
  error?: string;
}

export default function InputField({ label, error, style, ...rest }: Props) {
  return (
    <View style={styles.container}>
      <Text style={styles.label}>{label}</Text>
      <TextInput
        style={[
          styles.input,
          error ? styles.inputError : undefined,
          style as any,
        ]}
        placeholderTextColor={colors.textMuted}
        autoCapitalize="none"
        {...rest}
      />
      {error ? <Text style={styles.errorText}>{error}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginBottom: spacing.md,
  },
  label: {
    ...typography.caption,
    marginBottom: 6,
    color: colors.text,
    fontWeight: "600",
    letterSpacing: 0.3,
    textTransform: "uppercase",
  },
  input: {
    backgroundColor: colors.surface,
    borderWidth: 1.5,
    borderColor: colors.border,
    borderRadius: radius.md,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm + 3,
    fontSize: 15,
    color: colors.text,
  },
  inputError: {
    borderColor: colors.danger,
  },
  errorText: {
    marginTop: 4,
    fontSize: 12,
    color: colors.danger,
  },
});
