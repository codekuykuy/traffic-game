package car.test;

import javax.swing.*;
import java.awt.event.*;

public class GameTimer {
    private int remainingSeconds;
    private Timer timer;
    private Runnable onTick;

    public GameTimer(Runnable onTick) {
        this.onTick = onTick;
        this.remainingSeconds = 180;

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingSeconds > 0) {
                    remainingSeconds--;
                    if (onTick != null) {
                        onTick.run();
                    }
                } else {
                    stop();
                }
            }
        });
    }

    public void start() {
        if (!timer.isRunning()) timer.start();
    }

    public void stop() {
        if (timer.isRunning()) timer.stop();
    }

    public void reset() {
        remainingSeconds = 180; // รีเซ็ตกลับไป 3 นาที
        if (onTick != null) onTick.run();
    }

    // 🔹 getter สำหรับเวลาที่เหลือ
    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    // 🔹 getter สำหรับรูปแบบ mm:ss
    public String getTimeString() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
