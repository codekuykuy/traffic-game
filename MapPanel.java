package projectjava;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.AffineTransform;

import projectjava.TrafficLightUnit.Direction;

public class MapPanel extends JPanel {
    private Image mapImage;
    private double zoom = 1.0;
    private double minZoom = 1.0;
    private double cameraX = 0;
    private double cameraY = 0;
    private Point lastMouse;
    
    private final List<TrafficLightUnit> lights = new ArrayList<>();
    private final int roadWidth = 100;
    
    

    public MapPanel() {
        // โหลดภาพ
        mapImage = new ImageIcon(MapPanel.class.getResource("/projectjava/map.png")).getImage();
        
        lights.add(new TrafficLightUnit( 400, 2100, Direction.UP,    6, 2, 6, this::repaint));
        lights.add(new TrafficLightUnit( 800, 500, Direction.LEFT,  5, 2, 5, this::repaint));
        lights.add(new TrafficLightUnit(1200, 350, Direction.RIGHT, 7, 2, 7, this::repaint));
        lights.forEach(TrafficLightUnit::start);

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
            
            public void mouseReleased(MouseEvent e){
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
            repaint();
        });
    }

    // ล็อกไม่ให้เลื่อนเกิน map
    private void clampCamera() {
        if (mapImage == null) return;
        double maxX = mapImage.getWidth(null) - getWidth() / zoom;
        double maxY = mapImage.getHeight(null) - getHeight() / zoom;

        if (cameraX < 0) cameraX = 0;
        if (cameraY < 0) cameraY = 0;
        if (cameraX > maxX) cameraX = maxX;
        if (cameraY > maxY) cameraY = maxY;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapImage == null) return;

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
        
        for (TrafficLightUnit tl : lights){
            tl.render(g2, zoom, startX, startY, centerShiftX, centerShiftY, roadWidth);
        }
        
        g2.dispose();
    }

    public Dimension getPreferredSize() {
        return new Dimension(1110, 690);
    }
    
    private void handleClick(Point p){
        int startX = (int) Math.round(cameraX);
        int startY = (int) Math.round(cameraY);
        int centerShiftX = 0;
        int centerShiftY = 0;
        
        for(TrafficLightUnit tl : lights){
            Rectangle plusR = tl.getPlusButtonRect(zoom, startX, startY, centerShiftX, centerShiftY, roadWidth);
            Rectangle minusR = tl.getMinusButtonRect(zoom, startX, startY, centerShiftX, centerShiftY, roadWidth);
            
            if (plusR.contains(p)){
                tl.adjustSeconds(+1);
                repaint();
                return;
            }
            else if (minusR.contains(p)){
                tl.adjustSeconds(-1);
                repaint();
                return;
            }
        }
    }
}