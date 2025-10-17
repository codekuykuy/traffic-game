package javamenu;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Credit extends JPanel {


    private final Font pressFont;   
    private final Font mitr;      

    static class Member {
        private final String name;
        private final String id;
        private final String imagePath;

        Member(String name, String id, String imagePath) {
            this.name = name;
            this.id = id;
            this.imagePath = imagePath;
        }
        String name()      { return name; }
        String id()        { return id; }
        String imagePath() { return imagePath; }
    }

    private final Member[] members = {
        new Member("นายณกรณ์ เวชยัณต์", "6730200103", "/Image/Person1.jpg"),
        new Member("นางสาวธาราทิพย์ บัวดำ", "6730200197", "/Image/Person2.jpg"),
        new Member("นายพรพล วงศ์ปัญจพล", "6730200260", "/Image/Person3.jpg"),
        new Member("นายพีรพัฒน์ ฉัตรสิริโชค", "6730200286", "/Image/Person4.jpg"),
        new Member("นายอนุภัทร สมบัติศรี", "6730200375", "/Image/Person5.jpg")
    };

    private JPanel createCard(Member m) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(250, 220));

        ImageIcon avatar = scaledIcon(loadIcon(m.imagePath()), 160, 160);

        JLabel pic = new JLabel(avatar);
        pic.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel(m.name(), SwingConstants.CENTER);
        name.setFont(mitr);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel id = new JLabel(m.id(), SwingConstants.CENTER);
        id.setFont(mitr);
        id.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(pic);
        card.add(name);
        card.add(id);
        return card;
    }

    public Credit(Menu host) {

        // โหลดฟอนต์ต่าง ๆ
        this.pressFont = loadPressFont(18f);                     // สำหรับปุ่ม
        this.mitr    = loadMitrFont("/Font/Mitr-Regular.ttf", 20f);

        setLayout(new BorderLayout());
        setBackground(new Color(166, 166, 166));

        // ===== Title (บนสุด) =====
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("CREDIT", SwingConstants.CENTER);
        // ถ้าอยากใช้ Mitr แทน PressStart2P ให้เปลี่ยนบรรทัดถัดไปเป็น: title.setFont(mitr.deriveFont(56f));
        title.setFont(loadPressFont(56f));
        title.setBorder(new EmptyBorder(50, 0, 10, 0));
        titlePanel.add(title, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // ===== พื้นที่การ์ด: ใช้ FlowLayout ให้ wrap อัตโนมัติ =====
        JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 12));
        flow.setOpaque(false);
        for (Member m : members) {
            flow.add(createCard(m));
        }
        add(flow, BorderLayout.CENTER);

        // ===== ปุ่ม BACK วาง SOUTH =====
        JButton BtnBack = createPixelButton("BACK");
        BtnBack.addActionListener(e -> host.showCard("menu"));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        south.setOpaque(false);
        south.setBorder(new EmptyBorder(0, 20, 20, 20));
        south.add(BtnBack);
        add(south, BorderLayout.SOUTH);
    }

    /* ===== ยูทิลโหลดรูปจาก classpath แบบกันพัง ===== */
    private static ImageIcon loadIcon(String path) {
        // path ต้องขึ้นต้นด้วย "/"
        URL url = Credit.class.getResource(path);
        if (url == null) {
            String msg = "ไม่พบ resource: " + path + "  (ตรวจพาธและการแพ็กลง JAR)";
            System.err.println(msg);
            // คืนรูปว่างเพื่อกัน NPE และให้ UI ยังคงแสดงได้
            BufferedImage blank = new BufferedImage(160, 160, BufferedImage.TYPE_INT_ARGB);
            return new ImageIcon(blank);
        }
        return new ImageIcon(url);
    }

    private static ImageIcon scaledIcon(ImageIcon src, int w, int h) {
        Image scaled = src.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /* ===== ปุ่มสไตล์ Pixel (ใช้ฟอนต์ที่โหลด) ===== */
    private JButton createPixelButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(pressFont);
        btn.setBackground(new Color(49, 33, 90));
        btn.setPreferredSize(new Dimension(170, 57));
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered (java.awt.event.MouseEvent e) { btn.setBackground(new Color(80, 50, 130)); }
            @Override public void mouseExited  (java.awt.event.MouseEvent e) { btn.setBackground(new Color(49, 33, 90)); }
            @Override public void mousePressed (java.awt.event.MouseEvent e) { btn.setBackground(new Color(20, 10, 60)); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { btn.setBackground(new Color(80, 50, 130)); }
        });
        return btn;
    }

    /* ===== โหลดฟอนต์ PressStart2P ===== */
    private Font loadPressFont(float size) {
        try (InputStream is = getClass().getResourceAsStream("/Font/PressStart2P-vaV7.ttf")) {
            if (is != null) {
                Font base = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base);
                return base.deriveFont(size);
            }
        } catch (Exception e) {
            System.err.println("⚠️ โหลดฟอนต์ PressStart2P ไม่สำเร็จ ใช้ Monospaced แทน (" + e + ")");
        }
        return new Font(Font.MONOSPACED, Font.BOLD, Math.round(size));
    }

    /* ===== โหลดฟอนต์ Mitr จาก resource (fallback เป็นระบบ/ sans-serif) ===== */
    private Font loadMitrFont(String resourcePath, float size) {
        // แผน 1: โหลดจาก resource
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is != null) {
                Font base = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base);
                return base.deriveFont(size);
            }
        } catch (Exception ignore) { /* ไปแผน 2 */ }

        // แผน 2: ลองใช้ชื่อฟอนต์ในระบบ
        Font trySystem = new Font("Mitr", Font.PLAIN, Math.round(size));
        if ("Mitr".equalsIgnoreCase(trySystem.getFamily())) {
            return trySystem;
        }

        // แผน 3: fallback
        return new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(size));
    }
}
