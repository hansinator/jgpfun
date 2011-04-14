/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainView.java
 *
 * Created on Apr 16, 2010, 8:42:34 PM
 */
package jgpfun.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import jgpfun.life.BaseOrganism;
import jgpfun.world2d.Food;
import jgpfun.world2d.Organism2d;
import jgpfun.world2d.Body2d;

/**
 *
 * @author hansinator
 */
public class MainView extends javax.swing.JPanel {

    List<Food> curFood;

    List<BaseOrganism> curOrganisms;

    private int rps;

    private int progress;


    /*
     * TODO:
     * add setters for things to draw or make a worldmodel including all stuff to draw
     * possibly make object lists for world objects, like bodies and food
     */


    /** Creates new form MainView */
    public MainView() {
        initComponents();
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.black);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        if (curFood != null) {
            drawFood(curFood, g);
        }

        if (curOrganisms != null) {
            drawOrganisms(curOrganisms, g);
        }

        if (rps != 0) {
            g.setColor(Color.yellow);
            g.drawString("RPS: " + rps, 10, 15);
        }

        if (progress != 0) {
            g.setColor(Color.yellow);
            g.drawString("" + progress + "%", 10, 30);
        }
    }


    public void drawStuff(List<Food> food, List<BaseOrganism> organisms, int rps, int progress) {
        this.curFood = food;
        this.curOrganisms = organisms;
        this.rps = rps;
        this.progress = progress;
    }


    private void drawFood(List<Food> food, Graphics g) {
        g.setColor(Color.green);

        for (Food f : food) {
            g.fillOval(f.x, f.y, 2, 2);
        }
    }


    private void drawOrganisms(List<BaseOrganism> organisms, Graphics g) {
        g.setColor(Color.red);

        for (BaseOrganism o : organisms) {
            for (Body2d b : ((Organism2d)o).bodies) {
                double sindir = Math.sin(b.dir);
                double cosdir = Math.cos(b.dir);
                int xrot = (int) Math.round(8.0 * sindir);
                int yrot = (int) Math.round(8.0 * cosdir);
                int x = Math.round((float)b.x);
                int y = Math.round((float)b.y);

                g.drawLine(x, y, x + xrot, y - yrot);
                g.fillOval(x, y, 4, 4);

                //Polygon p = new Polygon();
                /*
                //1st try
                p.addPoint((b.x - xrot), (b.y + yrot));
                p.addPoint((b.x + xrot), (b.y + yrot));
                p.addPoint(b.x + xrot, b.y - 6 + yrot);

                //2nd try - looks like a triangle
                p.addPoint((b.x - 4 + xrot), (b.y + 4 + yrot));
                p.addPoint((b.x + 4 + xrot), (b.y + 4 + yrot));
                p.addPoint(b.x + xrot, b.y - 6 + yrot);
                
                g.drawPolygon(p);
                g.fillPolygon(p);*/
            }

        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
