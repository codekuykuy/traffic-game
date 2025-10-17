    package project;

    import javax.swing.*;
    import java.awt.*;
    import java.io.File;
    import java.util.*;
    import java.util.List;
    
    import project.Node;
    import project.Path;

    public class Car {
        private static final String ASSET_PATH = "assets";
        private static final Random random = new Random();

        private Image[] frames;     // เฟรมภาพเคลื่อนไหว
        private int currentFrame = 0;
        private long lastFrameTime = 0;
        private int frameDelay = 1000 / 12; // 12 fps

        private String type;  // ประเภทรถ เช่น bus, taxi
        private String color; // สี เช่น Black
        private String direction; // ทิศ เช่น EAST
        
        private Node position;

        private List<Path> spawnPaths;
        private Path path;
        private Path currentPath;
        private int currentNode = 0;
        private double x, y;
        private double speed = 3.5;
        private static final double SAFE_DISTANCE = 50.0; 
        private boolean isStopped = false;
        
        private double angle = 0;
        
        private int currentTargetIndex = 0;
        
        private Map<Node, List<Path>> nodeToPaths;

        public Car(Path path, Map<Node, List<Path>> nodeToPaths, List<Path> spawnPaths) {
            this.path = path;
            this.nodeToPaths = nodeToPaths;
            this.spawnPaths = spawnPaths; // กำหนดค่า
            Node start = path.getNode(0);
            this.x = start.x;
            this.y = start.y;
            loadRandomCar();
        }
        
        //--------------------------------------------------------------------------------------------
        private String getDirectionFromAngle(double angle) {
            angle = Math.toDegrees(angle);
            if (angle < 0) angle += 360;

            if (angle >= 337.5 || angle < 22.5) return "EAST";
            else if (angle < 67.5) return "NORTHEAST";
            else if (angle < 112.5) return "NORTH";
            else if (angle < 157.5) return "NORTHWEST";
            else if (angle < 202.5) return "WEST";
            else if (angle < 247.5) return "SOUTHWEST";
            else if (angle < 292.5) return "SOUTH";
            else return "SOUTHEAST";
        }
        
        private void loadRandomCar() {
            // 1. เลือกประเภทรถ
            File assetDir = new File(ASSET_PATH);
            String[] carTypes = assetDir.list((dir, name) -> new File(dir, name).isDirectory() && !name.startsWith("."));
            if (carTypes == null || carTypes.length == 0) {
                System.err.println("❌ No car types found in assets/");
                return;
            }
            type = carTypes[random.nextInt(carTypes.length)];

            // 2. ตรวจว่ารถประเภทนี้มีสีไหม
            File typeDir = new File(ASSET_PATH, type);
            File moveBase; // โฟลเดอร์ที่เราจะเริ่มโหลด MOVE

            File maybeColorDir = new File(typeDir, "MOVE");
            if (maybeColorDir.exists()) {
                // ✅ กรณีไม่มีสี เช่น ambulance/police/taxi
                color = null;
                moveBase = maybeColorDir;
            } else {
                // ✅ กรณีมีสี เช่น bus, jeep, sedan
                String[] colors = typeDir.list((dir, name) -> new File(dir, name).isDirectory());
                if (colors == null || colors.length == 0) {
                    System.err.println("❌ No colors found in " + typeDir.getPath());
                    return;
                }
                color = colors[random.nextInt(colors.length)];
                moveBase = new File(typeDir, color + "/MOVE");
            }

            // 3. สุ่มทิศ (EAST, WEST, ...)
            String[] dirs = moveBase.list((dir, name) -> new File(dir, name).isDirectory());
            if (dirs == null || dirs.length == 0) {
                System.err.println("❌ No directions found in " + moveBase.getPath());
                return;
            }
            direction = dirs[random.nextInt(dirs.length)];

            // 4. เข้าไปใน SEPARATED แล้วโหลดภาพ
            File dir = new File(moveBase, direction + "/SEPARATED");
            if (!dir.exists()) {
                dir = new File(moveBase, direction); // fallback
            }

            File[] imgs = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
            if (imgs == null || imgs.length == 0) {
                System.err.println("❌ No image frames found in " + dir.getPath());
                return;
            }

            Arrays.sort(imgs);
            frames = new Image[Math.min(11, imgs.length)];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = new ImageIcon(imgs[i].getPath()).getImage();
            }

            System.out.println("🚗 Loaded: " + type + 
                (color != null ? " | " + color : "") + " | " + direction);
        }
        
        //--------------------------------------------------------------------------------------------
        
        public void updatePosition() {
            if (currentNode >= path.length() - 1) {
                // ถึงปลายทางของ path นี้แล้ว
                Node lastNode = path.getNode(path.length() - 1);
                List<Path> nextOptions = path.getNextPaths(lastNode);

                if (nextOptions != null && !nextOptions.isEmpty()) {
                    // สุ่ม path ใหม่
                    Path nextPath = nextOptions.get(random.nextInt(nextOptions.size()));
                    this.path = nextPath;
                    this.currentNode = 0;
                    return; // เริ่ม path ใหม่
                } else {
                        resetToStart();
                    return;
                }
            }

            // ---- เคลื่อนที่ไป node ถัดไป ----
            Node current = path.getNode(currentNode);
            Node next = path.getNode(currentNode + 1);

            double dx = next.x - x;
            double dy = next.y - y;
            double dist = Math.sqrt(dx*dx + dy*dy);

            angle = Math.atan2(-dy, dx);

            if (dist < speed) {
                x = next.x;
                y = next.y;
                currentNode++;
            } else {
                x += dx / dist * speed;
                y += dy / dist * speed;
            }

            // อัปเดตทิศทาง
            String newDirection = getDirectionFromAngle(angle);
            if (!newDirection.equals(direction)) {
                direction = newDirection;
                loadDirectionFrames();
            }
        }



        
        private void loadDirectionFrames() {
            try {
                File moveBase;
                if (color == null) {
                    moveBase = new File(ASSET_PATH + "/" + type + "/MOVE");
                } else {
                    moveBase = new File(ASSET_PATH + "/" + type + "/" + color + "/MOVE");
                }

                File dir = new File(moveBase, direction + "/SEPARATED");
                if (!dir.exists()) dir = new File(moveBase, direction); // fallback

                File[] imgs = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
                if (imgs == null || imgs.length == 0) return;

                Arrays.sort(imgs);
                frames = new Image[Math.min(11, imgs.length)];
                for (int i = 0; i < frames.length; i++) {
                    frames[i] = new ImageIcon(imgs[i].getPath()).getImage();
                }

            } catch (Exception e) {
                System.err.println("❌ Error loading direction frames: " + e.getMessage());
            }
        }


        
        public void updateFrame() {
            long now = System.currentTimeMillis();
            if (now - lastFrameTime > frameDelay) {
                currentFrame = (currentFrame + 1) % frames.length;
                lastFrameTime = now;
            }
        }

        public void draw(Graphics2D g2) {
            if (frames != null && frames.length > 0){
                Image carImage = frames[currentFrame]; // ✅ ดึงภาพจากเฟรมปัจจุบัน

                int drawX = (int)(x - carImage.getWidth(null) / 2);
                int drawY = (int)(y - carImage.getHeight(null) / 2);

                g2.drawImage(carImage, drawX, drawY, null); 
            }
        }
        
        private void resetToStart() {
            // เลือก path ใหม่จาก spawnPaths
            Path newPath = spawnPaths.get(new Random().nextInt(spawnPaths.size()));
            this.path = newPath;
            this.currentNode = 0;

            Node start = newPath.getNode(0);
            this.x = start.x;
            this.y = start.y;

            this.isStopped = false; // รีเซ็ตการหยุด
        }


        public void update(List<Car> allCars) {
            if (!isStopped && !isBlocked(allCars)) {
                updatePosition();
            }
        }

        private boolean isBlocked(List<Car> allCars) {
            for (Car other : allCars) {
                if (other == this) continue;
                if (other.path != this.path) continue;

                double dist = Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2));

                if (isAheadOf(other) && dist < SAFE_DISTANCE) {
                    isStopped = true;
                    return true;
                }
            }
            isStopped = false;
            return false;
        }


        private boolean isAheadOf(Car other) {
            if (path.isVertical()) {
                if (path.isGoingUp()) {
                    return other.y < this.y;
                } else {
                    return other.y > this.y;
                }
            } else {
                if (path.isGoingRight()) {
                    return other.x > this.x;
                } else {
                    return other.x < this.x;
                }
            }
        }


    }
