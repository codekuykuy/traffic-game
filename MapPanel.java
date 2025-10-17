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

    private boolean zoomedIn = false;//warnning

    private final List<TrafficLightUnit> lights = new ArrayList<>();
    private final int roadWidth = 120;

    private List<WarningSign> warningSigns = new ArrayList<>();

    private GameTimer gameTimer;

    public MapPanel() {
        // โหลดภาพ
        mapImage = new ImageIcon(MapPanel.class.getResource("map.jpg")).getImage();
        //0
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

        //0-9
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

        // เม้ากลางซูม
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastMouse = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                handleClick(e.getPoint());
            }
        });

        // คลิกซ้ายค้างแล้วเลื่อน
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

            zoomedIn = zoom > 0.5;//warnning

            repaint();

        });
        gameTimer = new GameTimer(this::repaint);
        gameTimer.start();
    }

    // ล็อกไม่ให้เลื่อนเกิน map
    private void clampCamera() {
        if (mapImage == null) {
            return;
        }
        double maxX = mapImage.getWidth(null) - getWidth() / zoom;
        double maxY = mapImage.getHeight(null) - getHeight() / zoom;

        if (cameraX < 0) {
            cameraX = 0;
        }
        if (cameraY < 0) {
            cameraY = 0;
        }
        if (cameraX > maxX) {
            cameraX = maxX;
        }
        if (cameraY > maxY) {
            cameraY = maxY;
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapImage == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        AffineTransform oldTx = g2.getTransform();
        g2.scale(zoom, zoom);
        g2.translate(-cameraX, -cameraY);
        g2.drawImage(mapImage, 0, 0, null);

        g2.setTransform(oldTx);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int startX = (int) Math.round(cameraX);
        int startY = (int) Math.round(cameraY);
        int centerShiftX = 0;
        int centerShiftY = 0;

        if (zoomedIn) {
            for (TrafficLightUnit tl : lights) {
                tl.render(g2, zoom, startX, startY, centerShiftX, centerShiftY, roadWidth);
            }
        }

        if (!zoomedIn) { //warnning
            for (WarningSign sign : warningSigns) {
                sign.render(g2, zoom, (int) cameraX, (int) cameraY, centerShiftX, centerShiftY);
            }
        }

        int rectWidth = 180;
        int rectHeight = 60;
        int arc = 30; // ความโค้ง
        int margin = 20;
        int x = (getWidth() - rectWidth - margin);
        int y = margin;

        g2.setColor(new Color(0, 0, 0, 150)); // สีดำโปร่งแสง
        g2.fillRoundRect(x, y, rectWidth, rectHeight, arc, arc);

        g2.setColor(Color.WHITE);// timer right up
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        String timeText = gameTimer.getTimeString();
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
        int centerShiftX = 0;
        int centerShiftY = 0;

        for (TrafficLightUnit tl : lights) {
            Rectangle plusR = tl.getPlusButtonRect(zoom, startX, startY, centerShiftX, centerShiftY, roadWidth);
            Rectangle minusR = tl.getMinusButtonRect(zoom, startX, startY, centerShiftX, centerShiftY, roadWidth);

            if (plusR.contains(p)) {
                tl.adjustSeconds(+1);
                repaint();
                return;
            } else if (minusR.contains(p)) {
                tl.adjustSeconds(-1);
                repaint();
                return;
            }

        }
        if (!zoomedIn) {
            for (WarningSign sign : warningSigns) {
                if (sign.clickScreen(p, zoom, startX, startY, centerShiftX, centerShiftY)) {
                    repaint();
                    return;
                }
            }
        }
    }

}
