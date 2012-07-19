/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotgui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import share.DataStream;
import share.Packet;

/**
 *
 * @author laptop
 */
public class Graph extends javax.swing.JPanel implements Runnable {

    private double graphTime = -5000;
    private ArrayList<GStream> streams;
    private int hz = 20;
    private Thread thread;

    /**
     * Creates new form Graph
     */
    public Graph() {
        initComponents();
        streams = new ArrayList<>();
        thread = new Thread(this);
        thread.start();
    }

    public void sethz(int hz) {
        this.hz = hz;
    }

    public void addStream(
            DataStream dataStream, Color color,
            double center, double scale,
            boolean drawZero) {
        streams.add(new GStream(dataStream, center, scale, color, drawZero));
    }

    public String[] geStreams() {
        String[] names = new String[streams.size()];
        for (int i = 0; i < streams.size(); i++) {
            names[i] = streams.get(i).stream.getName();
        }
        return names;
    }

    public void removeStream(String stream) {
        for (int i = 0; i < streams.size(); i++) {
            if (streams.get(i).stream.getName().equals(stream)) {
                streams.remove(i);
            }
        }
    }

    public void removeAllStreams() {
        streams = new ArrayList<>();
    }

    public void setTime(long time) {
        graphTime = -Math.abs(time);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color c;
        for (GStream gStream : streams) {
            if (gStream.drawZero) {
                c = new Color(
                        (gStream.color.getRed() + 255) / 2,
                        (gStream.color.getGreen() + 255) / 2,
                        (gStream.color.getBlue() + 255) / 2);
                g.setColor(c);
                paintLine(
                        System.currentTimeMillis(),
                        gStream.center,
                        System.currentTimeMillis() + ((long) graphTime),
                        gStream.center, g);
            }
        }
        Packet oldPacket;
        for (GStream gStream : streams) {
            oldPacket = gStream.stream.getLastPacket();
            for (int i = 1; i < gStream.stream.getPackets().length
                    && oldPacket.time - System.currentTimeMillis()
                    > graphTime; i++) {
                g.setColor(gStream.color);
                paintLine(
                        oldPacket.time,
                        (oldPacket.val
                        * gStream.scale) + gStream.center,
                        gStream.stream.getPackets()[i].time,
                        (gStream.stream.getPackets()[i].val
                        * gStream.scale) + gStream.center, g);
                oldPacket = gStream.stream.getPackets()[i];
            }
        }
    }

    private void paintLine(
            long x1, double y1,
            long x2, double y2,
            Graphics g) {

        y1 = -y1 + 1;
        y2 = -y2 + 1;

        y1 = y1 * getHeight();
        y2 = y2 * getHeight();

        double xx1 = (((double) (x1 - System.currentTimeMillis()))
                * getWidth()) / graphTime;
        double xx2 = (((double) (x2 - System.currentTimeMillis()))
                * getWidth()) / graphTime;

        g.drawLine((int) xx1, (int) y1, (int) xx2, (int) y2);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 * (1 / hz));
            } catch (InterruptedException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
            repaint();
        }
    }

    private class GStream {

        public GStream(DataStream stream, double center, double scale, Color color, boolean drawZero) {
            this.stream = stream;
            this.center = center;
            this.scale = scale;
            this.color = color;
            this.drawZero = drawZero;
        }

        public GStream() {
        }
        DataStream stream;
        double center;
        double scale;
        Color color;
        boolean drawZero;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));

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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
