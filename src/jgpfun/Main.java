/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jgpfun;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

/**
 *
 * @author hansinator
 */
public class Main {

    private volatile boolean running = true;

    private void run() {
        Dimension d = new Dimension(1280, 1024);
        final PopulationManager pm = new PopulationManager(d.width, d.height, 26, 128, 30);
        JFrame frame = new JFrame("BAH! Bonn!!1!11!!!");
        MainView mainView = new MainView();

        frame.setSize(d);
        frame.setContentPane(mainView);
        frame.setVisible(true);

        System.out.println("Main window size: " + mainView.getWidth() + "x" + mainView.getHeight());

        frame.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if(pm.roundsMod == 400) {
                    pm.roundsMod = 1;
                    System.out.println("its 1 now");
                }
                else {
                    System.out.println("its 400 now");
                    pm.roundsMod = 400;
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });

        frame.addWindowListener(new WindowListener() {

            public void windowOpened(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                running = false;
                System.out.println("Closing main window");
            }

            public void windowClosed(WindowEvent e) {
                running = false;
                System.out.println("Closed main window");
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowActivated(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
            }
        });

        while (running) {
            pm.runGeneration(4000, mainView);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Main().run();
        System.exit(0);
    }
}
