# Диаграммы последовательности

## Сценарий 1: Регистрация нового пользователя (UC4)

```plantuml
@startuml
actor Пользователь
participant "RegisterScreen\n(Presentation)" as P
participant "AuthController\n(Control)" as C
participant "AuthServiceImpl\n(Mediator)" as M
participant "UserRepository\n(Foundation)" as F
database "PostgreSQL" as DB

Пользователь -> P : заполняет форму\n(имя, email, пароль)
P -> P : валидация формата (validate())
P -> C : POST /api/auth/register\n{fullName, email, password, phone}
C -> C : @Valid BindingResult
C -> M : authService.register(request)
M -> F : userRepository.existsByEmail(email)
F -> DB : SELECT ... WHERE email=?
DB --> F : false
F --> M : false
M -> M : new User(...)\nuser.addRole(ROLE_USER)\npassword = BCrypt(password)
M -> F : userRepository.save(user)
F -> DB : INSERT INTO users ...
DB --> F : savedUser
F --> M : savedUser
M -> M : jwtUtils.generateToken(email, id)
M --> C : AuthResponse(token, userId, ...)
C --> P : 201 Created + AuthResponse
P -> P : saveToken(AsyncStorage)\nsetUser(profile)
P --> Пользователь : переход на HomeScreen
@enduml
```

---

## Сценарий 2: Бронирование тура (UC6)

```plantuml
@startuml
actor Турист
participant "BookingScreen\n(Presentation)" as P
participant "BookingController\n(Control)" as C
participant "BookingServiceImpl\n(Mediator)" as M
participant "TourServiceImpl\n(Mediator)" as TM
participant "TourRepository\n(Foundation)" as TF
participant "BookingRepository\n(Foundation)" as BF
database "PostgreSQL" as DB

Турист -> P : нажимает «Подтвердить»\n(people=2)
P -> C : POST /api/bookings/tour/{tourId}\n{numberOfPeople: 2}\nAuthorization: Bearer <JWT>
C -> C : JwtAuthFilter.authenticate()
C -> M : bookingService.createBooking(tourId, userId, request)
M -> TM : tourService.getEntityOrThrow(tourId)
TM -> TF : tourRepository.findById(tourId)
TF -> DB : SELECT * FROM tours WHERE id=?
DB --> TF : tour
TF --> TM : tour
TM --> M : tour
M -> M : tour.reservePlaces(2)\nавailablePlaces -= 2
M -> M : new Booking(user, tour, 2)\ntotalPrice = tour.price * 2\nconfirmationCode = UUID
M -> BF : bookingRepository.save(booking)
BF -> DB : INSERT INTO bookings ...
DB --> BF : savedBooking
BF --> M : savedBooking
M --> C : BookingResponse
C --> P : 201 Created + BookingResponse
P --> Турист : Alert("Бронирование создано!\nКод: xxxxxxxx")
@enduml
```

---

## Сценарий 3: Администратор подтверждает бронирование (UC13)

```plantuml
@startuml
actor Администратор
participant "AdminScreen\n(Presentation)" as P
participant "BookingController\n(Control)" as C
participant "BookingServiceImpl\n(Mediator)" as M
participant "BookingRepository\n(Foundation)" as BF
database "PostgreSQL" as DB

Администратор -> P : нажимает «Подтвердить» у бронирования
P -> C : PUT /api/bookings/{id}/confirm\nAuthorization: Bearer <JWT_ADMIN>
C -> C : @PreAuthorize("hasRole('ADMIN')")
C -> M : bookingService.confirmBooking(id)
M -> BF : bookingRepository.findById(id)
BF -> DB : SELECT * FROM bookings WHERE id=?
DB --> BF : booking (status=PENDING)
BF --> M : booking
M -> M : booking.confirm()\nstatus = CONFIRMED
M --> C : BookingResponse(status=CONFIRMED)
C --> P : 200 OK + BookingResponse
P --> Администратор : обновление статуса в списке
@enduml
```
