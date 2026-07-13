package dao;

import db.DBConnection;
import model.Payment;
import java.math.BigDecimal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(mapPayment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payments: " + e.getMessage(), e);
        }
        return payments;
    }

    public Payment getPaymentByBookingId(int bookingId) {
        String sql = "SELECT * FROM payments WHERE booking_id = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapPayment(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payment: " + e.getMessage(), e);
        }
    }

    public void addPayment(Payment p) {
        String sql = "INSERT INTO payments (booking_id, payment_date, amount, payment_method, status) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, p.getBookingId());
            stmt.setDate(2, p.getPaymentDate());
            stmt.setBigDecimal(3, p.getAmount());
            stmt.setString(4, p.getPaymentMethod());
            stmt.setString(5, p.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding payment: " + e.getMessage(), e);
        }
    }

    public void updatePaymentStatus(int paymentId, String status) {
        String sql = "UPDATE payments SET status=? WHERE payment_id=?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, paymentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating payment status: " + e.getMessage(), e);
        }
    }

    public int getTotalPayments() {
        String sql = "SELECT COUNT(*) FROM payments";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting payments: " + e.getMessage(), e);
        }
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status = 'PAID'";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating revenue: " + e.getMessage(), e);
        }
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setBookingId(rs.getInt("booking_id"));
        p.setPaymentDate(rs.getDate("payment_date"));
        p.setAmount(rs.getBigDecimal("amount"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setStatus(rs.getString("status"));
        return p;
    }
}
