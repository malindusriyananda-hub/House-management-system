package view;

import controller.GuestController;
import model.Guest;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;


public class GuestForm extends JPanel {

    private static final Color PRIMARY = new Color(27, 67, 50);
    private static final Color SECONDARY = new Color(74, 124, 89);
    private static final Color ACCENT = new Color(196, 154, 91);
    private static final Color BACKGROUND = new Color(250, 246, 238);
    private static final Color TEXT = new Color(35, 46, 33);
    private static final Color WHITE = Color.WHITE;

    private GuestController guestController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Guest> allGuests;

    public GuestForm() {
        guestController = new GuestController();
        allGuests = new ArrayList<>();
        initComponents();
        loadGuests();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 15));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(BACKGROUND);

        JLabel title = new JLabel("Guest Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        topPanel.add(title, BorderLayout.WEST);

        JButton addBtn = new JButton("+ Add Guest");
        addBtn.setBackground(ACCENT);
        addBtn.setForeground(TEXT);
        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> showGuestDialog(null));
        topPanel.add(addBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(BACKGROUND);
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchLabel.setForeground(TEXT);
        searchPanel.add(searchLabel);

        searchField = new JTextField(30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterGuests();
            }
        });
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        String[] columns = {"Guest ID", "Full Name", "NIC/Passport", "Phone", "Email", "Address"};
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
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) tableModel.getValueAt(row, 0);
                Guest g = guestController.getGuestById(id);
                if (g != null) showGuestDialog(g);
            }
        });

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(211, 47, 47));
        deleteBtn.setForeground(WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorder(new EmptyBorder(8, 25, 8, 25));
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) tableModel.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this guest?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String error = guestController.deleteGuest(id);
                    if (error != null) {
                        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        loadGuests();
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
                return c;
            }
        });
    }

    private void loadGuests() {
        allGuests = guestController.getAllGuests();
        refreshTable(allGuests);
    }

    private void filterGuests() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshTable(allGuests);
            return;
        }
        List<Guest> filtered = new ArrayList<>();
        for (Guest g : allGuests) {
            if (g.getName().toLowerCase().contains(keyword) ||
                (g.getPhone() != null && g.getPhone().contains(keyword)) ||
                (g.getEmail() != null && g.getEmail().toLowerCase().contains(keyword)) ||
                (g.getNic() != null && g.getNic().toLowerCase().contains(keyword))) {
                filtered.add(g);
            }
        }
        refreshTable(filtered);
    }

    private void refreshTable(List<Guest> guests) {
        tableModel.setRowCount(0);
        for (Guest g : guests) {
            tableModel.addRow(new Object[]{
                g.getGuestId(), g.getName(), g.getNic(), g.getPhone(), g.getEmail(), g.getAddress()
            });
        }
    }

    void showGuestDialog(Guest existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Add Guest" : "Edit Guest", true);
        dialog.setSize(450, 420);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(25);
        JTextField nicField = new JTextField(25);
        JTextField phoneField = new JTextField(25);
        JTextField emailField = new JTextField(25);
        JTextField addressField = new JTextField(25);

        JLabel nameError = new JLabel(" ");
        nameError.setForeground(new Color(211, 47, 47));
        nameError.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel phoneError = new JLabel(" ");
        phoneError.setForeground(new Color(211, 47, 47));
        phoneError.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel emailError = new JLabel(" ");
        emailError.setForeground(new Color(211, 47, 47));
        emailError.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        if (existing != null) {
            nameField.setText(existing.getName());
            nicField.setText(existing.getNic());
            phoneField.setText(existing.getPhone());
            emailField.setText(existing.getEmail());
            addressField.setText(existing.getAddress());
        }

        int row = 0;
        addDialogField(panel, gbc, "Full Name *:", nameField, row++);
        panel.add(nameError, gbc);
        gbc.gridy = row++;
        addDialogField(panel, gbc, "NIC/Passport:", nicField, row++);
        addDialogField(panel, gbc, "Phone *:", phoneField, row++);
        panel.add(phoneError, gbc);
        gbc.gridy = row++;
        addDialogField(panel, gbc, "Email *:", emailField, row++);
        panel.add(emailError, gbc);
        gbc.gridy = row++;
        addDialogField(panel, gbc, "Address:", addressField, row++);

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
            nameError.setText(" ");
            phoneError.setText(" ");
            emailError.setText(" ");

            String name = nameField.getText().trim();
            String nic = nicField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();

            boolean valid = true;

            if (name.isEmpty() || name.length() < 2) {
                nameError.setText("Name must be at least 2 characters");
                valid = false;
            }

            if (!phone.isEmpty() && (!phone.matches("\\d{10}")) ) {
                phoneError.setText("Phone must be exactly 10 digits");
                valid = false;
            }

            if (!email.isEmpty() && !Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
                emailError.setText("Invalid email format");
                valid = false;
            }

            if (!valid) return;

            Guest g = new Guest();
            if (existing != null) g.setGuestId(existing.getGuestId());
            g.setName(name);
            g.setNic(nic);
            g.setPhone(phone);
            g.setEmail(email);
            g.setAddress(address);

            String error;
            if (existing == null) {
                error = guestController.addGuest(g);
            } else {
                error = guestController.updateGuest(g);
            }

            if (error != null) {
                JOptionPane.showMessageDialog(dialog, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                dialog.dispose();
                loadGuests();
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

    private void addDialogField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
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
