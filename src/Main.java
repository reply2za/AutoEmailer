import org.jsoup.nodes.Document;


public class Main {

  public static void main(String[] args) {
    //utilizes macOS menu bar for menu items
    if (System.getProperty("os.name").contains("Mac")) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("apple.awt.application.name", "Auto Emailer");
    }
    new ViewImpl();
  }
}


