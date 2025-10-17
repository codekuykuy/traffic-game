package javamenu;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Menu extends JFrame {

    private final JPanel root = new JPanel(new CardLayout());

    // โหลดฟอนต์กดปุ่มครั้งเดียว ใช้ซ้ำ
    private final Font pressFont = loadPressFont(18f);

    public void showCard(String name) {
        ((CardLayout) root.getLayout()).show(root, name);
    }

    public Menu() {
        setTitle("Traffic Simulator");
        setSize(1100, 690);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(166, 166, 166));
        setLocationRelativeTo(null);

        setContentPane(root);

        JPanel menuPage = new JPanel(new BorderLayout());
        menuPage.setBackground(new Color(166, 166, 166));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        // ===== Title =====
        JLabel title = new JLabel("TRAFFIC SIMULATOR", SwingConstants.CENTER);
        title.setFont(loadPressFont(56f));
        title.setBorder(new EmptyBorder(50, 0, 10, 0));
        titlePanel.add(title, BorderLayout.CENTER);
        menuPage.add(titlePanel, BorderLayout.NORTH);

        // ===== Button Panel =====
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        JPanel buttonCol = new JPanel(new GridLayout(3, 1, 0, 20));
        buttonCol.setOpaque(false);
        buttonCol.setBorder(new EmptyBorder(10, 0, 40, 0));

        JButton BtnPlay = createPixelButton("PLAY");
        JButton BtnTutorial = createPixelButton("TUTORIAL");
        JButton BtnCredit = createPixelButton("CREDIT");

        BtnPlay.addActionListener(e -> {
            System.out.println("Go to Play!!");
        });

        BtnTutorial.addActionListener(e -> {
            System.out.println("Go To Tutoria!!l");
            root.add(new Tutorial(this), "tutorial");
            showCard("tutorial");
        });

        BtnCredit.addActionListener(e -> {
            System.out.println("Go To Credit!!");
            root.add(new Credit(this), "credit");
            showCard("credit");
        });

        buttonCol.add(BtnPlay);
        buttonCol.add(BtnTutorial);
        buttonCol.add(BtnCredit);
        buttonPanel.add(buttonCol, new GridBagConstraints());

        // ===== LayeredPane + Images =====
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setOpaque(false);
        layeredPane.setLayout(null);

        ImageIcon carIcon = loadIcon("/Image/Car.png");
        ImageIcon lightIcon = loadIcon("/Image/light.png");

        ImageIcon carScaled = scaledIcon(carIcon, 332, 120);
        ImageIcon lightScaled = scaledIcon(lightIcon, 150, 415);

        JLabel CarLabel = new JLabel(carScaled);
        JLabel LightLabel = new JLabel(lightScaled);

        layeredPane.add(LightLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(CarLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER);

        // จัดวางตำแหน่งตามขนาดหน้าต่าง
        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = layeredPane.getWidth();
                int h = layeredPane.getHeight();

                buttonPanel.setBounds(0, 0, w, h);

                int lw = LightLabel.getPreferredSize().width;
                int lh = LightLabel.getPreferredSize().height;
                LightLabel.setBounds(120, Math.max(80, h - lh + 1), lw, lh);

                int cw = CarLabel.getPreferredSize().width;
                int ch = CarLabel.getPreferredSize().height;
                CarLabel.setBounds(w - cw - 50, h - ch - 40, cw, ch);
            }
        });

        menuPage.add(layeredPane, BorderLayout.CENTER);

        root.add(menuPage, "menu");

        setVisible(true);
    }

    private static ImageIcon loadIcon(String path) {
        URL url = Menu.class.getResource(path);
        if (url == null) {
            String msg = "ไม่พบ resource: " + path + "  (ตรวจพาธ+การแพ็กลง JAR)";
            System.err.println(msg);
            BufferedImage blank = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            return new ImageIcon(blank);
        }
        return new ImageIcon(url);
    }

    private static ImageIcon scaledIcon(ImageIcon src, int w, int h) {
        Image scaled = src.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /* ==== ฟอนต์ PressStart2P จาก resource ==== */
    private Font loadPressFont(float size) {
        try (InputStream is = getClass().getResourceAsStream("/Font/PressStart2P-vaV7.ttf")) {
            if (is != null) {
                Font base = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base);
                return base.deriveFont(size);
            }
        } catch (Exception e) {
            System.err.println("⚠️ โหลดฟอนต์ไม่สำเร็จ ใช้ Monospaced แทน (" + e + ")");
        }
        return new Font(Font.MONOSPACED, Font.BOLD, Math.round(size));
    }

    /* ==== ปุ่มสไตล์ Pixel (ใช้ฟอนต์ที่โหลด) ==== */
    private JButton createPixelButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(pressFont); // ใช้ฟอนต์ที่โหลดจาก resource
        btn.setBackground(new Color(49, 33, 90));
        btn.setPreferredSize(new Dimension(240, 80));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Menu::new);
    }
}
