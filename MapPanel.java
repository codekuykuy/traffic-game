package car.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;//------------------------------------------------
import java.util.ArrayList;//------------------------------------------------

public class MapPanel extends JPanel {

    private Image mapImage;
    private double zoom = 1.0;
    private double minZoom = 1.0;
    private double cameraX = 0;
    private double cameraY = 0;
    private Point lastMouse;

    private boolean zoomedIn = false;//------------------------------------------------
    private int zoomX, zoomY; // พิกัดที่คลิก
    private double zoomScale = 5; // ระดับการซูม       
    private int zoomSize = 222; // ขนาดพื้นที่ที่ต้องการซูม (px)      

    private List<WarningSign> warnings = new ArrayList<>();//------------------------------------------------
    private GameTimer gameTimer;//---------------------------------------------------

    public MapPanel() {
        // โหลดภาพ
        setPreferredSize(new Dimension(1110, 690));
        mapImage = new ImageIcon("assets/Map/map.png").getImage();

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

            zoomedIn = zoom > 0.5;//---------------------------------------------

            Point p = e.getPoint();
            double px = (p.x / oldZoom) + cameraX;
            double py = (p.y / oldZoom) + cameraY;

            cameraX = px - (p.x / zoom);
            cameraY = py - (p.y / zoom);

            clampCamera();
            repaint();
        });

        warnings.add(new WarningSign(900, 1100, 150));//------------------------------------------------
        warnings.add(new WarningSign(600, 100, 150));//------------------------------------------------

        gameTimer = new GameTimer(this::repaint);//------------------------------------------------
        gameTimer.start();//------------------------------------------------
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

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2.scale(zoom, zoom);
        g2.translate(-cameraX, -cameraY);
        g2.drawImage(mapImage, 0, 0, null);

        if (!zoomedIn) {//--------------------------------------------
            for (WarningSign sign : warnings) {
                sign.render(g2, 1.0, 0, 0, 0, 0);
            }
        }

        int rectWidth = 1000;
        int rectHeight = 250;
        int arc = 200; // ความโค้ง
        int margin = 80;
        int x = getWidth() - rectWidth - margin;
        int y = 10000;

        g2.setColor(new Color(0, 0, 0, 150)); // สีดำโปร่งแสง
        g2.fillRoundRect(x, y, rectWidth, rectHeight, arc, arc);

        g2.setColor(Color.WHITE);// timer right up
        g2.setFont(new Font("Arial", Font.BOLD, 150));
        String timeText = gameTimer.getTimeString();
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (rectWidth - fm.stringWidth(timeText)) / 2;
        int ty = y + ((rectHeight - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(timeText, tx, ty);
    }

    public Dimension getPreferredSize() {
        return new Dimension(1110, 690);
    }
}
