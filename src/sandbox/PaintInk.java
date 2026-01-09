package sandbox;
import react.graphics.*;
import react.*;
import java.awt.*;

public class PaintInk extends WinApp{
  public PaintInk(){
    super("Paint Ink", UC.mainWindowWidth, UC.mainWindowHeight);
  }
  @Override
  public void paintComponent(Graphics g){
    G.clearBack(g);
    g.setColor(Color.red); g.fillRect(100,100,100,100); // the graphics equivalent of "Hello World"
  }

  public static void main(String[] args){PANEL=new PaintInk();WinApp.launch();}
}

