package project;
import javax.swing.*;

public class run {
    public static void main(String[] args) {
        JFrame f = new JFrame("");
        f.setDefaultCloseOperation(3);
        f.setResizable(false);
        MapPanel map = new MapPanel();
        f.add(map);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
