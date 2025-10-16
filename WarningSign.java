package car.test;

import java.awt.*;

public class WarningSign {
    private int x, y; // พิกัดในแผนที่
    private int size;

    public WarningSign(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void render(Graphics2D g2d, double scale, int startX, int startY, int centerShiftX, int centerShiftY) {
        int drawX = centerShiftX + (int) ((x - startX) * scale);
        int drawY = centerShiftY + (int) ((y - startY) * scale);
        int drawSize = (int) (size * scale);

        // วงกลมเหลือง
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(drawX - drawSize/2, drawY - drawSize/2, drawSize, drawSize);

        // ขอบดำ
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke((float)(2 * scale)));
        g2d.drawOval(drawX - drawSize/2, drawY - drawSize/2, drawSize, drawSize);

        // เครื่องหมาย !
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, (int)(drawSize * 0.8)));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "!";
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2d.drawString(text, drawX - textWidth / 2, drawY + textHeight / 3);
    }
}
