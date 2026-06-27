/**
 * Единая система дизайн-токенов приложения. Палитра вдохновлена морскими
 * курортами и закатами — характерная для туристической тематики, без
 * шаблонного фиолетово-голубого градиента.
 */

export const colors = {
  // основной — глубокий "океанский" тил, используется в шапках/кнопках
  primary: "#0E3B43",
  primaryDark: "#082327",
  primaryLight: "#1B5A64",

  // акцент — закатный коралл, для CTA и активных состояний
  accent: "#FF6B4A",
  accentDark: "#E0512E",

  // вспомогательный — песочный, для карточек/подложек
  sand: "#F4E9D8",
  sandDark: "#E5D3B7",

  success: "#2E7D5B",
  warning: "#E8A33D",
  danger: "#D14343",

  background: "#F7F4EF",
  surface: "#FFFFFF",

  text: "#1B2B2E",
  textMuted: "#6B7B7E",
  textOnPrimary: "#FFFFFF",
  border: "#E4DFD4",
};

export const spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
  xxl: 48,
};

export const radius = {
  sm: 8,
  md: 14,
  lg: 22,
  pill: 999,
};

export const typography = {
  h1: { fontSize: 28, fontWeight: "700" as const, color: colors.text, letterSpacing: -0.3 },
  h2: { fontSize: 22, fontWeight: "700" as const, color: colors.text },
  h3: { fontSize: 18, fontWeight: "600" as const, color: colors.text },
  body: { fontSize: 15, fontWeight: "400" as const, color: colors.text, lineHeight: 21 },
  bodyMuted: { fontSize: 14, fontWeight: "400" as const, color: colors.textMuted, lineHeight: 20 },
  caption: { fontSize: 12, fontWeight: "600" as const, color: colors.textMuted, letterSpacing: 0.4 },
  price: { fontSize: 20, fontWeight: "700" as const, color: colors.accentDark },
  button: { fontSize: 15, fontWeight: "600" as const, color: colors.textOnPrimary },
};

export const shadow = {
  card: {
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.08,
    shadowRadius: 12,
    elevation: 3,
  },
};

const theme = { colors, spacing, radius, typography, shadow };
export default theme;
