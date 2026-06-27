import React from "react";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "../theme/theme";
import { useAuth } from "../context/AuthContext";

import HomeScreen from "../screens/tours/HomeScreen";
import TourListScreen from "../screens/tours/TourListScreen";
import TourDetailScreen from "../screens/tours/TourDetailScreen";
import BookingScreen from "../screens/booking/BookingScreen";
import MyBookingsScreen from "../screens/booking/MyBookingsScreen";
import ProfileScreen from "../screens/profile/ProfileScreen";
import AdminScreen from "../screens/admin/AdminScreen";

// ─── Stack types ───────────────────────────────────────────
export type ToursStackParams = {
  Home: undefined;
  TourList: { countryId?: number; keyword?: string };
  TourDetail: { tourId: number };
  Booking: { tourId: number };
};

export type BookingsStackParams = {
  MyBookings: undefined;
};

export type ProfileStackParams = {
  Profile: undefined;
};

export type AdminStackParams = {
  AdminDashboard: undefined;
};

// ─── Stack Navigators ──────────────────────────────────────
const ToursStack = createNativeStackNavigator<ToursStackParams>();
function ToursNavigator() {
  return (
    <ToursStack.Navigator
      screenOptions={{
        headerStyle: { backgroundColor: colors.primary },
        headerTintColor: "#fff",
        headerBackTitle: "Назад",
      }}
    >
      <ToursStack.Screen name="Home" component={HomeScreen} options={{ title: "Туры" }} />
      <ToursStack.Screen name="TourList" component={TourListScreen} options={{ title: "Каталог туров" }} />
      <ToursStack.Screen name="TourDetail" component={TourDetailScreen} options={{ title: "О туре" }} />
      <ToursStack.Screen name="Booking" component={BookingScreen} options={{ title: "Бронирование" }} />
    </ToursStack.Navigator>
  );
}

const BookingsStack = createNativeStackNavigator<BookingsStackParams>();
function BookingsNavigator() {
  return (
    <BookingsStack.Navigator
      screenOptions={{
        headerStyle: { backgroundColor: colors.primary },
        headerTintColor: "#fff",
      }}
    >
      <BookingsStack.Screen name="MyBookings" component={MyBookingsScreen} options={{ title: "Мои поездки" }} />
    </BookingsStack.Navigator>
  );
}

const ProfileStack = createNativeStackNavigator<ProfileStackParams>();
function ProfileNavigator() {
  return (
    <ProfileStack.Navigator
      screenOptions={{
        headerStyle: { backgroundColor: colors.primary },
        headerTintColor: "#fff",
      }}
    >
      <ProfileStack.Screen name="Profile" component={ProfileScreen} options={{ title: "Профиль" }} />
    </ProfileStack.Navigator>
  );
}

const AdminStack = createNativeStackNavigator<AdminStackParams>();
function AdminNavigator() {
  return (
    <AdminStack.Navigator
      screenOptions={{
        headerStyle: { backgroundColor: colors.primary },
        headerTintColor: "#fff",
      }}
    >
      <AdminStack.Screen name="AdminDashboard" component={AdminScreen} options={{ title: "Панель администратора" }} />
    </AdminStack.Navigator>
  );
}

// ─── Bottom Tabs ───────────────────────────────────────────
const Tab = createBottomTabNavigator();

export default function AppNavigator() {
  const { isAdmin } = useAuth();

  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarActiveTintColor: colors.accent,
        tabBarInactiveTintColor: colors.textMuted,
        tabBarStyle: {
          backgroundColor: colors.surface,
          borderTopColor: colors.border,
          paddingBottom: 4,
          height: 58,
        },
        tabBarIcon: ({ color, size }) => {
          const icons: Record<string, keyof typeof Ionicons.glyphMap> = {
            Tours: "compass-outline",
            Bookings: "calendar-outline",
            ProfileTab: "person-outline",
            Admin: "settings-outline",
          };
          return <Ionicons name={icons[route.name] ?? "ellipse"} size={size} color={color} />;
        },
      })}
    >
      <Tab.Screen name="Tours" component={ToursNavigator} options={{ title: "Туры" }} />
      <Tab.Screen name="Bookings" component={BookingsNavigator} options={{ title: "Поездки" }} />
      <Tab.Screen name="ProfileTab" component={ProfileNavigator} options={{ title: "Профиль" }} />
      {isAdmin && (
        <Tab.Screen name="Admin" component={AdminNavigator} options={{ title: "Админ" }} />
      )}
    </Tab.Navigator>
  );
}
