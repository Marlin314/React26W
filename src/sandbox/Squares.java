package sandbox;
import react.graphics.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Squares extends WinApp{
  public static G.VS theVS = new G.VS(100,100,200,300);
  public static Color color = G.rndColor();
  public static Square.List squares = new Square.List();

  public Squares(){super("Squares",1000,800);}

  @Override
  public void paintComponent(Graphics g){
    G.clearBack(g);
    squares.draw(g);
  }

  @Override
  public void mousePressed(MouseEvent me){
    squares.add(new Square(me.getX(), me.getY()));
    repaint();
  }
  
  public static void main(String[] args){PANEL=new Squares();WinApp.launch();}

  //-----------------Square------------------------------
  public static class Square extends G.VS{
    public Color c = G.rndColor();
    public Square(int x, int y){super(x,y,100,100);}
    public void draw(Graphics g){fill(g,c);}

    //------------------List----------------------------
    public static class List extends ArrayList<Square> {
      public void draw(Graphics g){for(Square s : this){s.draw(g);}}
    }
  }


}

