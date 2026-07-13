package dao;

import db.DBConnection;
import model.Booking;
import java.math.BigDecimal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking b = mapBooking(rs);
                bookings.add(b);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bookings: " + e.getMessage(), e);
        }
        return bookings;
    }

    public Booking getBookingById(int id) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapBooking(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching booking: " + e.getMessage(), e);
        }
    }

    public int addBooking(Booking b) {
        String sql = "INSERT INTO bookings (guest_id, room_id, check_in_date, check_out_date, guest_count, total_amount, status, booking_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, b.getGuestId());
            stmt.setInt(2, b.getRoomId());
            stmt.setDate(3, b.getCheckInDate());
            stmt.setDate(4, b.getCheckOutDate());
            stmt.setInt(5, b.getGuestCount());
            stmt.setBigDecimal(6, b.getTotalAmount());
            stmt.setString(7, b.getStatus());
            stmt.setDate(8, b.getBookingDate());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding booking: " + e.getMessage(), e);
        }
    }

    public void updateBooking(Booking b) {
        String sql = "UPDATE bookings SET guest_id=?, room_id=?, check_in_date=?, check_out_date=?, guest_count=?, total_amount=?, status=?, booking_date=? WHERE booking_id=?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, b.getGuestId());
            stmt.setInt(2, b.getRoomId());
            stmt.setDate(3, b.getCheckInDate());
            stmt.setDate(4, b.getCheckOutDate());
            stmt.setInt(5, b.getGuestCount());
            stmt.setBigDecimal(6, b.getTotalAmount());
            stmt.setString(7, b.getStatus());
            stmt.setDate(8, b.getBookingDate());
            stmt.setInt(9, b.getBookingId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating booking: " + e.getMessage(), e);
        }
    }

    public void cancelBooking(int id) {
        String sql = "UPDATE bookings SET status='CANCELLED' WHERE booking_id=?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error cancelling booking: " + e.getMessage(), e);
        }
    }

    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE status = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bookings: " + e.getMessage(), e);
        }
        return bookings;
    }

    public List<Booking> searchBookings(String keyword) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE CAST(booking_id AS CHAR) LIKE ? OR status LIKE ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            String like = "%" + keyword + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching bookings: " + e.getMessage(), e);
        }
        return bookings;
    }

    public int getTotalBookings() {
        String sql = "SELECT COUNT(*) FROM bookings";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting bookings: " + e.getMessage(), e);
        }
    }

    public int getTodaysCheckIns() {
        String sql = "SELECT COUNT(*) FROM bookings WHERE check_in_date = CURDATE() AND status IN ('CONFIRMED', 'CHECKED_IN')";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting today's check-ins: " + e.getMessage(), e);
        }
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setGuestId(rs.getInt("guest_id"));
        b.setRoomId(rs.getInt("room_id"));
        b.setCheckInDate(rs.getDate("check_in_date"));
        b.setCheckOutDate(rs.getDate("check_out_date"));
        b.setGuestCount(rs.getInt("guest_count"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setStatus(rs.getString("status"));
        b.setBookingDate(rs.getDate("booking_date"));
        return b;
    }
}
