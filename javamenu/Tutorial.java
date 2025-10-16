package javamenu;

import java.awt.*;
import javax.swing.*;
import java.net.URL;
import java.awt.image.BufferedImage;

public class Tutorial extends JPanel {

    private final Menu host;

    // ===== ตั้งค่ารูป =====
    private final String[] imagePaths = {
        "/Tutorial/Tutorial1.png",
        "/Tutorial/Tutorial1.png",
        "/Tutorial/Tutorial1.png",
        "/Tutorial/Tutorial1.png",
        "/Tutorial/Tutorial1.png"
    };
    private final Image[] originals = new Image[imagePaths.length];
    private int index = 0;

    // ===== UI =====
    private final JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
    private final JButton btnBack = createPixelButton("BACK");
    private final JButton btnNext = createPixelButton("NEXT");

    public Tutorial(Menu host) {
        this.host = host;

        setLayout(new BorderLayout());
        setBackground(new Color(166, 166, 166));

        for (int i = 0; i < imagePaths.length; i++) {
            URL url = getClass().getResource(imagePaths[i]);   
            if (url != null) {
                originals[i] = new ImageIcon(url).getImage();
            } else {
                System.err.println("ไม่พบรูป: " + imagePaths[i]);
                originals[i] = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
            }
        }

        JLayeredPane centerLayer = new JLayeredPane();
        centerLayer.setLayout(null);
        add(centerLayer, BorderLayout.CENTER);

        imageLabel.setOpaque(false);
        centerLayer.add(imageLabel, JLayeredPane.DEFAULT_LAYER);

        // ปุ่ม
        centerLayer.add(btnBack, JLayeredPane.PALETTE_LAYER);
        centerLayer.add(btnNext, JLayeredPane.PALETTE_LAYER);

        btnNext.addActionListener(e -> {
            if (index < originals.length - 1) {
                index++;
                refresh(centerLayer);
                System.out.println("index = " + index);
            } else {
                host.showCard("menu");
            }
        });

        btnBack.addActionListener(e -> {
            if (index > 0) {
                index--;
                refresh(centerLayer);
                System.out.println("index = " + index);
            } else {
                host.showCard("menu");
            }
        });

        centerLayer.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                refresh(centerLayer);
            }
        });

        refresh(centerLayer);
    }

    private void refresh(JLayeredPane layer) {
        int w = layer.getWidth();
        int h = layer.getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }

        int btnH = Math.max(btnBack.getPreferredSize().height, btnNext.getPreferredSize().height);
        int margin = 24;

        int boxW = Math.max(1, w - margin * 2);
        int boxH = Math.max(1, h - (btnH + margin * 3));

        Image scaled = scaleToFit(originals[index], boxW, boxH, 0.9);
        ImageIcon icon = new ImageIcon(scaled);
        imageLabel.setIcon(icon);

        int imgW = icon.getIconWidth();
        int imgH = icon.getIconHeight();
        int x = (w - imgW) / 2;
        int y = (h - btnH - margin) / 2 - imgH / 2;
        y = Math.max(margin, y);
        imageLabel.setBounds(x, y, imgW, imgH);

        Dimension f = btnBack.getPreferredSize();
        Dimension l = btnNext.getPreferredSize();
        btnBack.setBounds(margin, h - f.height - margin, f.width, f.height);
        btnNext.setBounds(w - l.width - margin, h - l.height - margin, l.width, l.height);

        btnBack.setText(index == 0 ? "BACK" : "BACK");
        btnNext.setText(index == originals.length - 1 ? "PLAY" : "NEXT");
    }

    private Image scaleToFit(Image img, int boxW, int boxH, double scaleFactor) {
        int iw = img.getWidth(null), ih = img.getHeight(null);
        if (iw <= 0 || ih <= 0) {
            return img;
        }

        double s = Math.min(boxW / (double) iw, boxH / (double) ih);
        s *= scaleFactor;

        int w = Math.max(1, (int) Math.round(iw * s));
        int h = Math.max(1, (int) Math.round(ih * s));

        return img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }

    private JButton createPixelButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Press Start 2P", Font.PLAIN, 18));
        btn.setBackground(new Color(49, 33, 90));
        btn.setPreferredSize(new Dimension(170, 57));
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(80, 50, 130));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(49, 33, 90));
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(20, 10, 60));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(80, 50, 130));
            }
        });
        return btn;
    }
}
