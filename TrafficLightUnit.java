package projectjava;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class TrafficLightUnit{
    
    public enum Direction {UP, LEFT, RIGHT, DOWN, UPRIGHT}
    public enum Phase {RED, YELLOW, GREEN}

    private int mapX;
    private int mapY;
    private Direction dir;
    
    private int redSec;
    private int yellowSec;
    private int greenSec;
    private Phase phase = Phase.RED;
    private int secondsLeft;
    
    private final Timer tick;
    private final Runnable repaintRequest;
    
    public TrafficLightUnit(int mapX, int mapY, Direction dir, int redSec, int yellowSec, int greenSec, Runnable repaintRequest){
        this.mapX = mapX;
        this.mapY = mapY;
        this.dir = dir;
        this.redSec = Math.max(1, redSec);
        this.yellowSec = Math.max(1, yellowSec);
        this.greenSec = Math.max(1, greenSec);
        this.secondsLeft = this.redSec;
        this.repaintRequest = repaintRequest;
        
        this.tick = new Timer(1000, e-> {
            if (secondsLeft > 0) secondsLeft--;
            if (secondsLeft == 0) nextPhase();
            repaintRequest.run();
        });
    }
    
    public void start(){
        if (!tick.isRunning()) tick.start();
    }
    
    public void stop(){ 
        tick.stop(); 
    }
    
    public void adjustSeconds(int delta){
        secondsLeft = Math.max(0, secondsLeft + delta);
    }
    
    public void setPosition(int x, int y){
        this.mapX = x;
        this.mapY = y;
    }
    
    public int getMapX(){ 
        return mapX;
    }
    
    public int getMapY(){ 
        return mapY; 
    }
    
    private void nextPhase(){
        switch (phase){
            case RED -> phase = Phase.GREEN;
            case YELLOW -> phase = Phase.RED;
            case GREEN  -> phase = Phase.YELLOW;
        }
        secondsLeft = secondsFor(phase);
    }
    
    private int secondsFor(Phase p){
        return switch (p) {
            case RED -> redSec;
            case YELLOW -> yellowSec;
            case GREEN  -> greenSec;
        };
    }
    
    private Color colorFor(Phase p) {
        return switch (p) {
            case RED -> new Color(210, 40, 40);
            case YELLOW -> new Color(230, 185, 20);
            case GREEN  -> new Color(40, 170, 80);
        };
    }
    
    public Rectangle getPlusButtonRect(double scale, int startX, int startY, int centerShiftX, int centerShiftY, int roadWidth){
        int sx = centerShiftX + (int) ((mapX - startX) * scale);
        int sy = centerShiftY + (int) ((mapY - startY) * scale);
        int d = (int) Math.max(6, Math.round(roadWidth * 0.5 * scale));
        int bw = (int) (d * 0.6);
        int bh = (int) (d * 0.6);
        return new Rectangle(sx + d/2 + 6, sy - d/2, bw, bh);
    }
    
    public Rectangle getMinusButtonRect(double scale, int startX, int startY, int centerShiftX, int centerShiftY, int roadWidth){
        int sx = centerShiftX + (int) ((mapX - startX) * scale);
        int sy = centerShiftY + (int) ((mapY - startY) * scale);
        int d = (int) Math.max(6, Math.round(roadWidth * 0.5 * scale));
        int bw = (int)(d * 0.6);
        int bh = (int)(d * 0.6);
        return new Rectangle(sx + d/2 + 6, sy, bw, bh);
    }
    
    public void render(Graphics2D g2, double scale, int startX, int startY, int centerShiftX, int centerShiftY, int roadWidth){
        int sx = centerShiftX + (int) ((mapX - startX) * scale);
        int sy = centerShiftY + (int) ((mapY - startY) * scale);
        int d = (int) Math.max(6, Math.round(roadWidth * 0.9 * scale));
        
        int cx = sx - d/2;
        int cy = sy - d/2;
        g2.setColor(new Color(24, 24, 24));
        g2.fillOval(cx, cy, d, d);
        g2.setColor(new Color(0, 0, 0, 110));
        g2.setStroke(new BasicStroke(Math.max(1f, (float)(1.5 * scale))));
        g2.drawOval(cx, cy, d, d);
        
        g2.setColor(colorFor(phase));
        Shape arrow = createArrowShape(sx, sy, d);
        AffineTransform at = new AffineTransform();
        double angle = switch (dir) {
            case UP -> 0;
            case LEFT  -> -Math.PI / 2;
            case RIGHT ->  Math.PI / 2;
            case DOWN -> Math.PI;
            case UPRIGHT -> Math.PI / 4;
        };
        at.rotate(angle, sx, sy);
        g2.fill(at.createTransformedShape(arrow));
        
        String txt = String.valueOf(secondsLeft);
        Font f = g2.getFont().deriveFont((float)Math.max(8, d * 0.30));
        g2.setFont(f);
        FontMetrics fm = g2.getFontMetrics();
        int tx = sx - fm.stringWidth(txt)/2;
        int ty = sy + (fm.getAscent() - fm.getDescent())/2;
        g2.setColor(Color.WHITE);
        g2.drawString(txt, tx, ty);
        
        Rectangle plusR = getPlusButtonRect(scale, startX, startY, centerShiftX, centerShiftY, roadWidth);
        Rectangle minusR = getMinusButtonRect(scale, startX, startY, centerShiftX, centerShiftY, roadWidth);
        
        g2.setColor(new Color(60, 60, 60));
        g2.fill(plusR);
        g2.fill(minusR);
        
        g2.setColor(Color.WHITE);
        Font f2 = g2.getFont().deriveFont((float) Math.max(6, d * 0.35));
        g2.setFont(f2);
        FontMetrics fm2 = g2.getFontMetrics();
        int pX = plusR.x + (plusR.width  - fm2.stringWidth("+")) / 2;
        int pY = plusR.y + (plusR.height + fm2.getAscent() - fm2.getDescent()) / 2;
        int mX = minusR.x + (minusR.width - fm2.stringWidth("−")) / 2;
        int mY = minusR.y + (minusR.height + fm2.getAscent() - fm2.getDescent()) / 2;
        g2.drawString("+", pX, pY);
        g2.drawString("−", mX, mY);
    }
    
    private Shape createArrowShape(int sx, int sy, int d) {
        int arrowW = Math.max(4, d / 6);
        int arrowH = Math.max(10, d / 2);
        
        Polygon shaft = new Polygon();
        shaft.addPoint(sx - arrowW/2, sy + arrowH/3);
        shaft.addPoint(sx + arrowW/2, sy + arrowH/3);
        shaft.addPoint(sx + arrowW/2, sy - arrowH/4);
        shaft.addPoint(sx - arrowW/2, sy - arrowH/4);
        
        Polygon head = new Polygon();
        head.addPoint(sx, sy - arrowH/2);
        head.addPoint(sx - arrowW, sy - arrowH/4);
        head.addPoint(sx + arrowW, sy - arrowH/4);
        
        java.awt.geom.Area area = new java.awt.geom.Area(shaft);
        area.add(new java.awt.geom.Area(head));
        return area;
    }
}