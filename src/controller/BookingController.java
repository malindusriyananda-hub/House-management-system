package controller;

import dao.BookingDAO;
import dao.PaymentDAO;
import db.DBConnection;
import exception.InvalidBookingException;
import model.Booking;
import model.Guest;
import model.Payment;
import model.Room;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BookingController {

    private final BookingDAO bookingDAO;
    private final PaymentDAO paymentDAO;
    private final RoomController roomController;

    public BookingController() {
        this.bookingDAO = new BookingDAO();
        this.paymentDAO = new PaymentDAO();
        this.roomController = new RoomController();
    }

    public int createBooking(Guest guest, Room room, String checkInStr, String checkOutStr,
                              int guestCount, String paymentMethod) throws InvalidBookingException {

        if (guestCount > room.getMaxGuests()) {
            throw new InvalidBookingException("Guest count (" + guestCount + ") exceeds room maximum (" + room.getMaxGuests() + ")");
        }

        LocalDate checkInLocal;
        LocalDate checkOutLocal;
        try {
            String[] inParts = checkInStr.split("/");
            checkInLocal = LocalDate.of(Integer.parseInt(inParts[2]), Integer.parseInt(inParts[1]), Integer.parseInt(inParts[0]));
            String[] outParts = checkOutStr.split("/");
            checkOutLocal = LocalDate.of(Integer.parseInt(outParts[2]), Integer.parseInt(outParts[1]), Integer.parseInt(outParts[0]));
        } catch (Exception e) {
            throw new InvalidBookingException("Invalid date format. Use dd/MM/yyyy");
        }

        if (checkInLocal.isBefore(LocalDate.now())) {
            throw new InvalidBookingException("Check-in date cannot be in the past");
        }

        if (checkOutLocal.isBefore(checkInLocal) || checkOutLocal.isEqual(checkInLocal)) {
            throw new InvalidBookingException("Check-out date must be after check-in date");
        }

        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new InvalidBookingException("Room is not available");
        }

        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkInLocal, checkOutLocal);
        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Connection conn = DBConnection.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);

            Booking booking = new Booking();
            booking.setGuestId(guest.getGuestId());
            booking.setRoomId(room.getRoomId());
            booking.setCheckInDate(Date.valueOf(checkInLocal));
            booking.setCheckOutDate(Date.valueOf(checkOutLocal));
            booking.setGuestCount(guestCount);
            booking.setTotalAmount(totalAmount);
            booking.setStatus("CONFIRMED");
            booking.setBookingDate(Date.valueOf(LocalDate.now()));

            int bookingId = bookingDAO.addBooking(booking);

            roomController.updateRoomStatus(room.getRoomId(), "OCCUPIED");

            Payment payment = new Payment();
            payment.setBookingId(bookingId);
            payment.setPaymentDate(Date.valueOf(LocalDate.now()));
            payment.setAmount(totalAmount);
            payment.setPaymentMethod(paymentMethod);
            payment.setStatus("PENDING");
            paymentDAO.addPayment(payment);

            conn.commit();
            return bookingId;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Rollback failed: " + ex.getMessage(), ex);
            }
            throw new RuntimeException("Booking creation failed: " + e.getMessage(), e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                // ignore
            }
        }
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }

    public Booking getBookingById(int id) {
        return bookingDAO.getBookingById(id);
    }

    public String cancelBooking(int bookingId) {
        try {
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking != null) {
                bookingDAO.cancelBooking(bookingId);
                roomController.updateRoomStatus(booking.getRoomId(), "AVAILABLE");
            }
            return null;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public String checkOutBooking(int bookingId) {
        try {
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking != null) {
                booking.setStatus("CHECKED_OUT");
                bookingDAO.updateBooking(booking);
                roomController.updateRoomStatus(booking.getRoomId(), "AVAILABLE");
            }
            return null;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public List<Booking> getBookingsByStatus(String status) {
        return bookingDAO.getBookingsByStatus(status);
    }

    public List<Booking> searchBookings(String keyword) {
        return bookingDAO.searchBookings(keyword);
    }

    public int getTotalBookings() {
        return bookingDAO.getTotalBookings();
    }

    public int getTodaysCheckIns() {
        return bookingDAO.getTodaysCheckIns();
    }
}
