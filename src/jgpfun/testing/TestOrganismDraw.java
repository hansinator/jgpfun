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
public class TestOrganismDraw extends JPanel {

    private final Organism2d organism;

    private final JSlider slider;

    private final World2d world;


    public TestOrganismDraw() throws IOException {
        setPreferredSize(new Dimension(640, 480));
        world = new World2d(800, 600, 23);
        world.randomFood();
        this.organism = new Organism2d(Genome.randomGenome(256));
        this.organism.addToWorld(world);
        this.organism.bodies[0].x = 320.0;
        this.organism.bodies[0].y = 240.0;
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
        organism.draw(g);
        world.draw(g);
        g.setColor(Color.yellow);
        g.drawString("Dir: " + String.format("%.2f", organism.bodies[0].dir), 10, 15);
    }


    public static void main(String[] args) throws IOException {
        Settings.load(new File("default.properties"));
        JFrame frame = new JFrame("test");
        TestOrganismDraw testOrganismDraw = new TestOrganismDraw();
        frame.setLayout(new BorderLayout());
        frame.add(testOrganismDraw, BorderLayout.CENTER);
        frame.add(testOrganismDraw.slider, BorderLayout.PAGE_END);
        frame.pack();
        frame.setVisible(true);
    }

}
