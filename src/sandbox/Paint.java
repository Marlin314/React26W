package sandbox;

import react.graphics.WinApp;
import java.awt.*;

public class Paint extends WinApp {
  public Paint(){super("Paint", 1000, 700);}

  @Override
  public void paintComponent(Graphics g){
    g.setColor(Color.BLUE);
    g.fillOval(100,50,200,300);
    g.setColor(Color.BLACK);
    g.drawLine(100,600,600,100);
    int x = 400, y = 200; String msg = "Dude";

    FontMetrics fm = g.getFontMetrics(); // local variable fm is information about the current font.
    int a = fm.getAscent(), d = fm.getDescent(); // get numbers from font metrics

    // the ascent is how far above the baseline the font extends, 
    // .. descent is how far below the base line for letters with tails like
    // gyq

    // the entire height of the font will be a+d

    int w = fm.stringWidth(msg); // get width of msg from font metrics

    // note: since fonts can have variable character width, "iii" takes less space than "mmm", we must
    // tell fm, what string we are interested in measuring and fm will perform the calculation for us
    // and tell us how many pixels wide that string will be.

    // so now we know enough to draw the box.

    g.drawRect(x,y-a,w,a+d); // note: move y from baseline UP the page by the ascent

    g.drawString(msg,x,y);
    g.drawOval(x,y,3,3); // 3 is just a small number to make a small dot
  }

  public static void main(String[] args){PANEL=new Paint(); WinApp.launch();}
}

