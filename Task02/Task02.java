
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Task02 extends JFrame {

    private JTextField patientIdField;
    private JComboBox<String> departmentBox;
    private JComboBox<String> doctorBox;
    private JComboBox<String> timeBox;
    private JLabel statusLabel;

    private boolean bookingInProgress = false;

    private Map<String, String[]> doctors = new HashMap<>();

    public Task02() {

        setTitle("Hospital Appointment Desk");
        setSize(450, 350);
        setLayout(new GridLayout(6, 2, 10, 10));

        doctors.put("Cardiology",
                new String[]{"Dr. Kumar", "Dr. Priya"});
        doctors.put("Neurology",
                new String[]{"Dr. Arun", "Dr. Meena"});
        doctors.put("Orthopedics",
                new String[]{"Dr. Ravi", "Dr. Divya"});
        doctors.put("Dermatology",
                new String[]{"Dr. Suresh", "Dr. Anitha"});

        add(new JLabel("Patient ID:"));
        patientIdField = new JTextField();
        add(patientIdField);

        add(new JLabel("Department:"));
        departmentBox = new JComboBox<>(
                new String[]{"Cardiology", "Neurology",
                        "Orthopedics", "Dermatology"});
        add(departmentBox);

        add(new JLabel("Doctor:"));
        doctorBox = new JComboBox<>();
        add(doctorBox);

        add(new JLabel("Time Slot:"));
        timeBox = new JComboBox<>(
                new String[]{"09:00 AM", "10:00 AM",
                        "11:00 AM", "02:00 PM"});
        add(timeBox);

        JButton bookButton = new JButton("Book Appointment");
        add(bookButton);

        statusLabel = new JLabel("Select details and book appointment");
        add(statusLabel);

        departmentBox.addActionListener(e -> refreshDoctors());

        bookButton.addActionListener(e -> bookAppointment());

        refreshDoctors();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (bookingInProgress) {
                    int choice = JOptionPane.showConfirmDialog(
                            Task02.this,
                            "Booking is in progress. Do you want to exit?",
                            "Confirm Exit",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        dispose();
                    }
                } else {
                    dispose();
                }
            }
        });

        setLocationRelativeTo(null);
    }

    private void refreshDoctors() {
        String department =
                (String) departmentBox.getSelectedItem();

        doctorBox.removeAllItems();

        if (department != null) {
            for (String doctor : doctors.get(department)) {
                doctorBox.addItem(doctor);
            }
        }
    }

    private void bookAppointment() {

        String patientId = patientIdField.getText().trim();

        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter Patient ID");
            return;
        }

        String department =
                (String) departmentBox.getSelectedItem();

        String doctor =
                (String) doctorBox.getSelectedItem();

        String time =
                (String) timeBox.getSelectedItem();

        bookingInProgress = true;
        statusLabel.setText("Booking appointment...");

        SwingWorker<String, Void> worker =
                new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {
                Thread.sleep(1500);
                return "Appointment Booked Successfully!";
            }

            @Override
            protected void done() {
                try {
                    String result = get();

                    statusLabel.setText(result);
                    JOptionPane.showMessageDialog(
                            Task02.this,
                            result + "\nPatient ID: " + patientId
                            + "\nDepartment: " + department
                            + "\nDoctor: " + doctor
                            + "\nTime: " + time
                    );

                } catch (Exception ex) {
                    statusLabel.setText("Booking failed");
                    JOptionPane.showMessageDialog(
                            Task02.this, "Appointment booking failed");
                } finally {
                    bookingInProgress = false;
                }
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Task02().setVisible(true);
        });
    }
}
