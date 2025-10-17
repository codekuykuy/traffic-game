package car.test;

import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class WarningSign {
    private int x, y; // พิกัดในแผนที่
    private int size;
    private boolean active = false;
    private boolean finished = false;
    private long finishedTime = 0;
    
    private static final int SPAWN_INTERVAL = 4000;
    private static final int FINISHED_DELAY = 1200;
    private static final Random rng = new Random();
    
    private static Timer spawnTimer;
    private static Timer cleanupTimer;
    private static java.util.List<WarningSign> allSigns = new java.util.ArrayList<>();

    public WarningSign(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        allSigns.add(this);
        
        if (spawnTimer == null) {
            spawnTimer = new Timer(SPAWN_INTERVAL, e -> randomActivate());
            spawnTimer.start();
        }
        
        if (cleanupTimer == null) {
            cleanupTimer = new Timer(200, e -> cleanUp());
            cleanupTimer.start();
        }
    }
    
    private static void randomActivate(){
        long activeCount = allSigns.stream().filter(s -> s.active).count();
        if (activeCount >= 3) return;
        
        java.util.List<WarningSign> idle = allSigns.stream().filter(s -> !s.active && !s.finished).toList();
        if (idle.isEmpty()) return;
        
        WarningSign s = idle.get(rng.nextInt(idle.size()));
        s.active = true;
    }
    
    private static void cleanUp(){
        long now = System.currentTimeMillis();
        for (WarningSign s : allSigns){
            if (s.finished && now - s.finishedTime >= FINISHED_DELAY){
                s.finished = false;
            }
        }
    }
    
    private void handleClick(Point p){
        Rectangle hit = new Rectangle(x - size / 2, y - size / 2, size, size);
        if (hit.contains(p) && active){
            active = false;
            finished = true;
            finishedTime = System.currentTimeMillis();
        }
    }
    
    public boolean clickScreen(Point p, double scale, int startX, int startY, int shiftX, int shiftY) {
        int sx = shiftX + (int) ((x - startX) * scale);
        int sy = shiftY + (int) ((y - startY) * scale);
        int d  = (int) (size * scale);
        
        Rectangle hit = new Rectangle(sx - d/2, sy - d/2, d, d);
        if (active && hit.contains(p)) {
            active = false;
            finished = true;
            finishedTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public void render(Graphics2D g2, double scale, int startX, int startY, int shiftX, int shiftY) {
        if (!active && !finished) return;
        
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
            g2.drawString("OK", sx - fm.stringWidth("OK") / 2, sy + fm.getAscent() / 3);
        }
    }
    
    public static java.util.List<WarningSign> getAllSigns(){
        return allSigns;
    }
}