import com.apple.eawt.Application;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {

  public static void main(String[] args) throws IOException {
    BufferedImage myImg = ImageIO.read(Main.class.getResourceAsStream("mipng.png"));
    Main.setIcon(myImg);
    //utilizes macOS menu bar for menu items
    if (System.getProperty("os.name").contains("Mac")) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("apple.awt.application.name", "Auto Emailer");
    }
    new ViewImpl(myImg);
  }

  private static boolean exists() {
    try {
      Class.forName("com.apple.eawt.Application", false, null);
      return true;
    } catch (ClassNotFoundException exception) {
      return false;
    }
  }

  public static void setIcon(BufferedImage icn) {
    if (exists()) {
      Application.getApplication().setDockIconImage(icn);
    }
  }
}


