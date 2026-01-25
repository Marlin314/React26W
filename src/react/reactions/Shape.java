package react.reactions;
import react.UC;
import react.graphics.G;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class Shape{
  public Prototype.List prototypes = new Prototype.List();
  public String name;
  
  public static Shape.Database DB = Shape.Database.load();
  public static Shape DOT = DB.get("DOT"); // fetch the DOT out. It is special
  public static Collection<Shape> LIST = DB.values(); // list of all shapes in the DB

  public Shape(String name){this.name = name;}

  public static Shape recognize(Ink ink){ // note: can return null
    if(ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold){return DOT;}
    Shape bestMatch = null; int bestSoFar = UC.noMatchDist; // assume no match
    for(Shape s:LIST){
      int d = s.prototypes.bestDist(ink.norm);
      if(d < bestSoFar){bestMatch = s; bestSoFar = d;}
    }
    return bestMatch;
  }
  
  //--------------Shape.Database-----------------
  public static class Database extends TreeMap<String,Shape> {
    public static Database load(){  // stub
      Database res = new Database();
      res.put("DOT", new Shape("DOT"));
      return res;
    }
    public static void save(){}//stub
    
    boolean isKnown(String name){return containsKey(name);}
    boolean isUnknown(String name){return !containsKey(name);}
    boolean isLegal(String name){return !name.equals("") && !name.equals("DOT");}
  }
  
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
