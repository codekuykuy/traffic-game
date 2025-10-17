package car.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.AffineTransform;

import car.test.TrafficLightUnit.Direction;

public class MapPanel extends JPanel {

    private Image mapImage;
    private double zoom = 1.0;
    private double minZoom = 1.0;
    private double cameraX = 0;
    private double cameraY = 0;
    private Point lastMouse;

    private boolean zoomedIn = false;

    private final List<TrafficLightUnit> lights = new ArrayList<>();
    private final int roadWidth = 120;

    private final List<WarningSign> warningSigns = new ArrayList<>();

    private GameTimer gameTimer;

    // ===== เพิ่มให้เป็น field =====
    private EndAndToxicBar hud;
    private JLayeredPane centerLayer;
    private boolean gameOver = false;
    private boolean warned = false;

    public MapPanel() {
        setLayout(new BorderLayout());

        // โหลดภาพ
        mapImage = new ImageIcon(MapPanel.class.getResource("/car/Image/map.jpg")).getImage();

        // ---------- สร้างเลเยอร์กลาง + HUD ----------
        centerLayer = new JLayeredPane();
        centerLayer.setLayout(null);
        add(centerLayer, BorderLayout.CENTER);

        hud = new EndAndToxicBar();
        hud.setBounds(16, 16, 220, 150);
        centerLayer.add(hud, JLayeredPane.DRAG_LAYER);

        // ปุ่ม BACK
        JButton backButton = new JButton("BACK");
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(49, 33, 90));
        backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        backButton.setBounds(940, 600, 120, 50);
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backButton.setBackground(new Color(80, 50, 130));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backButton.setBackground(new Color(49, 33, 90));
            }
        });
        backButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(MapPanel.this);
            frame.dispose();
            new Menu();
        });
        centerLayer.add(backButton, JLayeredPane.PALETTE_LAYER);
        // ---------------------------------------------

        // ไฟจราจร (ของเดิม)
        lights.add(new TrafficLightUnit(350, 2085, Direction.RIGHT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(625, 2085, Direction.LEFT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(495, 2225, Direction.UP, 15, 5, 15, this::repaint));
        //1
        lights.add(new TrafficLightUnit(1750, 2930, Direction.LEFT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1450, 2930, Direction.RIGHT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1603, 3060, Direction.UP, 15, 5, 15, this::repaint));
        //2
        lights.add(new TrafficLightUnit(4000, 2930, Direction.LEFT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(3700, 2930, Direction.RIGHT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(3865, 3060, Direction.UP, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(3865, 2800, Direction.DOWN, 15, 5, 15, this::repaint));
        //3
        lights.add(new TrafficLightUnit(4000, 2085, Direction.LEFT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(3700, 2085, Direction.RIGHT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(3865, 2225, Direction.UP, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(3865, 1950, Direction.DOWN, 15, 5, 15, this::repaint));
        //4
        lights.add(new TrafficLightUnit(1850, 2035, Direction.UP, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1850, 2140, Direction.LEFT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1300, 2035, Direction.RIGHT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1300, 2140, Direction.DOWN, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1700, 1875, Direction.LEFT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1825, 1875, Direction.DOWN, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1350, 2250, Direction.UP, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1475, 2250, Direction.RIGHT, 15, 5, 15, this::repaint));
        //5
        lights.add(new TrafficLightUnit(495, 2875, Direction.RIGHT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(495, 2975, Direction.DOWN, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1000, 2875, Direction.UP, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1000, 2975, Direction.LEFT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(875, 2700, Direction.LEFT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(1000, 2700, Direction.DOWN, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(510, 3090, Direction.UP, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(640, 3090, Direction.RIGHT, 15, 5, 15, this::repaint));
        //6
        lights.add(new TrafficLightUnit(2350, 2085, Direction.RIGHT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(3000, 2085, Direction.LEFT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(2585, 2300, Direction.UP, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(2790, 1885, Direction.DOWN, 15, 5, 15, this::repaint));
        //7
        lights.add(new TrafficLightUnit(2350, 2925, Direction.RIGHT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(3000, 2925, Direction.LEFT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(2585, 3140, Direction.UP, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(2790, 2725, Direction.DOWN, 15, 5, 15, this::repaint));
        //8
        lights.add(new TrafficLightUnit(3865, 1100, Direction.RIGHT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(3700, 825, Direction.RIGHT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(3700, 925, Direction.DOWN, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(4000, 975, Direction.LEFT, 15, 5, 15, this::repaint));
        //9
        lights.add(new TrafficLightUnit(2790, 675, Direction.DOWN, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(2590, 1260, Direction.UP, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(2425, 1220, Direction.UPRIGHT, 15, 5, 15, this::repaint));
        lights.add(new TrafficLightUnit(3000, 780, Direction.LEFT, 15, 5, 15, this::repaint));

        lights.forEach(TrafficLightUnit::start);

        // Warning จุดต่าง ๆ
        warningSigns.add(new WarningSign(490, 2110, 300));
        warningSigns.add(new WarningSign(1601, 2973, 300));
        warningSigns.add(new WarningSign(3857, 2930, 300));
        warningSigns.add(new WarningSign(3857, 2086, 300));
        warningSigns.add(new WarningSign(1581, 2075, 300));
        warningSigns.add(new WarningSign(751, 2910, 400));
        warningSigns.add(new WarningSign(2681, 2089, 400));
        warningSigns.add(new WarningSign(2681, 2928, 400));
        warningSigns.add(new WarningSign(3816, 956, 400));
        warningSigns.add(new WarningSign(2701, 983, 600));

        SwingUtilities.invokeLater(() -> {
            if (mapImage == null || mapImage.getWidth(null) <= 0) {
                System.err.println("❌ Map Image loading failed. Please check the path again.");
                return;
            }
            int imgW = mapImage.getWidth(null);
            int imgH = mapImage.getHeight(null);

            double zoomX = (double) getPreferredSize().width / imgW;
            double zoomY = (double) getPreferredSize().height / imgH;
            zoom = minZoom = Math.min(zoomX, zoomY);

            cameraX = (imgW - getPreferredSize().width / zoom) / 2.0;
            cameraY = (imgH - getPreferredSize().height / zoom) / 2.0;

            repaint();
        });

        // เมาส์
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastMouse = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                handleClick(e.getPoint());
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouse.x;
                int dy = e.getY() - lastMouse.y;
                cameraX -= dx / zoom;
                cameraY -= dy / zoom;
                lastMouse = e.getPoint();
                clampCamera();
                repaint();
            }
        });
        addMouseWheelListener(e -> {
            double delta = e.getPreciseWheelRotation();
            double zoomFactor = 1.1;
            double oldZoom = zoom;

            if (delta < 0) {
                zoom *= zoomFactor;
            } else {
                zoom /= zoomFactor;
            }
            zoom = Math.max(minZoom, Math.min(5.0, zoom));

            Point p = e.getPoint();
            double px = (p.x / oldZoom) + cameraX;
            double py = (p.y / oldZoom) + cameraY;

            cameraX = px - (p.x / zoom);
            cameraY = py - (p.y / zoom);

            clampCamera();
            zoomedIn = zoom > 0.5;
            repaint();
        });

        // ===== Timer: อัปเดต Toxic ตาม "เวลาถอยหลัง 3 นาที" =====
        gameTimer = new GameTimer(() -> {
            int remain = gameTimer.getRemainingSeconds();         // เวลาเหลือ
            double progress = 1.0 - (remain / 180.0);             // 0..1
            hud.setToxicProgress(progress);                       // <<<< ตรงนี้ครับ

            if (!gameOver) {
                if (remain == 10 && !warned) {
                    warnAlmostTimeUp();
                }
                if (remain <= 0) {
                    handleTimeout();
                }
            }
            repaint();
        });
        gameTimer.start();
    }

    // ล็อกไม่ให้เลื่อนเกิน map
    private void clampCamera() {
        if (mapImage == null) {
            return;
        }
        double maxX = mapImage.getWidth(null) - getWidth() / zoom;
        double maxY = mapImage.getHeight(null) - getHeight() / zoom;

        cameraX = Math.max(0, Math.min(cameraX, maxX));
        cameraY = Math.max(0, Math.min(cameraY, maxY));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapImage == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        AffineTransform oldTx = g2.getTransform();
        g2.scale(zoom, zoom);
        g2.translate(-cameraX, -cameraY);
        g2.drawImage(mapImage, 0, 0, null);

        g2.setTransform(oldTx);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int startX = (int) Math.round(cameraX);
        int startY = (int) Math.round(cameraY);

        if (zoomedIn) {
            for (TrafficLightUnit tl : lights) {
                tl.render(g2, zoom, startX, startY, 0, 0, roadWidth);
            }
        } else {
            for (WarningSign sign : warningSigns) {
                sign.render(g2, zoom, (int) cameraX, (int) cameraY, 0, 0);
            }
        }

        // กล่องเวลา
        int rectWidth = 180, rectHeight = 60, arc = 30, margin = 20;
        int x = (getWidth() - rectWidth - margin), y = margin;
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(x, y, rectWidth, rectHeight, arc, arc);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        String timeText = gameTimer.getTimeString(); // แสดง mm:ss ของ "เวลาถอยหลัง"
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (rectWidth - fm.stringWidth(timeText)) / 2;
        int ty = y + ((rectHeight - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(timeText, tx, ty);

        g2.dispose();
    }

    public Dimension getPreferredSize() {
        return new Dimension(1110, 690);
    }

    private void handleClick(Point p) {
        int startX = (int) Math.round(cameraX);
        int startY = (int) Math.round(cameraY);

        // ปุ่ม +/-
        for (TrafficLightUnit tl : lights) {
            Rectangle plusR = tl.getPlusButtonRect(zoom, startX, startY, 0, 0, roadWidth);
            Rectangle minusR = tl.getMinusButtonRect(zoom, startX, startY, 0, 0, roadWidth);

            if (plusR.contains(p)) {
                tl.adjustSeconds(+1);
                repaint();
                return;
            }
            if (minusR.contains(p)) {
                tl.adjustSeconds(-1);
                repaint();
                return;
            }
        }

        // คลิกแก้ Warning
        if (!zoomedIn) {
            for (WarningSign sign : warningSigns) {
                if (sign.clickScreen(p, zoom, startX, startY, 0, 0)) {
                    repaint();
                    return;
                }
            }
        }
    }

    // ====== เตือน/หมดเวลา ======
    private void warnAlmostTimeUp() {
        if (warned || gameOver) {
            return;
        }
        warned = true;
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(
                this, "เหลือเวลา 10 วินาที!", "ใกล้หมดเวลา", JOptionPane.WARNING_MESSAGE
        );
    }

    private void handleTimeout() {
        if (gameOver) {
            return;
        }
        gameOver = true;

        lights.forEach(TrafficLightUnit::stop);
        gameTimer.stop();
        hud.setToxicProgress(1.0);

        Toolkit.getDefaultToolkit().beep();
        Toolkit.getDefaultToolkit().beep();

        // ===== เลือกสูตรคะแนนที่คุณต้องการ =====
        // แบบที่ 1: ตามแถบ End Game ล้วน
        int score = hud.getScorePercentSimple();

        // แบบที่ 2 (ถ้าอยากคิด Toxic ร่วมด้วย): 
        // int score = hud.getScorePercentWithToxic();
        JOptionPane.showMessageDialog(
                this,
                "หมดเวลา 3 นาที — เกมจบ\nคะแนนของคุณ: " + score + " คะแนน",
                "Game Over",
                JOptionPane.ERROR_MESSAGE
        );

        repaint();
    }
}
