package controller;

import dao.RoomDAO;
import model.Room;
import java.util.List;

public class RoomController {

    private final RoomDAO roomDAO;

    public RoomController() {
        this.roomDAO = new RoomDAO();
    }

    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    public Room getRoomById(int id) {
        return roomDAO.getRoomById(id);
    }

    public List<Room> getAvailableRooms() {
        return roomDAO.getAvailableRooms();
    }

    public String addRoom(Room room) {
        try {
            roomDAO.addRoom(room);
            return null;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public String updateRoom(Room room) {
        try {
            roomDAO.updateRoom(room);
            return null;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public String deleteRoom(int id) {
        try {
            roomDAO.deleteRoom(id);
            return null;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public String updateRoomStatus(int roomId, String status) {
        try {
            roomDAO.updateRoomStatus(roomId, status);
            return null;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public int getTotalRooms() {
        return roomDAO.getTotalRooms();
    }

    public int getAvailableRoomCount() {
        return roomDAO.getAvailableRoomCount();
    }
}
