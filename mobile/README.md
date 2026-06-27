# Tour Agency Mobile (React Native + Expo)

Мобильный клиент — **Presentation-слой** архитектуры PCMEF.
Взаимодействует с backend через REST API (JWT в AsyncStorage).

## Стек

- React Native 0.74 + Expo 51
- TypeScript
- @react-navigation (stack + bottom-tabs)
- Axios
- @react-native-async-storage/async-storage
- @expo/vector-icons (Ionicons)
- date-fns

## Экраны (5+ по требованию методички)

| Экран | Маршрут | Описание |
|---|---|---|
| LoginScreen | Auth/Login | Вход, POST /api/auth/login |
| RegisterScreen | Auth/Register | Регистрация, POST /api/auth/register |
| HomeScreen | App/Tours/Home | Главная: направления + популярные туры |
| TourListScreen | App/Tours/TourList | Каталог с поиском и фильтром |
| TourDetailScreen | App/Tours/TourDetail | Детали тура, отзывы |
| BookingScreen | App/Tours/Booking | Оформление бронирования |
| MyBookingsScreen | App/Bookings/MyBookings | Мои поездки, отмена |
| ProfileScreen | App/Profile/Profile | Профиль, редактирование, выход |
| AdminScreen | App/Admin | Панель администратора (только ROLE_ADMIN) |

## Структура (PCMEF: Presentation-слой)

```
src/
├── api/           # ApiFunctions.ts — вызовы REST API (аналог оригинального ApiFunctions.js)
│                  # client.ts — axios с JWT interceptor (AsyncStorage вместо localStorage)
├── context/       # AuthContext — глобальное состояние аутентификации
├── navigation/    # RootNavigator → AuthNavigator / AppNavigator
├── components/    # TourCard, Button, InputField, StarRating, EmptyState
├── screens/       # auth/, tours/, booking/, profile/, admin/
└── theme/         # design tokens: цвета, шрифты, отступы, тени
```

## Запуск

```bash
cd mobile
npm install
npm start        # запускает Expo Dev Server
# затем:
# нажмите 'a' для Android-эмулятора
# нажмите 'i' для iOS-симулятора (только macOS)
```

Backend должен быть запущен на `localhost:8080` (Android-эмулятор использует `10.0.2.2:8080`).
Адрес настраивается в `src/api/client.ts` (функция `resolveBaseUrl`).

## Авторизация

JWT хранится в `AsyncStorage` (key: `touragency_token`).
При запуске приложения токен автоматически восстанавливается из хранилища —
пользователь остаётся в системе без повторного входа.
