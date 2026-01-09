package sandbox;
import react.graphics.*;
import react.*;
import react.reactions.Ink;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PaintInk extends WinApp{
  public static Ink.List inkList = new Ink.List();
  
  public PaintInk(){super("Paint Ink", UC.mainWindowWidth, UC.mainWindowHeight);}
  
  @Override
  public void paintComponent(Graphics g){
    G.clearBack(g);
    inkList.show(g);
    g.setColor(Color.RED); Ink.BUFFER.show(g);
  }

  public void mousePressed(MouseEvent me){Ink.BUFFER.dn(me.getX(),me.getY()); repaint();}
  public void mouseDragged(MouseEvent me){Ink.BUFFER.drag(me.getX(),me.getY()); repaint();}
  public void mouseReleased(MouseEvent me){
    Ink.BUFFER.up(me.getX(), me.getY());
    inkList.add(new Ink());
    repaint();
  }

  public static void main(String[] args){PANEL=new PaintInk();WinApp.launch();}
}

