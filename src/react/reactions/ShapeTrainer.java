package react.reactions;

import react.UC;
import react.graphics.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ShapeTrainer extends WinApp {
  public static Shape.Prototype.List pList = null;
  
  public ShapeTrainer(){super("Shape Trainer", UC.mainWindowWidth, UC.mainWindowHeight);}

  public static String UNKNOWN = " <- this name is currently Unknown.";
  public static String ILLEGAL = " <-this name is NOT a legal Shape name.";
  public static String KNOWN   = " <-this is a known shape.";

  public static String curName = "";
  public static String curState = ILLEGAL;

  public void setState(){
    curState = !Shape.DB.isLegal(curName) ? ILLEGAL : UNKNOWN;
    //noinspection StringEquality
    if(curState == UNKNOWN){
      if(Shape.DB.isKnown(curName)){
        curState = KNOWN;
        pList = Shape.DB.get(curName).prototypes;
      }else{ // it really is UNKNOWN
        pList = null;
      }
    }
  }

  public void mousePressed(MouseEvent me){Ink.BUFFER.dn(me.getX(),me.getY()); repaint();}
  public void mouseDragged(MouseEvent me){Ink.BUFFER.drag(me.getX(),me.getY()); repaint();}
  public void mouseReleased(MouseEvent me){
    Ink ink = new Ink();
    Shape.DB.train(curName, ink.norm); // this is safe because legal name testing is done in Database
    setState(); // possibly convert previously UNKNOWN to KNOWN
    repaint();
  }

  public void paintComponent(Graphics g){
    G.clearBack(g);
    g.setColor(Color.BLACK);
    g.drawString(curName, 600,30);
    g.drawString(curState, 700,30);
    g.setColor(Color.RED);
    Ink.BUFFER.show(g);
    if(pList != null){pList.show(g);}
  }
  
  public void keyTyped(KeyEvent e) {
    char c = e.getKeyChar(); System.out.println("Typed: " + c);
    curName = (c == ' ' || c == 0x0D || c == 0x0A)? "": curName + c; // x0D & x0A are ascii CR & LF
    if(c == 0x0D || c == 0x0A){Shape.DB.save();}
    setState();
    repaint();
  }
  
  public static void main(String[] args){PANEL=new ShapeTrainer(); WinApp.launch();}
}
