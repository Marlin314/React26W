package react.reactions;
import react.UC;
import react.graphics.G;

import java.awt.*;
import java.util.ArrayList;

public class Shape{
  public Prototype.List prototypes = new Prototype.List();
  public String name;

  public Shape(String name){this.name = name;}

  //------------------Prototype-----------------
  public static class Prototype extends Ink.Norm {
    int nBlend = 1;
    
    public void blend(Ink.Norm norm){blend(norm, nBlend); nBlend++;}
    
    //-------------------Prototype.List-----------
    public static class List extends ArrayList<Prototype>{

      public static Prototype bestMatch; // side effect of bestDist(); can be null

      public int bestDist(Ink.Norm norm){
        bestMatch = null; // note: bestDist can return null - NoMatches!
        int bestSoFar = UC.noMatchDist; // assume no match
        for(Prototype p : this) {
          int d = p.dist(norm);
          if(d < bestSoFar){
            bestMatch = p;
            bestSoFar = d;
          }
        }
        return bestSoFar;
      }

      private static int m = 10, w = 60; private static G.VS showbox = new G.VS(m,m,w,w);
      
      public void show(Graphics g){ // draw a list of boxes across top of screen
        g.setColor(Color.ORANGE);
        for(int i = 0; i<size(); i++){
          Prototype p = get(i); int x = m + i*(m+w);
          showbox.loc.set(x, m); // march the showbox across the top of the screen
          p.drawAt(g, showbox);
          g.drawString(""+p.nBlend,x,20);
        }
      }
    }
  }
}
