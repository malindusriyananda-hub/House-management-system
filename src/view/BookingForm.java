package view;

import controller.GuestController;
import controller.RoomController;
import controller.BookingController;
import controller.PaymentController;
import exception.InvalidBookingException;
import model.Guest;
import model.Room;
import model.Booking;
import model.Payment;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;


public class BookingForm extends JPanel {

    private static final Color PRIMARY = new Color(27, 67, 50);
    private static final Color SECONDARY = new Color(74, 124, 89);
    private static final Color ACCENT = new Color(196, 154, 91);
    private static final Color BACKGROUND = new Color(250, 246, 238);
    private static final Color TEXT = new Color(35, 46, 33);
    private static final Color WHITE = Color.WHITE;

    private GuestController guestController;
    private RoomController roomController;
    private BookingController bookingController;
    private PaymentController paymentController;

    private JComboBox<Guest> guestCombo;
    private JComboBox<Room> roomCombo;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JSpinner guestCountSpinner;
    private JComboBox<String> paymentMethodCombo;
    private JTextField totalNightsField;
    private JTextField totalAmountField;
    private JLabel errorLabel;
    private JButton confirmBtn;

    private JPanel summaryPanel;
    private JLabel summaryGuest, summaryRoomNum, summaryRoomType, summaryPrice;
    private JLabel summaryCheckIn, summaryCheckOut, summaryNights, summaryGuestCount, summaryTotal;

    private JTable bookingsTable;
    private DefaultTableModel bookingsModel;
    private JComboBox<String> statusFilterCombo;
    private JTextField searchField;
    private List<Booking> allBookings;

    public BookingForm() {
        guestController = new GuestController();
        roomController = new RoomController();
        bookingController = new BookingController();
        paymentController = new PaymentController();
        allBookings = new ArrayList<>();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 15));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Booking Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        add(title, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550);
        splitPane.setLeftComponent(createBookingFormPanel());
        splitPane.setRightComponent(createSummaryPanel());
        splitPane.setBorder(null);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(splitPane, BorderLayout.CENTER);

        JPanel bottomSection = createBookingsTablePanel();

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSection, bottomSection);
        mainSplit.setDividerLocation(320);
        mainSplit.setBorder(null);

        add(mainSplit, BorderLayout.CENTER);
    }

    private JPanel createBookingFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        guestCombo = new JComboBox<>();
        guestCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                if (value instanceof Guest) setText(((Guest) value).getName());
                return this;
            }
        });

        roomCombo = new JComboBox<>();
        roomCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                if (value instanceof Room) {
                    Room r = (Room) value;
                    setText(r.getRoomNumber() + " - " + r.getRoomType() + " (LKR " + r.getPricePerNight() + ")");
                }
                return this;
            }
        });

        checkInField = new JTextField(15);
        checkOutField = new JTextField(15);
        guestCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        paymentMethodCombo = new JComboBox<>(new String[]{"CASH", "CARD", "BANK_TRANSFER"});
        totalNightsField = new JTextField(15);
        totalNightsField.setEditable(false);
        totalNightsField.setBackground(new Color(240, 240, 240));
        totalAmountField = new JTextField(15);
        totalAmountField.setEditable(false);
        totalAmountField.setBackground(new Color(240, 240, 240));
        totalAmountField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalAmountField.setForeground(PRIMARY);

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(211, 47, 47));
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        int row = 0;
        addFormRow(panel, gbc, "Select Guest *:", guestCombo, row++);
        addFormRow(panel, gbc, "Select Room *:", roomCombo, row++);
        addFormRow(panel, gbc, "Check-in Date (dd/MM/yyyy) *:", checkInField, row++);
        addFormRow(panel, gbc, "Check-out Date (dd/MM/yyyy) *:", checkOutField, row++);
        addFormRow(panel, gbc, "Guest Count *:", guestCountSpinner, row++);
        addFormRow(panel, gbc, "Payment Method:", paymentMethodCombo, row++);
        addFormRow(panel, gbc, "Total Nights:", totalNightsField, row++);
        addFormRow(panel, gbc, "Total Amount (LKR):", totalAmountField, row++);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(errorLabel, gbc);
        row++;

        confirmBtn = new JButton("Confirm Booking");
        confirmBtn.setBackground(ACCENT);
        confirmBtn.setForeground(TEXT);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmBtn.setBorder(new EmptyBorder(12, 30, 12, 30));
        confirmBtn.setEnabled(false);
        confirmBtn.addActionListener(e -> confirmBooking());

        gbc.gridy = row;
        panel.add(confirmBtn, gbc);

        roomCombo.addActionListener(e -> updateSummary());
        checkInField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { calculateTotal(); }
        });
        checkOutField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { calculateTotal(); }
        });

        return panel;
    }

    private JPanel createSummaryPanel() {
        summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel summaryTitle = new JLabel("Booking Summary");
        summaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        summaryTitle.setForeground(PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        summaryPanel.add(summaryTitle, gbc);

        gbc.gridwidth = 1;
        summaryGuest = addSummaryRow(summaryPanel, gbc, 1, "Guest Name:", "-");
        summaryRoomNum = addSummaryRow(summaryPanel, gbc, 2, "Room Number:", "-");
        summaryRoomType = addSummaryRow(summaryPanel, gbc, 3, "Room Type:", "-");
        summaryPrice = addSummaryRow(summaryPanel, gbc, 4, "Price Per Night:", "-");
        summaryCheckIn = addSummaryRow(summaryPanel, gbc, 5, "Check-in:", "-");
        summaryCheckOut = addSummaryRow(summaryPanel, gbc, 6, "Check-out:", "-");
        summaryNights = addSummaryRow(summaryPanel, gbc, 7, "Total Nights:", "-");
        summaryGuestCount = addSummaryRow(summaryPanel, gbc, 8, "Guest Count:", "-");
        summaryTotal = addSummaryRow(summaryPanel, gbc, 9, "Total Amount:", "-");
        summaryTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        summaryTotal.setForeground(PRIMARY);

        return summaryPanel;
    }

    private JLabel addSummaryRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        val.setForeground(TEXT);
        panel.add(val, gbc);
        return val;
    }

    private JPanel createBookingsTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(BACKGROUND);

        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterPanel.add(filterLabel);

        statusFilterCombo = new JComboBox<>(new String[]{"All", "CONFIRMED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED"});
        statusFilterCombo.addActionListener(e -> filterBookings());
        filterPanel.add(statusFilterCombo);

        JLabel searchLabel = new JLabel("  Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterPanel.add(searchLabel);

        searchField = new JTextField(15);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { filterBookings(); }
        });
        filterPanel.add(searchField);

        panel.add(filterPanel, BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Guest Name", "Room Number", "Check-in", "Check-out", "Nights", "Total Amount", "Status"};
        bookingsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookingsTable = new JTable(bookingsModel);
        styleTable(bookingsTable);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnPanel.setBackground(BACKGROUND);

        JButton cancelBtn = new JButton("Cancel Booking");
        cancelBtn.setBackground(new Color(211, 47, 47));
        cancelBtn.setForeground(WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cancelBtn.setEnabled(false);

        JButton checkOutBtn = new JButton("Check Out");
        checkOutBtn.setBackground(new Color(46, 125, 50));
        checkOutBtn.setForeground(WHITE);
        checkOutBtn.setFocusPainted(false);
        checkOutBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        checkOutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        checkOutBtn.setEnabled(false);

        bookingsTable.getSelectionModel().addListSelectionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row >= 0) {
                String status = (String) bookingsModel.getValueAt(row, 7);
                cancelBtn.setEnabled("CONFIRMED".equals(status));
                checkOutBtn.setEnabled("CHECKED_IN".equals(status));
            } else {
                cancelBtn.setEnabled(false);
                checkOutBtn.setEnabled(false);
            }
        });

        cancelBtn.addActionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row >= 0) {
                int bookingId = (int) bookingsModel.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String error = bookingController.cancelBooking(bookingId);
                    if (error != null) {
                        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        loadData();
                    }
                }
            }
        });

        checkOutBtn.addActionListener(e -> {
            int row = bookingsTable.getSelectedRow();
            if (row >= 0) {
                int bookingId = (int) bookingsModel.getValueAt(row, 0);
                String error = bookingController.checkOutBooking(bookingId);
                if (error != null) {
                    JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    loadData();
                }
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(checkOutBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
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
                if (column == 7 && value != null) {
                    String status = value.toString();
                    if ("CONFIRMED".equals(status)) {
                        setForeground(new Color(74, 124, 89));
                    } else if ("CHECKED_IN".equals(status)) {
                        setForeground(new Color(46, 125, 50));
                    } else if ("CHECKED_OUT".equals(status)) {
                        setForeground(new Color(120, 120, 120));
                    } else if ("CANCELLED".equals(status)) {
                        setForeground(new Color(211, 47, 47));
                    }
                }
                return c;
            }
        });
    }

    private void loadData() {
        List<Guest> guests = guestController.getAllGuests();
        guestCombo.removeAllItems();
        for (Guest g : guests) guestCombo.addItem(g);

        List<Room> availableRooms = roomController.getAvailableRooms();
        roomCombo.removeAllItems();
        for (Room r : availableRooms) roomCombo.addItem(r);

        allBookings = bookingController.getAllBookings();
        refreshBookingsTable(allBookings);
        updateSummary();
    }

    private void updateSummary() {
        Guest guest = (Guest) guestCombo.getSelectedItem();
        Room room = (Room) roomCombo.getSelectedItem();

        summaryGuest.setText(guest != null ? guest.getName() : "-");
        summaryRoomNum.setText(room != null ? room.getRoomNumber() : "-");
        summaryRoomType.setText(room != null ? room.getRoomType() : "-");
        summaryPrice.setText(room != null ? "LKR " + NumberFormat.getNumberInstance().format(room.getPricePerNight()) : "-");
        summaryCheckIn.setText(checkInField.getText().isEmpty() ? "-" : checkInField.getText());
        summaryCheckOut.setText(checkOutField.getText().isEmpty() ? "-" : checkOutField.getText());

        if (room != null) {
            guestCountSpinner.setModel(new SpinnerNumberModel(1, 1, room.getMaxGuests(), 1));
            summaryGuestCount.setText("1 / " + room.getMaxGuests());
        }

        calculateTotal();
        confirmBtn.setEnabled(guest != null && room != null &&
                !checkInField.getText().isEmpty() && !checkOutField.getText().isEmpty());
    }

    private void calculateTotal() {
        Room room = (Room) roomCombo.getSelectedItem();
        String checkIn = checkInField.getText().trim();
        String checkOut = checkOutField.getText().trim();

        if (room == null || checkIn.isEmpty() || checkOut.isEmpty()) {
            totalNightsField.setText("");
            totalAmountField.setText("");
            return;
        }

        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate in = LocalDate.parse(checkIn, fmt);
            LocalDate out = LocalDate.parse(checkOut, fmt);
            long nights = ChronoUnit.DAYS.between(in, out);
            if (nights > 0) {
                totalNightsField.setText(String.valueOf(nights));
                BigDecimal total = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
                totalAmountField.setText("LKR " + NumberFormat.getNumberInstance().format(total));
            } else {
                totalNightsField.setText("-");
                totalAmountField.setText("-");
            }
        } catch (Exception e) {
            totalNightsField.setText("-");
            totalAmountField.setText("-");
        }
    }

    private void confirmBooking() {
        Guest guest = (Guest) guestCombo.getSelectedItem();
        Room room = (Room) roomCombo.getSelectedItem();
        String checkIn = checkInField.getText().trim();
        String checkOut = checkOutField.getText().trim();
        int guestCount = (int) guestCountSpinner.getValue();
        String method = (String) paymentMethodCombo.getSelectedItem();

        if (guest == null || room == null) {
            errorLabel.setText("Please select guest and room");
            return;
        }

        try {
            int bookingId = bookingController.createBooking(guest, room, checkIn, checkOut, guestCount, method);
            JOptionPane.showMessageDialog(this, "Booking confirmed! Booking ID: " + bookingId);
            loadData();
            resetForm();
        } catch (InvalidBookingException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    private void resetForm() {
        checkInField.setText("");
        checkOutField.setText("");
        guestCountSpinner.setValue(1);
        totalNightsField.setText("");
        totalAmountField.setText("");
        errorLabel.setText(" ");
    }

    private void refreshBookingsTable(List<Booking> bookings) {
        bookingsModel.setRowCount(0);
        GuestController gc = new GuestController();
        RoomController rc = new RoomController();
        for (Booking b : bookings) {
            Guest g = gc.getGuestById(b.getGuestId());
            Room r = rc.getRoomById(b.getRoomId());
            if (g != null && r != null) {
                long nights = ChronoUnit.DAYS.between(
                        b.getCheckInDate().toLocalDate(), b.getCheckOutDate().toLocalDate());
                bookingsModel.addRow(new Object[]{
                        b.getBookingId(), g.getName(), r.getRoomNumber(),
                        b.getCheckInDate(), b.getCheckOutDate(), nights,
                        b.getTotalAmount(), b.getStatus()
                });
            }
        }
    }

    private void filterBookings() {
        String status = (String) statusFilterCombo.getSelectedItem();
        String search = searchField.getText().trim().toLowerCase();

        List<Booking> filtered = new ArrayList<>();
        for (Booking b : allBookings) {
            boolean statusMatch = "All".equals(status) || status.equals(b.getStatus());
            boolean searchMatch = search.isEmpty() ||
                    String.valueOf(b.getBookingId()).contains(search) ||
                    b.getStatus().toLowerCase().contains(search);
            if (statusMatch && searchMatch) filtered.add(b);
        }
        refreshBookingsTable(filtered);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }
}
