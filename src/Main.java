import com.apple.eawt.Application;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Main {

  public static void main(String[] args) throws IOException {

    InputStream imgStream = Main.class.getResourceAsStream("mailIconPNG.png");
    BufferedImage myImg = ImageIO.read(imgStream);
    Main m = new Main();
    //utilizes macOS menu bar for menu items
    if (System.getProperty("os.name").contains("Mac")) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("apple.awt.application.name", "Auto Emailer");
    }
    new ViewImpl(myImg);
    m.setIcon(myImg);
  }

  private boolean exists() {
    try {
      Class.forName("com.apple.eawt.Application", false, null);
      return true;
    } catch (ClassNotFoundException exception) {
      return false;
    }
  }

  public void setIcon(BufferedImage icn) {
    if (exists()) {
      Application.getApplication().setDockIconImage(icn);
    }
  }
}


