package controller;

import dao.GuestDAO;
import model.Guest;
import java.util.List;

public class GuestController {

    private final GuestDAO guestDAO;

    public GuestController() {
        this.guestDAO = new GuestDAO();
    }

    public List<Guest> getAllGuests() {
        return guestDAO.getAllGuests();
    }

    public Guest getGuestById(int id) {
        return guestDAO.getGuestById(id);
    }

    public String addGuest(Guest guest) {
        try {
            guestDAO.addGuest(guest);
            return null;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public String updateGuest(Guest guest) {
        try {
            guestDAO.updateGuest(guest);
            return null;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public String deleteGuest(int id) {
        try {
            guestDAO.deleteGuest(id);
            return null;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public List<Guest> searchGuests(String keyword) {
        return guestDAO.searchGuests(keyword);
    }

    public int getTotalGuests() {
        return guestDAO.getTotalGuests();
    }
}
