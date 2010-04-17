/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jgpfun;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

/**
 *
 * @author hansinator
 */
public class Main {
    static volatile boolean running = true;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        Dimension d = new Dimension(1280, 1024);
        PopulationManager pm = new PopulationManager(d.width, d.height, 26, 256, 20);
        JFrame frame = new JFrame("BAH! Bonn!!1!11!!!");
        MainView mainView = new MainView();

        frame.setSize(d);
        frame.setContentPane(mainView);
        frame.setVisible(true);

        System.out.println("size: " + mainView.getWidth() + "x" + mainView.getHeight());

        frame.addWindowListener(new WindowListener() {

            public void windowOpened(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                Main.running = false;
            }

            public void windowClosed(WindowEvent e) {
                Main.running = false;
                System.out.println("TADAA");
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
        
        while (Main.running) {
            pm.runGeneration(4000, mainView);
        }
    }
}
