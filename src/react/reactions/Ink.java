package react.reactions;

import react.*;
import react.graphics.*;
import java.awt.*;
import java.util.ArrayList;

public class Ink extends G.PL implements I.Show{
  public static Buffer BUFFER = new Buffer();
  public static final int K = UC.normSampleSize;
  public Ink(){
    super(K); // create the PL that is this Ink.
    BUFFER.subSample(this);
  }
  public void show(Graphics g){g.setColor(UC.inkColor); this.draw(g);}
  //----------------------------Ink.Buffer----------------------------
  public static class Buffer extends G.PL implements I.Show, I.Area{
    public static final int MAX = UC.inkBufferMax; // maximum size of buffer
    public int n=0; // how many points are actually in the buffer.
    public G.BBox bbox = new G.BBox();
    
    private Buffer(){super(MAX);} // create the PL with MAX points

    public void add(int x, int y){if(n<MAX){points[n].set(x,y); n++; bbox.add(x,y);}} // update bbox too
    public void show(Graphics g){
      this.drawN(g, n); 
      //bbox.draw(g); // show bbox to test -- disable after test
    } 
    public void clear(){n = 0;}
    public boolean hit(int x, int y){return true;} // any point COULD go into ink
    public void dn(int x, int y){clear(); bbox.set(x,y); add(x,y);} // first point resets bbox
    public void drag(int x, int y){add(x,y);} // add each point as it comes in
    public void up(int x, int y){add(x,y);}
    public void subSample(G.PL pl){
      int k = pl.size();
      for(int i = 0; i<k; i++){pl.points[i].set(this.points[i*(n-1)/(k-1)]);}
    }
  }

  //---------------------------List----------------------------------
  public static class List extends ArrayList<Ink> implements I.Show{
    public void show(Graphics g){for(Ink ink : this){ink.show(g);}}
  }
}
