/**
 * API-функции мобильного клиента (Presentation-слой PCMEF).
 * Полный аналог ApiFunctions.js из исходного проекта Hotel-Booking,
 * переработан под предметную область туристического агентства и
 * переведён с localStorage на AsyncStorage.
 */
import { apiClient, saveToken, clearToken } from "./client";
import type {
  AuthResult,
  Tour,
  Country,
  Booking,
  Review,
  UserProfile,
  PageResponse,
} from "../types";

// ─────────────────────────── Auth ───────────────────────────

export async function register(
  fullName: string,
  email: string,
  password: string,
  phone?: string
): Promise<AuthResult> {
  const { data } = await apiClient.post<AuthResult>("/auth/register", {
    fullName,
    email,
    password,
    phone,
  });
  await saveToken(data.token);
  return data;
}

export async function login(
  email: string,
  password: string
): Promise<AuthResult> {
  const { data } = await apiClient.post<AuthResult>("/auth/login", {
    email,
    password,
  });
  await saveToken(data.token);
  return data;
}

export async function logout(): Promise<void> {
  await clearToken();
}

// ─────────────────────────── Tours ───────────────────────────

export async function getAllTours(
  page = 0,
  size = 10,
  sort = "startDate,asc"
): Promise<PageResponse<Tour>> {
  const { data } = await apiClient.get<PageResponse<Tour>>("/tours", {
    params: { page, size, sort },
  });
  return data;
}

export async function searchTours(params: {
  keyword?: string;
  countryId?: number;
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  size?: number;
}): Promise<PageResponse<Tour>> {
  const { data } = await apiClient.get<PageResponse<Tour>>("/tours/search", {
    params: { page: 0, size: 10, sort: "price,asc", ...params },
  });
  return data;
}

export async function getTourById(id: number): Promise<Tour> {
  const { data } = await apiClient.get<Tour>(`/tours/${id}`);
  return data;
}

export async function createTour(payload: object): Promise<Tour> {
  const { data } = await apiClient.post<Tour>("/tours", payload);
  return data;
}

export async function updateTour(id: number, payload: object): Promise<Tour> {
  const { data } = await apiClient.put<Tour>(`/tours/${id}`, payload);
  return data;
}

export async function deleteTour(id: number): Promise<void> {
  await apiClient.delete(`/tours/${id}`);
}

// ─────────────────────────── Countries ───────────────────────────

export async function getAllCountries(): Promise<Country[]> {
  const { data } = await apiClient.get<Country[]>("/countries");
  return data;
}

// ─────────────────────────── Bookings ───────────────────────────

export async function bookTour(
  tourId: number,
  numberOfPeople: number
): Promise<Booking> {
  const { data } = await apiClient.post<Booking>(`/bookings/tour/${tourId}`, {
    numberOfPeople,
  });
  return data;
}

export async function getMyBookings(): Promise<Booking[]> {
  const { data } = await apiClient.get<Booking[]>("/bookings/my");
  return data;
}

export async function getBookingById(id: number): Promise<Booking> {
  const { data } = await apiClient.get<Booking>(`/bookings/${id}`);
  return data;
}

export async function cancelBooking(id: number): Promise<void> {
  await apiClient.delete(`/bookings/${id}`);
}

export async function confirmBooking(id: number): Promise<Booking> {
  const { data } = await apiClient.put<Booking>(`/bookings/${id}/confirm`);
  return data;
}

export async function getAllBookings(): Promise<Booking[]> {
  const { data } = await apiClient.get<Booking[]>("/bookings");
  return data;
}

// ─────────────────────────── Reviews ───────────────────────────

export async function getReviewsByTour(tourId: number): Promise<Review[]> {
  const { data } = await apiClient.get<Review[]>(`/reviews/tour/${tourId}`);
  return data;
}

export async function createReview(
  tourId: number,
  rating: number,
  comment: string
): Promise<Review> {
  const { data } = await apiClient.post<Review>(`/tours/${tourId}/reviews`, {
    rating,
    comment,
  });
  return data;
}

export async function deleteReview(id: number): Promise<void> {
  await apiClient.delete(`/reviews/${id}`);
}

// ─────────────────────────── Users ───────────────────────────

export async function getMyProfile(): Promise<UserProfile> {
  const { data } = await apiClient.get<UserProfile>("/users/me");
  return data;
}

export async function updateMyProfile(
  fullName: string,
  phone?: string
): Promise<UserProfile> {
  const { data } = await apiClient.put<UserProfile>("/users/me", {
    fullName,
    phone,
  });
  return data;
}

export async function getAllUsers(): Promise<UserProfile[]> {
  const { data } = await apiClient.get<UserProfile[]>("/users");
  return data;
}

export async function deactivateUser(id: number): Promise<void> {
  await apiClient.delete(`/users/${id}`);
}
