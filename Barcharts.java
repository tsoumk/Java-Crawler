import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Draws a bar chart
 * @author Tsoum
 */
public class Barcharts extends JFrame {
      private ArrayList<String> words=new ArrayList<String>();
      private ArrayList<Integer> counters=new ArrayList<Integer>();
      
      /**
       * Creates a bar chart object
       * @param words the words of the barchart
       * @param counters the counters
       */
      public Barcharts (ArrayList<String> words,ArrayList<Integer> counters){
          super("Barcharts");
          
          this.words=words;
          this.counters=counters;
          this.setSize(800, 720);
          this.setVisible(true);   
      }
      
      /**
       * Draws barcharts and lines
       * @param g 
       */
      public void paint(Graphics g) {
          super.paint(g);

          int xstart=35;
          int ystart=670;
          int w=20;
          int gap=15;
          int yend=ystart-counters.get(0)*5;
          int xend=xstart+gap+(w+gap)*words.size();
          
          g.setColor(Color.orange);
          g.fillRect(0, 0, this.getWidth(), this.getHeight());
          g.setColor(Color.DARK_GRAY);       
          
          g.drawLine(xstart, ystart, xstart, yend-50);  /* orizontios axonas */
          g.drawLine(xstart, ystart, xend+50, ystart);  /* katakoryfos axonas */
          
          for(int i=1; i<=(counters.get(0)/10)+1; i++) {
              g.drawLine(xstart-2,ystart-i*5*10,xstart+2,ystart-i*5*10);
              g.drawString(i*10+"",xstart-25,ystart-i*5*10+5);
          }
          
          for(int i=0; i<words.size(); i++){
              g.drawRect(xstart+gap+( (w+gap)*i), ystart-counters.get(i)*5, w, counters.get(i)*5);                           
              g.drawString(counters.get(i)+"", xstart+gap+5+( (w+gap)*i),ystart-5-counters.get(i)*5);
              g.drawString(words.get(i), xstart+gap+((w+gap)*i),ystart+15+(i%4)*15);
          }
      }
}