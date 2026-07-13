package dao;

import db.DBConnection;
import model.Guest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Guest g = new Guest();
                g.setGuestId(rs.getInt("guest_id"));
                g.setName(rs.getString("name"));
                g.setNic(rs.getString("nic"));
                g.setPhone(rs.getString("phone"));
                g.setEmail(rs.getString("email"));
                g.setAddress(rs.getString("address"));
                guests.add(g);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching guests: " + e.getMessage(), e);
        }
        return guests;
    }

    public Guest getGuestById(int id) {
        String sql = "SELECT * FROM guests WHERE guest_id = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Guest g = new Guest();
                g.setGuestId(rs.getInt("guest_id"));
                g.setName(rs.getString("name"));
                g.setNic(rs.getString("nic"));
                g.setPhone(rs.getString("phone"));
                g.setEmail(rs.getString("email"));
                g.setAddress(rs.getString("address"));
                return g;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching guest: " + e.getMessage(), e);
        }
    }

    public void addGuest(Guest g) {
        String sql = "INSERT INTO guests (name, nic, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, g.getName());
            stmt.setString(2, g.getNic());
            stmt.setString(3, g.getPhone());
            stmt.setString(4, g.getEmail());
            stmt.setString(5, g.getAddress());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding guest: " + e.getMessage(), e);
        }
    }

    public void updateGuest(Guest g) {
        String sql = "UPDATE guests SET name=?, nic=?, phone=?, email=?, address=? WHERE guest_id=?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, g.getName());
            stmt.setString(2, g.getNic());
            stmt.setString(3, g.getPhone());
            stmt.setString(4, g.getEmail());
            stmt.setString(5, g.getAddress());
            stmt.setInt(6, g.getGuestId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating guest: " + e.getMessage(), e);
        }
    }

    public void deleteGuest(int id) {
        String sql = "DELETE FROM guests WHERE guest_id=?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting guest: " + e.getMessage(), e);
        }
    }

    public List<Guest> searchGuests(String keyword) {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests WHERE name LIKE ? OR phone LIKE ? OR email LIKE ? OR nic LIKE ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            String like = "%" + keyword + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            stmt.setString(4, like);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Guest g = new Guest();
                g.setGuestId(rs.getInt("guest_id"));
                g.setName(rs.getString("name"));
                g.setNic(rs.getString("nic"));
                g.setPhone(rs.getString("phone"));
                g.setEmail(rs.getString("email"));
                g.setAddress(rs.getString("address"));
                guests.add(g);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching guests: " + e.getMessage(), e);
        }
        return guests;
    }

    public int getTotalGuests() {
        String sql = "SELECT COUNT(*) FROM guests";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting guests: " + e.getMessage(), e);
        }
    }
}
