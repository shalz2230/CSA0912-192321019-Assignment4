
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;

public class Task06 extends JFrame {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/campus_events";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "your_password";

    private JTextField studentIdField;
    private JTextField studentNameField;
    private JComboBox<String> eventBox;
    private JComboBox<String> filterBox;

    private JTable table;
    private DefaultTableModel model;
    private JLabel statusLabel;

    private final String[] events = {
        "Tech Fest",
        "Cultural Fest",
        "Sports Meet",
        "Coding Contest"
    };

    public Task06() {

        setTitle("Campus Event Registration System");
        setSize(850, 550);
        setLayout(new BorderLayout());

        createMenu();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Student Registration", createRegistrationPanel());
        tabs.addTab("Committee Dashboard", createDashboardPanel());

        add(tabs, BorderLayout.CENTER);

        statusLabel = new JLabel("Ready");
        add(statusLabel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        Task06.this,
                        "Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        setLocationRelativeTo(null);
        loadRegistrations();
    }

    private void createMenu() {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem exportItem = new JMenuItem("Export CSV");
        JMenuItem refreshItem = new JMenuItem("Refresh");
        JMenuItem exitItem = new JMenuItem("Exit");

        exportItem.addActionListener(e -> exportCSV());
        refreshItem.addActionListener(e -> loadRegistrations());
        exitItem.addActionListener(e -> dispose());

        fileMenu.add(exportItem);
        fileMenu.add(refreshItem);
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private JPanel createRegistrationPanel() {

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        studentIdField = new JTextField();
        studentNameField = new JTextField();

        eventBox = new JComboBox<>(events);

        JButton registerButton = new JButton("Register");

        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);

        panel.add(new JLabel("Student Name:"));
        panel.add(studentNameField);

        panel.add(new JLabel("Select Event:"));
        panel.add(eventBox);

        panel.add(new JLabel(""));
        panel.add(registerButton);

        registerButton.addActionListener(e -> registerStudent());

        return panel;
    }

    private JPanel createDashboardPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterBox = new JComboBox<>();
        filterBox.addItem("All Events");

        for (String event : events) {
            filterBox.addItem(event);
        }

        JButton filterButton = new JButton("Filter");
        JButton refreshButton = new JButton("Refresh");
        JButton exportButton = new JButton("Export CSV");

        filterPanel.add(new JLabel("Filter by Event:"));
        filterPanel.add(filterBox);
        filterPanel.add(filterButton);
        filterPanel.add(refreshButton);
        filterPanel.add(exportButton);

        model = new DefaultTableModel(
                new String[]{"ID", "Student ID", "Student Name",
                        "Event Name", "Registration Date"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        filterButton.addActionListener(e -> filterRegistrations());
        refreshButton.addActionListener(e -> loadRegistrations());
        exportButton.addActionListener(e -> exportCSV());

        return panel;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DB_URL, DB_USER, DB_PASSWORD
        );
    }

    private void registerStudent() {

        String studentId = studentIdField.getText().trim();
        String studentName = studentNameField.getText().trim();
        String eventName = (String) eventBox.getSelectedItem();

        if (studentId.isEmpty() || studentName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "All fields are required",
                    "Validation Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!studentId.matches("[A-Za-z0-9_-]+")) {
            JOptionPane.showMessageDialog(
                    this, "Invalid Student ID"
            );
            return;
        }

        if (!studentName.matches("[A-Za-z ]+")) {
            JOptionPane.showMessageDialog(
                    this, "Enter a valid student name"
            );
            return;
        }

        setBusy(true);

        SwingWorker<String, Void> worker =
                new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {

                String sql = "INSERT INTO registrations "
                        + "(student_id, student_name, event_name) "
                        + "VALUES (?, ?, ?)";

                try (Connection con = getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setString(1, studentId);
                    ps.setString(2, studentName);
                    ps.setString(3, eventName);

                    ps.executeUpdate();

                    return "Registration successful!";
                }
            }

            @Override
            protected void done() {

                try {
                    JOptionPane.showMessageDialog(
                            Task06.this, get()
                    );

                    studentIdField.setText("");
                    studentNameField.setText("");

                    loadRegistrations();

                } catch (Exception ex) {

                    Throwable cause = ex.getCause();

                    if (cause instanceof SQLException) {
                        SQLException sqlEx = (SQLException) cause;

                        if (sqlEx.getSQLState() != null
                                && sqlEx.getSQLState().startsWith("23")) {
                            JOptionPane.showMessageDialog(
                                    Task06.this,
                                    "Duplicate registration! "
                                    + "Student already registered for this event.",
                                    "Duplicate Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        } else {
                            showDatabaseError(sqlEx);
                        }
                    } else {
                        showDatabaseError(ex);
                    }

                } finally {
                    setBusy(false);
                }
            }
        };

        worker.execute();
    }

    private void loadRegistrations() {

        if (model == null) {
            return;
        }

        loadData(null);
    }

    private void filterRegistrations() {

        String selectedEvent =
                (String) filterBox.getSelectedItem();

        if ("All Events".equals(selectedEvent)) {
            loadData(null);
        } else {
            loadData(selectedEvent);
        }
    }

    private void loadData(String eventName) {

        setBusy(true);

        SwingWorker<Object[][], Void> worker =
                new SwingWorker<Object[][], Void>() {

            @Override
            protected Object[][] doInBackground() throws Exception {

                String sql = "SELECT id, student_id, student_name, "
                        + "event_name, registration_date "
                        + "FROM registrations";

                if (eventName != null) {
                    sql += " WHERE event_name = ?";
                }

                sql += " ORDER BY id DESC";

                try (Connection con = getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    if (eventName != null) {
                        ps.setString(1, eventName);
                    }

                    try (ResultSet rs = ps.executeQuery()) {

                        java.util.List<Object[]> rows =
                                new java.util.ArrayList<>();

                        while (rs.next()) {
                            rows.add(new Object[]{
                                rs.getInt("id"),
                                rs.getString("student_id"),
                                rs.getString("student_name"),
                                rs.getString("event_name"),
                                rs.getTimestamp("registration_date")
                            });
                        }

                        return rows.toArray(new Object[0][]);
                    }
                }
            }

            @Override
            protected void done() {

                try {
                    Object[][] rows = get();

                    model.setRowCount(0);

                    for (Object[] row : rows) {
                        model.addRow(row);
                    }

                    statusLabel.setText(
                            "Total registrations: " + rows.length
                    );

                } catch (Exception ex) {
                    showDatabaseError(ex);
                } finally {
                    setBusy(false);
                }
            }
        };

        worker.execute();
    }

    private void exportCSV() {

        if (model == null || model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this, "No registration data to export"
            );
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(
                new File("event_registrations.csv")
        );

        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(file))) {

            for (int col = 0; col < model.getColumnCount(); col++) {
                writer.print(model.getColumnName(col));

                if (col < model.getColumnCount() - 1) {
                    writer.print(",");
                }
            }

            writer.println();

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {

                    String value =
                            String.valueOf(model.getValueAt(row, col));

                    writer.print("\""
                            + value.replace("\"", "\"\"") + "\"");

                    if (col < model.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }

                writer.println();
            }

            JOptionPane.showMessageDialog(
                    this, "CSV exported successfully!"
            );

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this, "Export failed: " + ex.getMessage()
            );
        }
    }

    private void setBusy(boolean busy) {

        statusLabel.setText(
                busy ? "Please wait... Processing database request"
                     : "Ready"
        );
    }

    private void showDatabaseError(Exception ex) {

        JOptionPane.showMessageDialog(
                this,
                "Database operation failed.\n"
                + ex.getMessage()
                + "\nCheck MySQL connection and database setup.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new Task06().setVisible(true);
        });
    }
}
