package car.test;
import javax.swing.*;

public class run {
    public static void main(String[] args) {
        JFrame f = new JFrame("");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new MapPanel());
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
