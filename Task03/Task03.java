
import java.awt.*;
import java.awt.event.*;

public class Task03 extends Frame {

    private TextField searchField;
    private Choice categoryChoice;
    private TextArea resultsArea;
    private Label statusLabel;

    private Button previousButton;
    private Button nextButton;
    private Button reserveButton;

    private int currentPage = 1;

    public Task03() {

        setTitle("Campus Library Search");
        setSize(700, 450);
        setLayout(new BorderLayout(10, 10));

        // TOP PANEL - Search controls
        Panel topPanel = new Panel(new FlowLayout(FlowLayout.LEFT));

        Label titleLabel = new Label("Campus Library Search");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        searchField = new TextField(20);

        categoryChoice = new Choice();
        categoryChoice.add("All Categories");
        categoryChoice.add("Programming");
        categoryChoice.add("Science");
        categoryChoice.add("Mathematics");

        Button searchButton = new Button("Search");

        topPanel.add(titleLabel);
        topPanel.add(searchField);
        topPanel.add(categoryChoice);
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // CENTER PANEL - Search results
        resultsArea = new TextArea(
                "Welcome to Campus Library!\nSearch for books to view results.",
                10, 50, TextArea.SCROLLBARS_VERTICAL_ONLY
        );

        resultsArea.setEditable(false);
        add(resultsArea, BorderLayout.CENTER);

        // BOTTOM PANEL - Navigation buttons
        Panel bottomPanel = new Panel(new BorderLayout());

        Panel buttonPanel = new Panel(new FlowLayout());

        previousButton = new Button("Previous");
        nextButton = new Button("Next");
        reserveButton = new Button("Reserve");

        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(reserveButton);

        statusLabel = new Label("Ready to search");

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Search event
        searchButton.addActionListener(e -> searchBooks());

        searchField.addActionListener(e -> searchBooks());

        previousButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                statusLabel.setText("Page: " + currentPage);
            }
        });

        nextButton.addActionListener(e -> {
            currentPage++;
            statusLabel.setText("Page: " + currentPage);
        });

        reserveButton.addActionListener(e -> {
            statusLabel.setText("Book reservation requested");
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
    }

    private void searchBooks() {

        String keyword = searchField.getText().trim();
        String category = categoryChoice.getSelectedItem();

        if (keyword.isEmpty()) {
            statusLabel.setText("Enter a book title to search");
            resultsArea.setText("Please enter a search keyword.");
            return;
        }

        currentPage = 1;

        resultsArea.setText(
                "LIBRARY SEARCH RESULTS\n\n"
                + "Search Keyword: " + keyword + "\n"
                + "Category: " + category + "\n\n"
                + "1. Java Programming\n"
                + "2. Object Oriented Programming\n"
                + "3. Advanced Java Concepts\n"
                + "4. Data Structures\n"
        );

        statusLabel.setText("Search completed - Page 1");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Task03 window = new Task03();
            window.setVisible(true);
        });
    }
}
