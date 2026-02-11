package react.reactions;
import react.I;
import react.UC;
import react.graphics.G;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class Shape implements Serializable{
  public Prototype.List prototypes = new Prototype.List();
  public String name;
  
  public static Shape.Database DB = Shape.Database.load();
  public static Shape DOT = DB.get("DOT"); // fetch the DOT out. It is special
  public static Collection<Shape> LIST = DB.values(); // list of all shapes in the DB
  public static Trainer TRAINER = new Trainer();
  
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
    private static String filename = UC.shapeDatabaseFileName;

    private Database(){super(); String dot = "DOT"; put(dot, new Shape(dot));} // make sure DOT exists
    private Shape forceGet(String name){ // always returns Shape..
      if(!DB.containsKey(name)){DB.put(name, new Shape(name));} //..adds new if necessary
      return DB.get(name);
    }
    public void train(String name, Ink.Norm norm){if(isLegal(name)){forceGet(name).prototypes.train(norm);}}

    public static Database load(){
      Database res;
      try{
        System.out.println("attempting DB load..");
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
        res = (Shape.Database) ois.readObject();
        System.out.println("Successful load - found" + res.keySet());
        ois.close();
      } catch(Exception e) {
        System.out.println("Load failed.");
        System.out.println(e);
        res = new Database();
      }
      return res;
    }

    public void save(){
      try{
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
        oos.writeObject(DB);
        System.out.println("Saved " + filename);
        oos.close();
      } catch(Exception e) {
        System.out.println("Failed database save");
        System.out.println(e);
      }
    }
    
    boolean isKnown(String name){return containsKey(name);}
    boolean isUnknown(String name){return !containsKey(name);}
    boolean isLegal(String name){return !name.equals("") && !name.equals("DOT");}
  }
  
  //------------------Prototype-----------------
  public static class Prototype extends Ink.Norm implements Serializable{
    int nBlend = 1;
    
    public void blend(Ink.Norm norm){blend(norm, nBlend); nBlend++;}
    
    //-------------------Prototype.List-----------
    public static class List extends ArrayList<Prototype> implements Serializable{

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

      public void train(Ink.Norm norm){ // either blend or add New
        if(bestDist(norm) < UC.noMatchDist){ // we found a match so blend
          bestMatch.blend(norm);
        }else{
          add(new Shape.Prototype()); // didn't match so add a new one (from Ink.BUFFER)
        }
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

  //----------------------Trainer---the App -----------------------
  public static class Trainer implements I.Show, I.Area{

    private Trainer(){} // Singlton

    public static String UNKNOWN = " <- this name is currently Unknown.";
    public static String ILLEGAL = " <-this name is NOT a legal Shape name.";
    public static String KNOWN   = " <-this is a known shape.";

    public static String curName = "";
    public static String curState = ILLEGAL;

    public static Shape.Prototype.List pList = new Shape.Prototype.List();

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

    // I.Show functions
    public void show(Graphics g){
      G.clearBack(g);
      g.setColor(Color.BLACK);
      g.drawString(curName, 600,30);
      g.drawString(curState, 700,30);
      g.setColor(Color.RED);
      Ink.BUFFER.show(g);
      if(pList != null){pList.show(g);}
    }

    // I.Area functions
    public boolean hit(int x, int y){return true;}
    public void dn(int x, int y){Ink.BUFFER.dn(x,y);}
    public void drag(int x, int y){Ink.BUFFER.drag(x,y);}
    public void up(int x, int y){
      Ink.BUFFER.up(x,y);
      Ink ink = new Ink();
      Shape.DB.train(curName, ink.norm); // safe because legal name test is done in Database
      setState(); // possibly convert previously UNKNOWN to KNOWN
    }

    public void keyTyped(KeyEvent e) {
      char c = e.getKeyChar();
      System.out.println("Typed: " + c); // debug
      if(c == 0x0D || c == 0x0A){
        DB.save();}
      curName = (c == ' ' || c == 0x0D || c == 0x0A)? "": curName + c; // x0D & x0A are ascii CR & LF
      setState();
    }
  }
}
