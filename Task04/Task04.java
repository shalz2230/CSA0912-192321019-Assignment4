
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Task04 extends Frame {

    private ArrayList<Point> points = new ArrayList<>();
    private Label statusLabel;
    private DrawingPanel drawingPanel;

    public Task04() {

        setTitle("Event Handling Forensics - Drawing Pad");
        setSize(600, 450);
        setLayout(new BorderLayout());

        statusLabel = new Label("Click anywhere in the drawing area");

        drawingPanel = new DrawingPanel();
        drawingPanel.setBackground(Color.WHITE);

        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                points.add(e.getPoint());

                statusLabel.setText(
                    "Total dots drawn: " + points.size()
                );

                drawingPanel.repaint();
            }
        });

        add(drawingPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
    }

    class DrawingPanel extends Panel {

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            g.setColor(Color.BLUE);

            for (Point point : points) {
                g.fillOval(
                    point.x - 3,
                    point.y - 3,
                    6,
                    6
                );
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Task04 window = new Task04();
            window.setVisible(true);
        });
    }
}
