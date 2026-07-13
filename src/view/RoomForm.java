package view;

import controller.RoomController;
import model.Room;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;


public class RoomForm extends JPanel {

    private static final Color PRIMARY = new Color(27, 67, 50);
    private static final Color SECONDARY = new Color(74, 124, 89);
    private static final Color ACCENT = new Color(196, 154, 91);
    private static final Color BACKGROUND = new Color(250, 246, 238);
    private static final Color TEXT = new Color(35, 46, 33);
    private static final Color WHITE = Color.WHITE;

    private RoomController roomController;
    private JTable table;
    private DefaultTableModel tableModel;

    public RoomForm() {
        roomController = new RoomController();
        initComponents();
        loadRooms();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 15));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(BACKGROUND);

        JLabel title = new JLabel("Room Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        topPanel.add(title, BorderLayout.WEST);

        JButton addBtn = new JButton("+ Add Room");
        addBtn.setBackground(ACCENT);
        addBtn.setForeground(TEXT);
        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> showRoomDialog(null));
        topPanel.add(addBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Room ID", "Room Number", "Room Type", "Price Per Night (LKR)", "Status", "Max Guests"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(BACKGROUND);

        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(SECONDARY);
        editBtn.setForeground(WHITE);
        editBtn.setFocusPainted(false);
        editBtn.setBorder(new EmptyBorder(8, 25, 8, 25));
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        editBtn.setEnabled(false);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(211, 47, 47));
        deleteBtn.setForeground(WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorder(new EmptyBorder(8, 25, 8, 25));
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteBtn.setEnabled(false);

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) tableModel.getValueAt(row, 0);
                Room r = roomController.getRoomById(id);
                if (r != null) showRoomDialog(r);
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) tableModel.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this room?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String error = roomController.deleteRoom(id);
                    if (error != null) {
                        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        loadRooms();
                    }
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = table.getSelectedRow() >= 0;
            editBtn.setEnabled(selected);
            deleteBtn.setEnabled(selected);
        });

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
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
                if (column == 3 && value instanceof BigDecimal) {
                    setText("LKR " + NumberFormat.getNumberInstance().format(value));
                }
                if (column == 4 && value != null) {
                    String status = value.toString();
                    if ("AVAILABLE".equals(status)) {
                        setForeground(new Color(46, 125, 50));
                    } else if ("OCCUPIED".equals(status)) {
                        setForeground(new Color(211, 47, 47));
                    } else if ("MAINTENANCE".equals(status)) {
                        setForeground(new Color(255, 143, 0));
                    }
                }
                return c;
            }
        });
    }

    private void loadRooms() {
        List<Room> rooms = roomController.getAllRooms();
        tableModel.setRowCount(0);
        for (Room r : rooms) {
            tableModel.addRow(new Object[]{
                r.getRoomId(), r.getRoomNumber(), r.getRoomType(),
                r.getPricePerNight(), r.getStatus(), r.getMaxGuests()
            });
        }
    }

    private void showRoomDialog(Room existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Add Room" : "Edit Room", true);
        dialog.setSize(420, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField roomNumberField = new JTextField(25);
        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{"SINGLE", "DOUBLE", "SUITE", "DELUXE"});
        JFormattedTextField priceField = new JFormattedTextField(NumberFormat.getNumberInstance());
        JSpinner maxGuestsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE"});

        JLabel numberError = new JLabel(" ");
        numberError.setForeground(new Color(211, 47, 47));
        numberError.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel priceError = new JLabel(" ");
        priceError.setForeground(new Color(211, 47, 47));
        priceError.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        if (existing != null) {
            roomNumberField.setText(existing.getRoomNumber());
            roomTypeCombo.setSelectedItem(existing.getRoomType());
            priceField.setValue(existing.getPricePerNight());
            maxGuestsSpinner.setValue(existing.getMaxGuests());
            statusCombo.setSelectedItem(existing.getStatus());
        }

        int row = 0;
        addField(panel, gbc, "Room Number *:", roomNumberField, row++);
        panel.add(numberError, gbc);
        gbc.gridy = row++;
        addField(panel, gbc, "Room Type:", roomTypeCombo, row++);
        addField(panel, gbc, "Price Per Night *:", priceField, row++);
        panel.add(priceError, gbc);
        gbc.gridy = row++;
        addField(panel, gbc, "Max Guests:", maxGuestsSpinner, row++);
        addField(panel, gbc, "Status:", statusCombo, row++);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(WHITE);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(TEXT);
        cancelBtn.setForeground(WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(new EmptyBorder(8, 20, 8, 20));

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(ACCENT);
        saveBtn.setForeground(TEXT);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        saveBtn.addActionListener(e -> {
            numberError.setText(" ");
            priceError.setText(" ");

            String roomNumber = roomNumberField.getText().trim();
            String roomType = (String) roomTypeCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            int maxGuests = (int) maxGuestsSpinner.getValue();

            boolean valid = true;

            if (roomNumber.isEmpty()) {
                numberError.setText("Room number is required");
                valid = false;
            }

            BigDecimal price = BigDecimal.ZERO;
            try {
                price = new BigDecimal(priceField.getText().replace(",", "").trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    priceError.setText("Price must be a positive number");
                    valid = false;
                }
            } catch (Exception ex) {
                priceError.setText("Invalid price format");
                valid = false;
            }

            if (!valid) return;

            Room r = new Room();
            if (existing != null) r.setRoomId(existing.getRoomId());
            r.setRoomNumber(roomNumber);
            r.setRoomType(roomType);
            r.setPricePerNight(price);
            r.setStatus(status);
            r.setMaxGuests(maxGuests);

            String error;
            if (existing == null) {
                error = roomController.addRoom(r);
            } else {
                error = roomController.updateRoom(r);
            }

            if (error != null) {
                JOptionPane.showMessageDialog(dialog, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                dialog.dispose();
                loadRooms();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
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
