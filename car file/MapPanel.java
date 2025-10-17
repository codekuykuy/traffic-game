package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.List;//------------------------------------------------
import java.util.ArrayList;//------------------------------------------------
import java.util.Map;
import java.util.HashMap;
import java.util.Random;


import project.Node;
import project.Path;
import project.TrafficLightUnit.Direction;

public class MapPanel extends JPanel {
    private Image mapImage;
    private double cameraX = 0, cameraY = 0, minZoom = 1.0, zoom = 1.0;
    private Point lastMouse;
    
    private boolean zoomedIn = false;//------------------------------------------------
    private int zoomX, zoomY; // พิกัดที่คลิก
    private double zoomScale = 5; // ระดับการซูม       
    private int zoomSize = 222; // ขนาดพื้นที่ที่ต้องการซูม (px)      

    private final List<TrafficLightUnit> lights = new ArrayList<>();
    private final int roadWidth = 300;
    
    private List<WarningSign> warnings = new ArrayList<>();//------------------------------------------------
    private GameTimer gameTimer;//---------------------------------------------------
    
    private static final Random random = new Random();
    private java.util.List<Car> cars = new java.util.ArrayList<>();
    private Car car;
    
    private List<Path> paths = new ArrayList<>();

    private Map<Node, List<Path>> nodeToPaths = new HashMap<>();
    private List<Path> spawnPaths = new ArrayList<>();

    public MapPanel() {
        // โหลดภาพ
        mapImage = new ImageIcon("assets/.map/map.png").getImage();
     
        paths = Path.getDefaultPaths();
        // ---- lanes spawn ----
        // Vertical main lanes
        spawnPaths.add(paths.get(0)); // Lane 1 (ซ้ายสุด, ขึ้น)
        spawnPaths.add(paths.get(3)); // Lane 2 (ขวา, ขึ้น)
        spawnPaths.add(paths.get(6)); // Lane 3 (ซ้าย, ลง)
        spawnPaths.add(paths.get(9)); // Lane 4 (ขวาสุด, ลง)
        // Horizontal main lanes
        spawnPaths.add(paths.get(13)); // Lane 1 (ซ้าย, ไปขวา)
        spawnPaths.add(paths.get(17)); // Lane 2 (ขวา, ไปซ้าย)
        
        for (Path p : paths) {
            Node end = p.getNode(p.length() - 1);
            for (Path q : paths) {
                Node start = q.getNode(0);
                if (end.equals(start)) {
                    p.addNextPath(q);
                }
            }
        }
        
        for (Path path : paths) {
            Node endNode = path.getNode(path.length() - 1);
            for (Path other : paths) {
                if (other == path) continue;
                Node startNode = other.getNode(0);
                if (startNode.equals(endNode)) {
                    path.addNextPath(other); // เชื่อมต่อกันได้
                }
            }
        }


        

        Timer timer = new Timer(30, e -> {
            for (Car c : cars) {
                c.update(cars);
                c.updateFrame();
            }
            repaint();
        });
        timer.start();
            
        int carCount = 100; // จำนวนรถทั้งหมดที่อยากให้มี
        int spawnInterval = 1000; // หน่วงเวลา 1 วินาทีต่อคัน (1000 ms)

        new javax.swing.Timer(spawnInterval, new ActionListener() {
            int spawned = 0; // นับจำนวนรถที่สร้างไปแล้ว
            @Override
            public void actionPerformed(ActionEvent e) {
                if (spawned >= carCount) {
                    ((javax.swing.Timer) e.getSource()).stop(); // หยุดสร้างเมื่อครบ
                    return;
                }
                Path randomPath = spawnPaths.get(random.nextInt(spawnPaths.size()));
                Car car = new Car(randomPath, nodeToPaths, spawnPaths);
                cars.add(car);
                spawned++;
            }
        }).start();



        // เพิ่ม Timer หลังสุ่มรถเสร็จ
        new javax.swing.Timer(16, e -> updateCars()).start();

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
        
        // คลิกซ้ายค้างแล้วเลื่อน
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
        
        // เมาส์กลาง
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
        
        warnings.add(new WarningSign(600, 100, 100));//------------------------------------------------

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
        
        AffineTransform oldTx = g2.getTransform();
        g2.scale(zoom, zoom);
        g2.translate(-cameraX, -cameraY);
        g2.drawImage(mapImage, 0, 0, null);
        
         // path check
//        g2.setColor(Color.RED); // สีแดงเพื่อเห็นชัด
//        g2.setStroke(new BasicStroke(2)); // หนา 2 px
//
//        for (Path path : paths) {
//            List<Node> nodes = path.getNodes();
//            for (int i = 0; i < nodes.size() - 1; i++) {
//                Node n1 = nodes.get(i);
//                Node n2 = nodes.get(i + 1);
//                g2.drawLine((int) n1.x, (int) n1.y, (int) n2.x, (int) n2.y);
//            }
//            // วาดจุด Node
//            for (Node n : nodes) {
//                int size = 20; // ขนาดจุด
//                g2.fillOval((int) n.x - size / 2, (int) n.y - size / 2, size, size);
//            }
//        }
        
        
         for (Car car : cars) {
            car.draw(g2);
         }   
         
        g2.setTransform(oldTx);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        int startX = (int) Math.round(cameraX);
        int startY = (int) Math.round(cameraY);
        int centerShiftX = 0;
        int centerShiftY = 0;

        for (TrafficLightUnit tl : lights) {
            tl.render(g2, zoom, startX, startY, centerShiftX, centerShiftY, roadWidth);
        }

        if (!zoomedIn) {//--------------------------------------------
            for (WarningSign sign : warnings) {
                sign.render(g2, 1.0, 0, 0, 0, 0);
            }
        }

        g2.setTransform(new AffineTransform());

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
          
    private void updateCars() {
        for (Car car : cars) {
            car.updatePosition();
            car.updateFrame();
        }
        repaint(); // วาดใหม่ทุก frame
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
    }
}
