package com.lordjoe.forms;


import org.bytedeco.javacv.FrameGrabber;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.TimerTask;

/**
 * com.lordjoe.forms.FrameGrabberPanel
 * User: Steve
 * Date: 8/31/2018
 */
public class FrameGrabberPanel extends JPanel {
    /*
    private FrameGrabber vision;
    private BufferedImage image;
    private VideoPanel videoPanel = new VideoPanel();
    private JButton jbtCapture = new JButton("Show Video");
    private Timer timer = new Timer();


    public FrameGrabberPanel() {
                setLayout(new FlowLayout());
           add(jbtCapture);

            setLayout(new BorderLayout());
            add(videoPanel, BorderLayout.CENTER);
            add(this, BorderLayout.SOUTH);
            setVisible(true);

            jbtCapture.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            timer.schedule(new ImageTimerTask(), 1000, 33);
                        }
                    }
            );
        }

        class ImageTimerTask extends TimerTask {
            public void run() {
                videoPanel.showImage();
            }
        }

        class VideoPanel extends JPanel {
            public VideoPanel() {
                try {
                    vision = new FrameGrabber();
                    vision.start();
                } catch (FrameGrabberException fge) {
                }
            }

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null)
                    g.drawImage(image, 10, 10, 160, 120, null);
            }

            public void showImage() {
                image = vision.getBufferedImage();
                repaint();
            }
        }

        public static void main(String[] args) {

            JFrame frame = new JFrame();
            frame.setLayout(new BorderLayout());
            frame.add(new FrameGrabberPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(190, 210);
            frame.setVisible(true);
        }
        */
    }