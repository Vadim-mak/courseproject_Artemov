export type RoleName = "ROLE_USER" | "ROLE_ADMIN";

export interface Country {
  id: number;
  name: string;
  description: string | null;
  imageUrl: string | null;
}

export interface Tour {
  id: number;
  title: string;
  description: string | null;
  price: number;
  durationDays: number;
  startDate: string; // ISO date
  endDate: string;   // ISO date
  availablePlaces: number;
  imageUrl: string | null;
  active: boolean;
  country: Country;
  averageRating: number | null;
  reviewsCount: number;
}

export type BookingStatus = "PENDING" | "CONFIRMED" | "CANCELLED";

export interface Booking {
  id: number;
  confirmationCode: string;
  bookingDate: string;
  numberOfPeople: number;
  totalPrice: number;
  status: BookingStatus;
  tour: Tour;
  userId: number;
  userFullName: string;
}

export interface Review {
  id: number;
  rating: number;
  comment: string | null;
  createdAt: string;
  userFullName: string;
  tourId: number;
}

export interface UserProfile {
  id: number;
  fullName: string;
  email: string;
  phone: string | null;
  active: boolean;
  registeredAt: string;
  roles: RoleName[];
}

export interface AuthResult {
  token: string;
  userId: number;
  fullName: string;
  email: string;
  roles: string[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ApiErrorBody {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  details?: string[];
}
