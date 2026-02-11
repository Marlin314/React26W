package react.graphics;
import java.awt.*;
import java.io.Serializable;
import java.util.Random;

public class G{
  public static Random RND = new Random();
  public static int rnd(int max){return RND.nextInt(max);}
  public static Color rndColor(){return new Color(rnd(256),rnd(256),rnd(256)); }
  public static void clearBack(Graphics g){g.setColor(Color.WHITE); g.fillRect(0,0,5000,5000);}
  public static void drawCircle(Graphics g, int x, int y, int r){g.drawOval(x-r,y-r,r+r,r+r);}
  //-----------------------V------------------------
  public static class V implements Serializable {
    public int x,y;
    public static Transform T = new Transform();

    public V(int x, int y){this.set(x,y);}
    public V(V v){this.set(v);} // copy existing V
    
    public void set(V v){x = v.x; y=v.y;}
    public void set(int x, int y){this.x = x; this.y = y;}
    public void add(V v){x += v.x; y += v.y;} // vector addition
    public void setT(V v){set(v.tx(), v.ty());} // set to transformed value
    public int tx(){return x*T.n/T.d + T.dx;}
    public int ty(){return y*T.n/T.d + T.dy;}

    //-----------------------Transform------------------------
    public static class Transform{
      public int dx, dy, n, d;

      // helpers
      // OldWidth, OldHeight, NewWidth, NewHeight - used to compute scale
      private void setScale(int oW, int oH, int nW, int nH){
        n = (nW>nH)?nW:nH;  d = (oW>oH)?oW:oH;
      }
      private int getOff(int oZ, int oW, int nZ, int nW){
        
        return (-oZ-oW/2)*n/d + nZ + nW/2;
      }

      public void set(VS oVS, VS nVS){
        setScale(oVS.size.x, oVS.size.y, nVS.size.x, nVS.size.y);
        dx = getOff(oVS.loc.x, oVS.size.x, nVS.loc.x, nVS.size.x);
        dy = getOff(oVS.loc.y, oVS.size.y, nVS.loc.y, nVS.size.y);
      }
      public void set(BBox from, VS to){
        setScale(from.h.size(), from.v.size(), to.size.x, to.size.y);
        dx = getOff(from.h.lo, from.h.size(), to.loc.x, to.size.x);
        dy = getOff(from.v.lo, from.v.size(), to.loc.y, to.size.y);
      }
    }
    public void blend(V v, int k){set((k*x + v.x)/(k+1), (k*y + v.y)/(k+1));}
  }

  //-----------------------VS-----------------------
  public static class VS implements Serializable{
    public V loc, size;
    public VS(int x, int y, int w, int h){loc = new V(x,y); size = new V(w,h);}
    public void fill(Graphics g, Color c){g.setColor(c); g.fillRect(loc.x,loc.y,size.x,size.y);}
    public boolean hit(int x, int y){return loc.x<=x && loc.y <=y && x<=(loc.x+size.x) && y<=(loc.y+size.y);}
    public int xL(){return loc.x;}
    public int xH(){return loc.x + size.x;}
    public int xM(){return (loc.x + loc.x + size.x)/2;}
    public int yL(){return loc.y;}
    public int yH(){return loc.y + size.y;}
    public int yM(){return (loc.y + loc.y + size.y)/2;}
  }
  
  //-----------------------LoHi---------------------
  public static class LoHi{ // range from lo to hi
    public int lo, hi;
    public LoHi(int min, int max){lo = min; hi = max;}
    public void set(int v){lo = v; hi = v;} // first value into the set
    public void add(int v){if(v<lo){lo=v;} if(v>hi){hi=v;}} // move bounds if necessary
    public int size(){return (hi-lo) > 0 ? hi-lo : 1;}
  }
  //-----------------------BBox---------------------
  public static class BBox{ // Bounding Box
    LoHi h, v;  // horizontal and vertical ranges.
    public BBox(){h = new LoHi(0,0); v = new LoHi(0,0);}
    public void set(int x, int y){h.set(x); v.set(y);} // sets it to a single point
    public void add(int x, int y){h.add(x); v.add(y);}
    public void add(V v){add(v.x, v.y);}
    public VS getNewVS(){return new VS(h.lo, v.lo, h.hi-h.lo, v.hi-v.lo);}
    public void draw(Graphics g){g.drawRect(h.lo, v.lo, h.hi-h.lo, v.hi-v.lo);}
  }
  //-----------------------PL-----------------------
  public static class PL implements Serializable { // Polyline
    public V[] points;  // we keep an array of points
    public PL(int count){
      points = new V[count]; // allocate the array 
      for(int i = 0; i < count; i++) { points[i] = new V(0, 0); } // populate it with V objects
    }
    public int size(){return points.length;}
    public void drawN(Graphics g, int n){  // used to draw an initial portion of the full array
      for(int i = 1; i < n; i++) {
        g.drawLine(points[i - 1].x, points[i - 1].y, points[i].x, points[i].y);
      }
      // drawNDots(g, n); used to determine sample size
    }
    public void drawNDots(Graphics g, int n){
      g.setColor(Color.BLUE);
      for(int i=0; i<n; i++){drawCircle(g, points[i].x, points[i].y, 4);}
    }
    public void draw(Graphics g){drawN(g, points.length);} // draws the whole array.
    public void transform(){for(V v:points){v.setT(v);}}  
  }
  
  // ------ more static functions ---------
  // parabolic spline
  public static void spline(Graphics g, int ax, int ay, int bx, int by, int cx, int cy, int n){
    if(n==0){g.drawLine(ax, ay, cx, cy); return;}
    int abx = (ax + bx)/2, aby =(ay + by)/2;
    int bcx = (bx + cx)/2, bcy =(by + cy)/2;
    int abcx = (abx + bcx)/2, abcy =(aby + bcy)/2;
    spline(g,ax,ay,abx,aby,abcx,abcy,n-1);
    spline(g,abcx,abcy,bcx,bcy,cx,cy,n-1);
  }
}
