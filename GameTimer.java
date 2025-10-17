package car.test;

import javax.swing.*;
import java.awt.event.*;

public class GameTimer {
    private int elapsedSeconds;
    private Timer timer;
    private Runnable onTick; // callback สำหรับ repaint หรืออัพเดท UI

    public GameTimer(Runnable onTick) {
        this.onTick = onTick;
        this.elapsedSeconds = 0;

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedSeconds++;
                if (onTick != null) {
                    onTick.run(); // เรียก repaint()
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
        elapsedSeconds = 0;
        if (onTick != null) onTick.run();
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    // เพิ่ม helper สำหรับรูปแบบ mm:ss
    public String getTimeString() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        return String.format("Time: %02d:%02d", minutes, seconds);
    }
}

