/*
 */
package jgpfun.testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jgpfun.genetics.Genome;
import jgpfun.util.Settings;
import jgpfun.world2d.Organism2d;
import jgpfun.world2d.World2d;

/**
 *
 * @author Hansinator
 */
public class TestRadarSense extends JPanel {

    private final Organism2d organism;

    private final JSlider slider;

    private final World2d world;


    public TestRadarSense() throws IOException {
        setPreferredSize(new Dimension(800, 600));
        world = new World2d(800, 600, 23);
        world.randomFood();
        this.organism = new Organism2d(Genome.randomGenome(256));
        this.organism.addToWorld(world);
        this.organism.bodies[0].x = 400.0;
        this.organism.bodies[0].y = 300.0;
        this.organism.bodies[0].prepareInputs();

        slider = new JSlider(0, 360);
        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                organism.bodies[0].dir = (double) slider.getValue() * (Math.PI / 180.0);
                organism.bodies[0].radarSense.get();
                repaint();
            }

        });
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        /*organism.draw(g);
        g.setColor(Color.yellow);
        g.drawString("Dir: " + String.format("%.2f", organism.bodies[0].dir), 10, 15);*/
    }
    

    public static void main(String[] args) throws IOException {
        /*Settings.load(new File("default.properties"));
        JFrame frame = new JFrame("test");
        TestRadarSense testOrganismDraw = new TestRadarSense();
        frame.setLayout(new BorderLayout());
        frame.add(testOrganismDraw, BorderLayout.CENTER);
        frame.add(testOrganismDraw.slider, BorderLayout.PAGE_END);
        frame.pack();
        frame.setVisible(true);*/
        double x1, x2, x3, y1, y2, y3;

        x1 = 10;
        y1 = 10;
        x2 = 200;
        y2 = 200;
        x3 = 15;
        y3 = 15;
        x3 = Math.floor(x3);
        y3 = Math.floor(y3);


        System.out.println(((x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1)) == 0);
        System.out.println((x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1));

        double m, b;
        m = (y2 - y1) / (x2 - x1);
        b = y1 - m * x1;

        double y = m * x3 + b;

        if (Math.abs(y - y3) < 10.0) {
            System.out.println(y);
            System.out.println(y3);
            System.out.println(y-y3);
            System.out.println("");
        }
        System.out.println(y);
        System.out.println(y3);
    }

}
