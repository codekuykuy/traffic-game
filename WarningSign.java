package car.test;

import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class WarningSign {

    private int x, y; // พิกัดในแผนที่
    private int size;
    private boolean active = false;
    private boolean finished = false;
    private boolean stopped = false;

    private static final int SPAWN_INTERVAL = 2000;
    private static final int FINISHED_DELAY = 1200;
    private static final Random rng = new Random();

    private static Timer spawnTimer;
    private static java.util.List<WarningSign> allSigns = new java.util.ArrayList<>();

    public static int score = 0;

    public WarningSign(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        allSigns.add(this);

        if (spawnTimer == null) {
            spawnTimer = new Timer(SPAWN_INTERVAL, e -> randomActivate());
            spawnTimer.start();
        }
    }

    private static void randomActivate() {
        // รีเซ็ตสถานะทั้งหมดก่อนสุ่มใหม่
        resetAll();

        // สุ่มเลือกบางจุดจากทั้งหมด
        java.util.List<WarningSign> shuffled = new java.util.ArrayList<>(allSigns);
        java.util.Collections.shuffle(shuffled, rng);

        int count = Math.min(5, shuffled.size()); // แสดงสูงสุด 3 จุดต่อรอบ
        for (int i = 0; i < count; i++) {
            WarningSign s = shuffled.get(i);
            double roll = rng.nextDouble();

            if (roll < 0.4) {
                s.active = true; // 40% เหลือง
            } else if (roll < 0.9) {
                s.finished = true; // 30% เขียว
            } else {
                s.stopNow(); // 30% แดง
            }
        }
    }

    public void stopNow() {
        active = false;
        finished = false;
        stopped = true;
    }

    private void handleClick(Point p) {
        Rectangle hit = new Rectangle(x - size / 2, y - size / 2, size, size);
        if (hit.contains(p) && active) {
            active = false;
            finished = true;
        }
    }

    public boolean clickScreen(Point p, double scale, int startX, int startY, int shiftX, int shiftY) {
        int sx = shiftX + (int) ((x - startX) * scale);
        int sy = shiftY + (int) ((y - startY) * scale);
        int d = (int) (size * scale);

        Rectangle hit = new Rectangle(sx - d / 2, sy - d / 2, d, d);

        if (!hit.contains(p)) {
            return false;
        }
        if (active) { // สีเหลือง
            active = false;
            score -= 1; // -1 คะแนน
            return true;
        } else if (finished) { // สีเขียว
            finished = false;
            score += 2; // +2 คะแนน
            return true;
        } else if (stopped) { // สีแดง
            stopped = false;
            score -= 3; // -3 คะแนน
            return true;
        }
        return false;
    }

    public void render(Graphics2D g2, double scale, int startX, int startY, int shiftX, int shiftY) {
        if (!active && !finished && !stopped) {
            return;
        }

        int sx = shiftX + (int) ((x - startX) * scale);
        int sy = shiftY + (int) ((y - startY) * scale);
        int d = (int) (size * scale);

        if (active) {
            g2.setColor(new Color(255, 220, 60));
            g2.fillOval(sx - d / 2, sy - d / 2, d, d);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(sx - d / 2, sy - d / 2, d, d);
            g2.setFont(new Font("Arial", Font.BOLD, (int) (d * 0.6)));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("!", sx - fm.stringWidth("!") / 2, sy + fm.getAscent() / 3);
        } else if (finished) {
            g2.setColor(new Color(60, 200, 100));
            g2.fillOval(sx - d / 2, sy - d / 2, d, d);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, (int) (d * 0.6)));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("GO", sx - fm.stringWidth("Go") / 2, sy + fm.getAscent() / 3);
        } else if (stopped) {
            g2.setColor(new Color(220, 40, 40)); // สีแดง
            g2.fillOval(sx - d / 2, sy - d / 2, d, d);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, (int) (d * 0.4)));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("STOP", sx - fm.stringWidth("STOP") / 2, sy + fm.getAscent() / 3);
        }

    }

    public static java.util.List<WarningSign> getAllSigns() {
        return allSigns;
    }

    private static void resetAll() {
        for (WarningSign s : allSigns) {
            s.active = false;
            s.finished = false;
            s.stopped = false;
        }
    }
}
