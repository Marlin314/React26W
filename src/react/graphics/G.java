package react.graphics;
import java.awt.*;
import java.util.Random;

public class G{
  public static Random RND = new Random();
  public static int rnd(int max){return RND.nextInt(max);}
  public static Color rndColor(){return new Color(rnd(256),rnd(256),rnd(256)); }
}
