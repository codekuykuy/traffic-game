package project;

import java.util.*;

public class Path {
    private String name;
    private List<Node> nodes;
    private List<Path> nextPaths = new ArrayList<>();
    private boolean loop; 

    public Path(Node... nodes) {
        this.nodes = new ArrayList<>(Arrays.asList(nodes));
        this.loop = false;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Path> getNextPaths(Node node) {
        if (!nodes.contains(node)) {
            return new ArrayList<>();
        }
        return nextPaths;
    }

    public void addNextPath(Path path) {
        nextPaths.add(path);
    }
    
    public Path(boolean loop, Node... nodes) {
        this.nodes = new ArrayList<>(Arrays.asList(nodes));
        this.loop = loop;
    }

    public Node getNode(int index) {
        if (index < 0 || index >= nodes.size()) return null;
        return nodes.get(index);
    }

    public List<Node> getNodes() { return nodes; }
    public boolean isLoop() { return loop; }
    public int length() { return nodes.size(); }
    
    public boolean isVertical() {
        Node start = nodes.get(0);
        Node end = nodes.get(nodes.size() - 1);
        return Math.abs(end.x - start.x) < Math.abs(end.y - start.y);
    }

    public boolean isGoingUp() {
        Node start = nodes.get(0);
        Node end = nodes.get(nodes.size() - 1);
        return start.y > end.y;
    }

    public boolean isGoingRight() {
        Node start = nodes.get(0);
        Node end = nodes.get(nodes.size() - 1);
        return start.x < end.x;
    }


    
    public static List<Path> getDefaultPaths() {
        List<Path> list = new ArrayList<>();
        List<Path> spawnPaths = new ArrayList<>();


        // ================= Vertical main lanes (ขึ้น/ลง) =================

        //  Lane 1 (ซ้ายสุด, ขึ้น)
        list.add(new Path(false,
            new Node(2538, 3449),
            new Node(2538, 1179)   // จบที่จุดเลี้ยว
        ));
        // เลี้ยวซ้ายจากแถวซ้ายสุด (ขึ้น -> ซ้าย)
        list.add(new Path(true,
            new Node(2538, 1179),
            new Node(268, 3449)
        ));
        // ตรงขึ้นต่อไปด้านบน (จากจุดเดิม)
        list.add(new Path(false,
            new Node(2538, 1179),
            new Node(2538, 1034),
            new Node(2538, 0)
        ));


        // Lane 2 (ขวา, ขึ้น)
        list.add(new Path(false,
            new Node(2638, 3449),
            new Node(2638, 933)   // จบที่จุดเลี้ยว
        ));
        // เลี้ยวขวา (ขึ้น -> ขวา)
        list.add(new Path(true,
            new Node(2638, 933),
            new Node(2856, 715),
            new Node(4802, 991)
        ));
        // ตรงขึ้นต่อ
        list.add(new Path(false,
            new Node(2638, 933),
            new Node(2638, 717),
            new Node(2638, 0)
        ));


        //  Lane 3 (ซ้าย, ลง)
        list.add(new Path(false,
            new Node(2738, 0),
            new Node(2738, 979)   // จบที่จุดเลี้ยว
        ));
        // เลี้ยวขวา (ลง -> ขวา)
        list.add(new Path(true,
            new Node(2738, 979),
            new Node(268, 3449)
        ));
        // ตรงลงต่อ
        list.add(new Path(false,
            new Node(2738, 979),
            new Node(2738, 1232),
            new Node(2738, 3449)
        ));


        //  Lane 4 (ขวาสุด, ลง)
        list.add(new Path(false,
            new Node(2838, 0),
            new Node(2838, 639)   // จบที่จุดเลี้ยว
        ));
        // เลี้ยวซ้าย (ลง -> ซ้าย)
        list.add(new Path(true,
            new Node(2838, 639),
            new Node(2935, 726),
            new Node(4802, 991)
        ));
        // ตรงลงต่อ
        list.add(new Path(false,
            new Node(2838, 639),
            new Node(2838, 3449)
        ));


        // ================= Horizontal main lanes (ขวา) =================
        // Lane 1 (ซ้าย -> ขวา)
        list.add(new Path(false,
            new Node(2838, 639),
            new Node(2935, 726),
            new Node(4802, 991)
        ));
        // Lane 2 (ขวา -> ซ้าย)
        list.add(new Path(false,
            new Node(4796, 1087),
            new Node(2900, 820)
        ));
        //เลี้ยวซ้าย
         list.add(new Path(true,
            new Node(2838, 879),
            new Node(2838, 3449)
        ));
         //ตรง
         list.add(new Path(true,
            new Node(2900, 820),
            new Node(2538, 1179),
            new Node(268, 3449)
        ));
         //เลี้ยวขวา
         list.add(new Path(true,
            new Node(2900, 820),
            new Node(2638, 717),
            new Node(2638, 0)
        ));
        
        // ================= Horizontal main lanes (ซ้าย) =================
        // Lane 1 (ซ้าย -> ขวา)
        list.add(new Path(false,
            new Node(123, 3449),
            new Node(2538, 1034)
        ));
        //เลี้ยวซ้าย
         list.add(new Path(true,
            new Node(2538, 1034),
            new Node(2538, 0)
        ));
        //ตรง
        list.add(new Path(true,
            new Node(2538, 1034),
            new Node(2856, 715),
            new Node(2935, 726),
            new Node(4802, 991)
        ));
        //เลี้ยวขวา
        list.add(new Path(true,
            new Node(2538, 1034),
            new Node(2738, 1232),
            new Node(2738, 3449)
        ));
        
        // Lane 2 (ขวา -> ซ้าย)
        list.add(new Path(false,
            new Node(2538, 1179),
            new Node(2458, 1259),
            new Node(268, 3449)
        ));
        
     
        return list;
    }

}
