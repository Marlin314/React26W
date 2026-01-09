package react.reactions;

import react.*;
import react.graphics.*;
import java.awt.*;
import java.util.ArrayList;

public class Ink extends G.PL implements I.Show{
  public static Buffer BUFFER = new Buffer();
  public Ink(){
    super(BUFFER.n); // allocate a PL large enough to hold the n points in the buffer
    for(int i = 0; i<BUFFER.n; i++){points[i] = new G.V(BUFFER.points[i]);}
  }
  public void show(Graphics g){g.setColor(UC.inkColor); this.draw(g);}
  //----------------------------Ink.Buffer----------------------------
  public static class Buffer extends G.PL implements I.Show, I.Area{
    public static final int MAX = UC.inkBufferMax; // maximum size of buffer
    public int n; // how many points are actually in the buffer.
    private Buffer(){super(MAX);} // create the PL with MAX points
    public void add(int x, int y){if(n<MAX){points[n].set(x,y); n++;}} // adds new point to buffer
    public void clear(){n = 0;}
    public void show(Graphics g){this.drawN(g, n);} // Only draw existing points
    public boolean hit(int x, int y){return true;} // any point COULD go into ink
    public void dn(int x, int y){clear(); add(x,y);} // add first point
    public void drag(int x, int y){add(x,y);} // add each point as it comes in
    public void up(int x, int y){add(x,y);}
  }

  //---------------------------List----------------------------------
  public static class List extends ArrayList<Ink> implements I.Show{
    public void show(Graphics g){for(Ink ink : this){ink.show(g);}}
  }
}
