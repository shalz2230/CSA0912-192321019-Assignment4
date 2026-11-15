
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class FeeService {

    public String payFee(String regNo, String amountText) {
        try {
            if (regNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Enter Registration Number");
            }

            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero");
            }

            // Payment logic is handled separately from the GUI.
            return "Payment Successful!\nRegistration Number: "
                    + regNo + "\nAmount Paid: Rs." + amount;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a valid amount");
        }
    }
}

public class Task01 extends JFrame {

    private JTextField regNoField;
    private JTextField amountField;
    private String lastReceipt = "";

    private FeeService feeService = new FeeService();

    public Task01() {
        setTitle("Student Fee Payment");
        setSize(400, 280);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Registration Number:"));
        regNoField = new JTextField();
        add(regNoField);

        add(new JLabel("Fee Amount:"));
        amountField = new JTextField();
        add(amountField);

        JButton payButton = new JButton("Pay");
        JButton clearButton = new JButton("Clear");
        JButton receiptButton = new JButton("Receipt");

        add(payButton);
        add(clearButton);
        add(receiptButton);
        add(new JLabel(""));

        payButton.addActionListener(e -> {
            try {
                lastReceipt = feeService.payFee(
                        regNoField.getText(),
                        amountField.getText()
                );

                JOptionPane.showMessageDialog(this, lastReceipt);

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this, ex.getMessage(),
                        "Payment Error", JOptionPane.ERROR_MESSAGE
                );
            }
        });

        clearButton.addActionListener(e -> {
            regNoField.setText("");
            amountField.setText("");
            lastReceipt = "";
        });

        receiptButton.addActionListener(e -> {
            if (lastReceipt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No payment receipt available");
            } else {
                JOptionPane.showMessageDialog(this, lastReceipt, "Receipt",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Task01().setVisible(true));
    }
}
