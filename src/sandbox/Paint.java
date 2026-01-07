package sandbox;

import react.graphics.G;
import react.graphics.WinApp;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Paint extends WinApp {
  public Paint(){super("Paint", 1000, 700);}

  @Override
  public void paintComponent(Graphics g){
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
  }

  public static int clicks = 0; // we will total the mouse clicks

  @Override
  public void mousePressed(MouseEvent me){
    clicks++; // bump up the click counter.
    repaint();
  }
  
  public static void main(String[] args){PANEL=new Paint(); WinApp.launch();}
}

