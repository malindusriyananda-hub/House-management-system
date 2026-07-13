package view;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import controller.GuestController;
import controller.RoomController;
import controller.BookingController;
import controller.PaymentController;
import db.DBConnection;
import model.Guest;
import model.Room;
import model.Booking;

public class Dashboard extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());

    // Colors
    private static final Color PRIMARY = new Color(27, 67, 50);
    private static final Color SECONDARY = new Color(74, 124, 89);
    private static final Color ACCENT = new Color(196, 154, 91);
    private static final Color BACKGROUND = new Color(250, 246, 238);
    private static final Color TEXT = new Color(35, 46, 33);
    private static final Color WHITE = Color.WHITE;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel homePanel;
    private GuestController guestController;
    private RoomController roomController;
    private BookingController bookingController;
    private PaymentController paymentController;
    private JTable todayCheckInsTable;
    private DefaultTableModel todayCheckInsModel;
    private GuestForm guestForm;

    public Dashboard() {
        guestController = new GuestController();
        roomController = new RoomController();
        bookingController = new BookingController();
        paymentController = new PaymentController();
        initComponents();
        setupDashboard();
    }

    private void setupDashboard() {
        setTitle("The Wren House - Reservation Manager");
        setSize(1366, 768);
        setMinimumSize(new Dimension(1200, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND);

        homePanel = createHomePanel();
        guestForm = new GuestForm();
        RoomForm roomForm = new RoomForm();
        BookingForm bookingForm = new BookingForm();
        PaymentForm paymentForm = new PaymentForm();

        contentPanel.add(homePanel, "HOME");
        contentPanel.add(guestForm, "GUESTS");
        contentPanel.add(roomForm, "ROOMS");
        contentPanel.add(bookingForm, "BOOKINGS");
        contentPanel.add(paymentForm, "PAYMENTS");

        JPanel sidebar = createSidebar();
        JPanel topBar = createTopBar();

        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY);
        topBar.setPreferredSize(new Dimension(0, 50));

        JLabel titleLabel = new JLabel("  The Wren House");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        titleLabel.setForeground(WHITE);
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton exitButton = new JButton("EXIT");
        exitButton.setBackground(new Color(150, 46, 38));
        exitButton.setForeground(WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(new EmptyBorder(8, 20, 8, 20));
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exitButton.addActionListener(e -> System.exit(0));
        topBar.add(exitButton, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(35, 46, 33));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new GridLayout(6, 1, 0, 2));
        sidebar.setBorder(new EmptyBorder(10, 0, 10, 0));

        String[] labels = {"Dashboard", "Guests", "Rooms", "Bookings", "Payments"};
        String[] cardNames = {"HOME", "GUESTS", "ROOMS", "BOOKINGS", "PAYMENTS"};
        Color[] colors = {PRIMARY, SECONDARY, SECONDARY, SECONDARY, SECONDARY};

        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            JButton btn = new JButton(labels[i]);
            btn.setBackground(colors[i]);
            btn.setForeground(WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(new EmptyBorder(10, 20, 10, 20));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                if (index == 0) refreshHomePanel();
                cardLayout.show(contentPanel, cardNames[index]);
            });
            sidebar.add(btn);
        }

        return sidebar;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        statsPanel.setBackground(BACKGROUND);
        statsPanel.setPreferredSize(new Dimension(0, 120));

        statsPanel.add(createStatCard("Total Guests", "0", "guests"));
        statsPanel.add(createStatCard("Total Rooms", "0", "rooms"));
        statsPanel.add(createStatCard("Available Rooms", "0", "available"));
        statsPanel.add(createStatCard("Total Bookings", "0", "bookings"));
        statsPanel.add(createStatCard("Today's Check-ins", "0", "checkins"));

        panel.add(statsPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(BACKGROUND);

        JPanel tableHeaderPanel = new JPanel(new BorderLayout(15, 0));
        tableHeaderPanel.setBackground(BACKGROUND);

        JLabel tableTitle = new JLabel("Today's Check-ins");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(TEXT);
        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);

        JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        quickActionsPanel.setBackground(BACKGROUND);

        JButton addGuestBtn = new JButton("+ Add Guest");
        addGuestBtn.setBackground(ACCENT);
        addGuestBtn.setForeground(TEXT);
        addGuestBtn.setFocusPainted(false);
        addGuestBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addGuestBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        addGuestBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addGuestBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "GUESTS");
            guestForm.showGuestDialog(null);
        });
        quickActionsPanel.add(addGuestBtn);

        JButton generateReportBtn = new JButton("Generate Report");
        generateReportBtn.setBackground(SECONDARY);
        generateReportBtn.setForeground(WHITE);
        generateReportBtn.setFocusPainted(false);
        generateReportBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        generateReportBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        generateReportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateReportBtn.addActionListener(e -> showGenerateReportDialog());
        quickActionsPanel.add(generateReportBtn);

        tableHeaderPanel.add(quickActionsPanel, BorderLayout.EAST);
        tablePanel.add(tableHeaderPanel, BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Guest Name", "Room Number", "Room Type", "Check-in", "Check-out", "Status"};
        todayCheckInsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        todayCheckInsTable = new JTable(todayCheckInsModel);
        styleTable(todayCheckInsTable);

        JScrollPane scrollPane = new JScrollPane(todayCheckInsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.setBackground(BACKGROUND);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(SECONDARY);
        refreshBtn.setForeground(WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        refreshBtn.addActionListener(e -> refreshHomePanel());
        refreshPanel.add(refreshBtn);
        tablePanel.add(refreshPanel, BorderLayout.SOUTH);

        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, String type) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 15, 20, 15)
        ));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(PRIMARY);
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT);
        card.add(titleLabel, BorderLayout.SOUTH);

        card.putClientProperty("type", type);
        card.putClientProperty("valueLabel", valueLabel);

        return card;
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
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? WHITE : new Color(227, 242, 253));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    public void refreshHomePanel() {
        try {
            int totalGuests = guestController.getTotalGuests();
            int totalRooms = roomController.getTotalRooms();
            int availableRooms = roomController.getAvailableRoomCount();
            int totalBookings = bookingController.getTotalBookings();
            int todaysCheckIns = bookingController.getTodaysCheckIns();

            Component[] cards = ((JPanel) homePanel.getComponent(0)).getComponents();
            for (Component c : cards) {
                if (c instanceof JPanel) {
                    JPanel card = (JPanel) c;
                    String type = (String) card.getClientProperty("type");
                    JLabel valLabel = (JLabel) card.getClientProperty("valueLabel");
                    if ("guests".equals(type)) {
                        valLabel.setText(String.valueOf(totalGuests));
                    } else if ("rooms".equals(type)) {
                        valLabel.setText(String.valueOf(totalRooms));
                    } else if ("available".equals(type)) {
                        valLabel.setText(String.valueOf(availableRooms));
                    } else if ("bookings".equals(type)) {
                        valLabel.setText(String.valueOf(totalBookings));
                    } else if ("checkins".equals(type)) {
                        valLabel.setText(String.valueOf(todaysCheckIns));
                    }
                }
            }

            todayCheckInsModel.setRowCount(0);
            List<Booking> bookings = bookingController.getAllBookings();
            GuestController gc = new GuestController();
            RoomController rc = new RoomController();
            for (Booking b : bookings) {
                if ("CONFIRMED".equals(b.getStatus()) || "CHECKED_IN".equals(b.getStatus())) {
                    Guest g = gc.getGuestById(b.getGuestId());
                    Room r = rc.getRoomById(b.getRoomId());
                    if (g != null && r != null) {
                        todayCheckInsModel.addRow(new Object[]{
                                b.getBookingId(), g.getName(), r.getRoomNumber(), r.getRoomType(),
                                b.getCheckInDate(), b.getCheckOutDate(), b.getStatus()
                        });
                    }
                }
            }
        } catch (Exception e) {
            logger.warning("Error refreshing dashboard: " + e.getMessage());
        }
    }

    private void showGenerateReportDialog() {
        JDialog dialog = new JDialog(this, "Generate Report", true);
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

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(1366, 768));
        setMinimumSize(new Dimension(1200, 700));
    }
}
