package car.test;

import static car.test.WarningSign.score;
import java.awt.*;
import javax.swing.*;

public class EndAndToxicBar extends JComponent {

    private double pollution = 0.0;     // เริ่มจาก 0%
    private int stagesDone = 0;
    private final int totalStages = score;

    private Timer timer; // Timer สำหรับเพิ่มค่าอัตโนมัติ

    public EndAndToxicBar() {
        setOpaque(false);
        setPreferredSize(new Dimension(220, 150));

        addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsAdapter() {
            @Override
            public void ancestorResized(java.awt.event.HierarchyEvent e) {
                updateSizeByParent();
            }
        });

        // สร้าง Timer ให้เพิ่มทุก 5 วินาที (5000 ms)
        timer = new Timer(5000, e -> increaseBar());
        timer.start();
    }

    private void updateSizeByParent() {
        Container parent = getParent();
        if (parent != null) {
            int parentW = parent.getWidth();
            int newW = Math.max(150, (int) (parentW * 0.2));
            setBounds(16, 16, newW, 120);
            revalidate();
            repaint();
        }
    }

    // ===== เมธอดควบคุม =====
    private void increaseBar() {
        // เพิ่ม pollution
        pollution = Math.min(1.0, pollution + 0.0278);
        repaint();
    }

    public void resetBar() {
        pollution = 0.0;
        stagesDone = 0;
        repaint();
    }

    public void setToxicProgress(double v) {
        pollution = Math.max(0.0, Math.min(1.0, v));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int W = getWidth(), H = getHeight();
        int margin = 12, barW = W - margin * 2, barH = 24, r = 12;

        int y1 = 34;
        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        g2.drawString("Life", margin, y1 - 8);
        g2.setColor(new Color(187, 191, 250));
        g2.fillRoundRect(margin, y1, barW, barH, r, r);
        int redW = (int) (barW * pollution);
        g2.setColor(new Color(230, 0, 0));
        g2.fillRoundRect(margin + 8, y1 + 5, Math.max(0, redW - 16), barH - 10, r, r);

        int y2 = y1 + 52;
        g2.setColor(new Color(187, 191, 250));
        g2.fillRoundRect(margin, y2, barW, barH, r, r);

// ข้อความอยู่ "ในกรอบ"
        g2.setColor(Color.BLACK);
        FontMetrics fm = g2.getFontMetrics();
        String text = "Score = " + WarningSign.score;
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

// จัดให้อยู่กึ่งกลางกรอบแนวนอนและแนวตั้ง
        int textX = margin + (barW - textWidth) / 2;
        int textY = y2 + (barH + textHeight) / 2 - 3;

        g2.drawString(text, textX, textY);

        g2.dispose();
    }

    public int getStagesDone() {
        return stagesDone;
    }

    public int getTotalStages() {
        return totalStages;
    }

    public double getPollution() {
        return pollution;
    } // 0.0 .. 1.0

// ===== คะแนนตามแถบ End Game ล้วน (0..100) =====
    public int getScorePercentSimple() {
        if (totalStages <= 0) {
            return 0;
        }
        return (int) Math.round(100.0 * (stagesDone / (double) totalStages));
    }

// ===== คะแนนแบบผสม: End Game * (1 - Toxic) (0..100) =====
    public int getScorePercentWithToxic() {
        if (totalStages <= 0) {
            return 0;
        }
        double endPart = (stagesDone / (double) totalStages); // ความคืบหน้าตามด่าน
        double clean = 1.0 - pollution;                       // สะอาดยิ่งได้มาก
        return (int) Math.round(100.0 * endPart * clean);
    }
}
