package sandbox;

import react.graphics.G;
import react.graphics.WinApp;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Paint extends WinApp {
  public static Path thePath = new Path();
  
  public Paint(){super("Paint", 1000, 700);}

  @Override
  public void paintComponent(Graphics g){
    g.setColor(Color.WHITE); g.fillRect(0,0,9000,9000);
    Color c = G.rndColor();
    g.setColor(c);
    g.fillOval(100,50,200,300);
    g.setColor(Color.BLACK);
    g.drawLine(100,600,600,100);
    int x = 400, y = 200; String msg = "Dude" + clicks;

    FontMetrics fm = g.getFontMetrics(); // local variable fm is information about the current font.
    int a = fm.getAscent(), d = fm.getDescent(); 
    int w = fm.stringWidth(msg);
    g.drawRect(x,y-a,w,a+d); 

    g.drawString(msg,x,y);
    g.drawOval(x,y,3,3);
    thePath.draw(g);
  }

  public static int clicks = 0; // we will total the mouse clicks

  @Override
  public void mousePressed(MouseEvent me){
    clicks++;
    thePath.clear();
    thePath.add(me.getPoint());
    repaint();
  }

  @Override
  public void mouseDragged(MouseEvent me){
    thePath.add(me.getPoint());
    repaint(); // If you forgot this - you add points but do not SEE them! a bug!
  }

  public static void main(String[] args){PANEL=new Paint(); WinApp.launch();}

  //--------------------PATH----------------------------
  public static class Path extends ArrayList<Point> {
    public void draw(Graphics g){
      for(int i = 1; i<size(); i++){
        Point p = get(i-1), n = get(i); // the previous and the next point
        g.drawLine(p.x,p.y,n.x,n.y);
      }
    }
  }
}

