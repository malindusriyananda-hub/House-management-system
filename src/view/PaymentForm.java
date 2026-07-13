package view;

import controller.PaymentController;
import controller.BookingController;
import controller.GuestController;
import controller.RoomController;
import model.Payment;
import model.Booking;
import model.Guest;
import model.Room;
import db.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;


public class PaymentForm extends JPanel {

    private static final Color PRIMARY = new Color(27, 67, 50);
    private static final Color SECONDARY = new Color(74, 124, 89);
    private static final Color ACCENT = new Color(196, 154, 91);
    private static final Color BACKGROUND = new Color(250, 246, 238);
    private static final Color TEXT = new Color(35, 46, 33);
    private static final Color WHITE = Color.WHITE;

    private PaymentController paymentController;
    private BookingController bookingController;
    private GuestController guestController;
    private RoomController roomController;

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> methodFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private List<Payment> allPayments;

    public PaymentForm() {
        paymentController = new PaymentController();
        bookingController = new BookingController();
        guestController = new GuestController();
        roomController = new RoomController();
        allPayments = new ArrayList<>();
        initComponents();
        loadPayments();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 15));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(BACKGROUND);

        JLabel title = new JLabel("Payment Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        topPanel.add(title, BorderLayout.WEST);

        JButton reportBtn = new JButton("Generate Report");
        reportBtn.setBackground(ACCENT);
        reportBtn.setForeground(TEXT);
        reportBtn.setFocusPainted(false);
        reportBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        reportBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        reportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reportBtn.addActionListener(e -> generateReport());
        topPanel.add(reportBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(BACKGROUND);

        JLabel methodLabel = new JLabel("Payment Method:");
        methodLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterPanel.add(methodLabel);

        methodFilterCombo = new JComboBox<>(new String[]{"All", "CASH", "CARD", "BANK_TRANSFER"});
        filterPanel.add(methodFilterCombo);

        JLabel statusLabel = new JLabel("  Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterPanel.add(statusLabel);

        statusFilterCombo = new JComboBox<>(new String[]{"All", "PAID", "PENDING", "CANCELLED"});
        filterPanel.add(statusFilterCombo);

        JButton applyBtn = new JButton("Apply Filter");
        applyBtn.setBackground(SECONDARY);
        applyBtn.setForeground(WHITE);
        applyBtn.setFocusPainted(false);
        applyBtn.setBorder(new EmptyBorder(6, 15, 6, 15));
        applyBtn.addActionListener(e -> filterPayments());
        filterPanel.add(applyBtn);

        add(filterPanel, BorderLayout.BEFORE_FIRST_LINE);

        String[] columns = {"Payment ID", "Booking ID", "Guest Name", "Room Number", "Amount (LKR)", "Payment Method", "Status", "Payment Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(BACKGROUND);

        JButton markPaidBtn = new JButton("Mark as Paid");
        markPaidBtn.setBackground(new Color(46, 125, 50));
        markPaidBtn.setForeground(WHITE);
        markPaidBtn.setFocusPainted(false);
        markPaidBtn.setBorder(new EmptyBorder(8, 25, 8, 25));
        markPaidBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        markPaidBtn.setEnabled(false);

        markPaidBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int paymentId = (int) tableModel.getValueAt(row, 0);
                String error = paymentController.updatePaymentStatus(paymentId, "PAID");
                if (error != null) {
                    JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    loadPayments();
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String status = (String) tableModel.getValueAt(row, 6);
                markPaidBtn.setEnabled("PENDING".equals(status));
            } else {
                markPaidBtn.setEnabled(false);
            }
        });

        buttonPanel.add(markPaidBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY);
        header.setForeground(WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 35));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? WHITE : new Color(227, 242, 253));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (column == 4 && value instanceof BigDecimal) {
                    setText("LKR " + NumberFormat.getNumberInstance().format(value));
                }
                if (column == 6 && value != null) {
                    String status = value.toString();
                    if ("PAID".equals(status)) {
                        setForeground(new Color(46, 125, 50));
                    } else if ("PENDING".equals(status)) {
                        setForeground(new Color(255, 143, 0));
                    } else if ("CANCELLED".equals(status)) {
                        setForeground(new Color(211, 47, 47));
                    }
                }
                return c;
            }
        });
    }

    private void loadPayments() {
        allPayments = paymentController.getAllPayments();
        refreshTable(allPayments);
    }

    private void filterPayments() {
        String method = (String) methodFilterCombo.getSelectedItem();
        String status = (String) statusFilterCombo.getSelectedItem();

        List<Payment> filtered = new ArrayList<>();
        for (Payment p : allPayments) {
            boolean methodMatch = "All".equals(method) || method.equals(p.getPaymentMethod());
            boolean statusMatch = "All".equals(status) || status.equals(p.getStatus());
            if (methodMatch && statusMatch) filtered.add(p);
        }
        refreshTable(filtered);
    }

    private void refreshTable(List<Payment> payments) {
        tableModel.setRowCount(0);
        for (Payment p : payments) {
            Booking b = bookingController.getBookingById(p.getBookingId());
            String guestName = "-";
            String roomNumber = "-";
            if (b != null) {
                Guest g = guestController.getGuestById(b.getGuestId());
                Room r = roomController.getRoomById(b.getRoomId());
                if (g != null) guestName = g.getName();
                if (r != null) roomNumber = r.getRoomNumber();
            }
            tableModel.addRow(new Object[]{
                p.getPaymentId(), p.getBookingId(), guestName, roomNumber,
                p.getAmount(), p.getPaymentMethod(), p.getStatus(), p.getPaymentDate()
            });
        }
    }

    private void generateReport() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Generate Report", true);
        dialog.setSize(380, 220);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField fromDateField = new JTextField(15);
        JTextField toDateField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel fromLabel = new JLabel("From Date (dd/MM/yyyy):");
        fromLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(fromLabel, gbc);
        gbc.gridx = 1;
        panel.add(fromDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel toLabel = new JLabel("To Date (dd/MM/yyyy):");
        toLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(toLabel, gbc);
        gbc.gridx = 1;
        panel.add(toDateField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(WHITE);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(TEXT);
        cancelBtn.setForeground(WHITE);
        cancelBtn.setFocusPainted(false);

        JButton generateBtn = new JButton("Generate");
        generateBtn.setBackground(ACCENT);
        generateBtn.setForeground(TEXT);
        generateBtn.setFocusPainted(false);
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        generateBtn.addActionListener(e -> {
            String from = fromDateField.getText().trim();
            String to = toDateField.getText().trim();
            if (from.isEmpty() || to.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter both dates");
                return;
            }
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                java.util.Date fromDate = sdf.parse(from);
                java.util.Date toDate = sdf.parse(to);
                dialog.dispose();
                openJasperReport(fromDate, toDate);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(generateBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void openJasperReport(java.util.Date fromDate, java.util.Date toDate) {
        try {
            String reportPath = "src/report/BookingReport.jrxml";
            java.io.InputStream reportStream = getClass().getResourceAsStream("/report/BookingReport.jrxml");
            if (reportStream == null) {
                reportStream = new java.io.FileInputStream(reportPath);
            }

            java.util.Map<String, Object> parameters = new java.util.HashMap<>();
            parameters.put("FROM_DATE", fromDate);
            parameters.put("TO_DATE", toDate);

            net.sf.jasperreports.engine.JasperReport jasperReport =
                    net.sf.jasperreports.engine.JasperCompileManager.compileReport(reportStream);
            net.sf.jasperreports.engine.JasperPrint jasperPrint =
                    net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, parameters,
                            DBConnection.getInstance().getConnection());

            net.sf.jasperreports.view.JasperViewer viewer = new net.sf.jasperreports.view.JasperViewer(jasperPrint, false);
            viewer.setTitle("Wren House Booking Report");
            viewer.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(),
                    "Report Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
