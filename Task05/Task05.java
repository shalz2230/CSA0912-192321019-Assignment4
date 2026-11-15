
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Task05 extends JFrame implements ActionListener {

    private JTextField amountField;
    private JComboBox<String> currencyBox;
    private JLabel resultLabel;

    private JButton interfaceButton;
    private JButton innerButton;
    private JButton anonymousButton;
    private JButton lambdaButton;

    public Task05() {

        setTitle("Currency Converter - Event Handling Styles");
        setSize(500, 350);
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel("Enter Amount (INR):"));

        amountField = new JTextField();
        add(amountField);

        add(new JLabel("Convert To:"));

        currencyBox = new JComboBox<>(
                new String[]{"USD", "EUR"}
        );
        add(currencyBox);

        interfaceButton = new JButton("Interface");
        innerButton = new JButton("Inner Class");
        anonymousButton = new JButton("Anonymous Class");
        lambdaButton = new JButton("Lambda");

        add(interfaceButton);
        add(innerButton);
        add(anonymousButton);
        add(lambdaButton);

        resultLabel = new JLabel("Converted amount will appear here");
        add(resultLabel);

        // Style 1: Implementing ActionListener
        interfaceButton.addActionListener(this);

        // Style 2: Named Inner Class
        innerButton.addActionListener(new InnerHandler());

        // Style 3: Anonymous Inner Class
        anonymousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });

        // Style 4: Lambda Expression
        lambdaButton.addActionListener(e -> convertCurrency());

        // Enter key also converts currency
        amountField.addActionListener(e -> convertCurrency());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
    }

    // Style 1 handler
    @Override
    public void actionPerformed(ActionEvent e) {
        convertCurrency();
    }

    // Style 2 named inner class
    class InnerHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            convertCurrency();
        }
    }

    private double rate(String currency) {
        if (currency.equals("USD")) {
            return 0.012;
        }
        return 0.011;
    }

    private void convertCurrency() {

        try {
            double amount = Double.parseDouble(
                    amountField.getText().trim()
            );

            if (amount < 0) {
                throw new NumberFormatException();
            }

            String currency =
                    (String) currencyBox.getSelectedItem();

            double converted = amount * rate(currency);

            String message = String.format(
                    "Converted Amount: %.2f %s",
                    converted, currency
            );

            resultLabel.setText(message);

            JOptionPane.showMessageDialog(
                    this, message,
                    "Conversion Result",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException ex) {

            resultLabel.setText("Invalid amount");

            JOptionPane.showMessageDialog(
                    this, "Invalid amount",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Task05().setVisible(true);
        });
    }
}
