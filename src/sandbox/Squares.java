package sandbox;
import react.graphics.*;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Squares extends WinApp{
  public static G.VS theVS = new G.VS(100,100,200,300);
  public static Color color = G.rndColor();

  public Squares(){super("Squares",1000,800);}

  @Override
  public void paintComponent(Graphics g){
    G.clearBack(g);
    theVS.fill(g, color); // give us a nice Rect
  }

  @Override
  public void mousePressed(MouseEvent me){
    if(theVS.hit(me.getX(), me.getY())){color = G.rndColor();}
    repaint(); // don't forget to repaint when you change something.
  }
  
  public static void main(String[] args){PANEL=new Squares();WinApp.launch();}
}

