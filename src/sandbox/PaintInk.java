package sandbox;
import react.graphics.*;
import react.*;
import react.reactions.*;
import react.reactions.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PaintInk extends WinApp{
  public static Ink.List inkList = new Ink.List();
  public static Shape.Prototype.List pList = new Shape.Prototype.List();
  public static String recognized = "";
  
  public PaintInk(){super("Paint Ink", UC.mainWindowWidth, UC.mainWindowHeight);}
  
  @Override
  public void paintComponent(Graphics g){
    G.clearBack(g);
    g.setColor(Color.RED); Ink.BUFFER.show(g);
    inkList.show(g);
    g.drawString("points: "+Ink.BUFFER.n, 600,30);
    if(inkList.size()>1){
      int last = inkList.size()-1;
      int dist = inkList.get(last).norm.dist(inkList.get(last-1).norm);
      g.setColor(dist>UC.noMatchDist?Color.RED:Color.BLACK); // black for same red for different
      g.drawString("Dist: "+dist, 600, 60);
    }
    pList.show(g);
    g.setColor(Color.BLACK);
    g.drawString(recognized, 700, 40);
  }

  public void mousePressed(MouseEvent me){Ink.BUFFER.dn(me.getX(),me.getY()); repaint();}
  public void mouseDragged(MouseEvent me){Ink.BUFFER.drag(me.getX(),me.getY()); repaint();}
  public void mouseReleased(MouseEvent me){
    Ink ink = new Ink();
    Shape s = Shape.recognize(ink); recognized = "Recog: " + ((s != null)?s.name : "UN-RECOGNIZED");
    Shape.Prototype proto;
    inkList.add(ink);
    if(pList.bestDist(ink.norm) < UC.noMatchDist){ // we found a match so blend
      proto = Shape.Prototype.List.bestMatch;
      proto.blend(ink.norm);
    }else{
      proto = new Shape.Prototype();
      pList.add(proto); // new Prototype
    }
    ink.norm = proto;
    repaint();
  }

  public static void main(String[] args){PANEL=new PaintInk();WinApp.launch();}
}

