package controller;

import dao.PaymentDAO;
import model.Payment;

import java.util.List;

public class PaymentController {

    private final PaymentDAO paymentDAO;

    public PaymentController() {
        this.paymentDAO = new PaymentDAO();
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }

    public Payment getPaymentByBookingId(int bookingId) {
        return paymentDAO.getPaymentByBookingId(bookingId);
    }

    public String addPayment(Payment payment) {
        try {
            paymentDAO.addPayment(payment);
            return null;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public String updatePaymentStatus(int paymentId, String status) {
        try {
            paymentDAO.updatePaymentStatus(paymentId, status);
            return null;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public int getTotalPayments() {
        return paymentDAO.getTotalPayments();
    }

    public double getTotalRevenue() {
        return paymentDAO.getTotalRevenue();
    }
}
