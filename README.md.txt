# The Wren House Management System

A Java Swing-based enterprise application for hotel management built with NetBeans IDE.

## Features

- **User Authentication** - Secure login with SHA-256 password hashing
- **Dashboard** - Real-time statistics and today's check-ins overview
- **Guest Management** - Full CRUD operations with search functionality
- **Room Management** - Room inventory with status tracking
- **Booking System** - Complete booking workflow with validation
- **Payment Management** - Payment tracking and status updates
- **Reports** - JasperReports integration for booking reports

## Prerequisites

- **Java JDK 17** or higher
- **XAMPP** (MySQL database server)
- **NetBeans IDE** (recommended) or any Java IDE

## Database Setup

1. Start XAMPP MySQL service
2. Open phpMyAdmin (http://localhost/phpmyadmin)
3. Import the `database_schema.sql` file
4. This creates:
   - Database: `wren_house_db`
   - Tables: users, guests, rooms, bookings, payments
   - Default admin user: `admin` / `1234`

## Building the Application

### Using NetBeans:
1. Open the project in NetBeans
2. Right-click project → Clean and Build
3. JAR file will be created in `dist/Hotel_management_system.jar`

### Using Command Line:
```bash
ant clean
ant jar
```

## Running the Application

### From NetBeans:
- Click Run → Run Project (F6)

### From Command Line:
```bash
java -jar dist/Hotel_management_system.jar
```

### Default Login:
- **Username:** admin
- **Password:** 1234

## Project Structure

```
src/
├── controller/          # Business logic controllers
│   ├── BookingController.java
│   ├── GuestController.java
│   ├── PaymentController.java
│   └── RoomController.java
├── dao/                 # Data Access Objects
│   ├── BookingDAO.java
│   ├── GuestDAO.java
│   ├── PaymentDAO.java
│   ├── RoomDAO.java
│   └── UserDAO.java
├── db/                  # Database connection
│   └── DBConnection.java
├── exception/           # Custom exceptions
│   └── InvalidBookingException.java
├── model/               # Data models (POJOs)
│   ├── Booking.java
│   ├── Guest.java
│   ├── Payment.java
│   ├── Room.java
│   └── User.java
├── view/                # UI components
│   ├── BookingForm.java
│   ├── GuestForm.java
│   ├── PaymentForm.java
│   └── RoomForm.java
├── report/              # JasperReports
│   └── BookingReport.jrxml
├── Dashboard.java       # Main dashboard
├── Login.java           # Login form
└── hotel/management/system/
    └── HotelManagementSystem.java  # Entry point
```

## Creating Executable (.exe)

1. Download [Launch4j](http://launch4j.sourceforge.net/)
2. Configure:
   - **Output file:** TheWrenHouse.exe
   - **Jar:** dist/Hotel_management_system.jar
   - **Minimum JRE:** 17
   - **Initial heap size:** 256 MB
   - **Maximum heap size:** 512 MB
3. Click Build Wrapper

## Color Palette

| Element | Color Code |
|---------|------------|
| Primary | #0D47A1 |
| Secondary | #1976D2 |
| Accent | #FFB300 |
| Background | #F5F7FA |
| Text | #263238 |

## License

This project is for educational purposes.
