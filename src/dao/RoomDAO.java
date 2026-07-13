package dao;

import db.DBConnection;
import model.Room;
import java.math.BigDecimal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setRoomType(rs.getString("room_type"));
                r.setPricePerNight(rs.getBigDecimal("price_per_night"));
                r.setStatus(rs.getString("status"));
                r.setMaxGuests(rs.getInt("max_guests"));
                rooms.add(r);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching rooms: " + e.getMessage(), e);
        }
        return rooms;
    }

    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setRoomType(rs.getString("room_type"));
                r.setPricePerNight(rs.getBigDecimal("price_per_night"));
                r.setStatus(rs.getString("status"));
                r.setMaxGuests(rs.getInt("max_guests"));
                return r;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching room: " + e.getMessage(), e);
        }
    }

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE status = 'AVAILABLE'";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setRoomType(rs.getString("room_type"));
                r.setPricePerNight(rs.getBigDecimal("price_per_night"));
                r.setStatus(rs.getString("status"));
                r.setMaxGuests(rs.getInt("max_guests"));
                rooms.add(r);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching available rooms: " + e.getMessage(), e);
        }
        return rooms;
    }

    public void addRoom(Room r) {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, status, max_guests) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, r.getRoomNumber());
            stmt.setString(2, r.getRoomType());
            stmt.setBigDecimal(3, r.getPricePerNight());
            stmt.setString(4, r.getStatus());
            stmt.setInt(5, r.getMaxGuests());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding room: " + e.getMessage(), e);
        }
    }

    public void updateRoom(Room r) {
        String sql = "UPDATE rooms SET room_number=?, room_type=?, price_per_night=?, status=?, max_guests=? WHERE room_id=?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, r.getRoomNumber());
            stmt.setString(2, r.getRoomType());
            stmt.setBigDecimal(3, r.getPricePerNight());
            stmt.setString(4, r.getStatus());
            stmt.setInt(5, r.getMaxGuests());
            stmt.setInt(6, r.getRoomId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating room: " + e.getMessage(), e);
        }
    }

    public void deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE room_id=?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting room: " + e.getMessage(), e);
        }
    }

    public void updateRoomStatus(int roomId, String status) {
        String sql = "UPDATE rooms SET status=? WHERE room_id=?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating room status: " + e.getMessage(), e);
        }
    }

    public int getTotalRooms() {
        String sql = "SELECT COUNT(*) FROM rooms";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting rooms: " + e.getMessage(), e);
        }
    }

    public int getAvailableRoomCount() {
        String sql = "SELECT COUNT(*) FROM rooms WHERE status = 'AVAILABLE'";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting available rooms: " + e.getMessage(), e);
        }
    }
}
