import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

public class ViewImpl extends JFrame {

  private String name;
  private final JFrame frame;
  private final JLabel recipientLabel;
  private final JLabel headingLabel;
  private final JTextArea textBox;
  private final TitledBorder titledBorder;
  private final JTextField recipientTextField;
  private final JTextField headerTextField;
  private final JTextField countTextField;
  private final JButton sendButton;
  private final JButton resetButton;
  private final JButton stopButton;
  private final JMenuItem counterMenuItem;
  private final JMenuItem taskModeMenuItem;
  private final JMenuItem emailToMenuItem;
  private final JMenuItem darkLightMenuItem;
  private final JPanel centerPanel;
  private final JPanel bottomPanel;
  private final JPanel topPanel;
  private final JMenuItem resetMenuItem;
  private final JMenuItem undoMenuItem;
  private final JMenuItem redoMenuItem;
  private final Preferences pp;
  private final Color appleWhite;
  private final LinkedList<String> undoLinkedList;
  private boolean isStopped;
  private boolean taskMode;
  private boolean showCounter;
  private boolean emailAnyone;
  private boolean isDarkMode;
  private int currentUndoIndex;

  ViewImpl(Image i) {

    // sets the name of the person to email - leave blank unless dedicated
    this.name = "";

    frame = new JFrame(name.concat(" Auto Emailer"));
    super.setIconImage(i);
    frame.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
    frame.setSize(480, 390);
    frame.setLocationRelativeTo(null);

    countTextField = new JTextField();
    countTextField.setText("1");
    countTextField.setColumns(3);
    stopButton = new JButton("Stop");
    this.isStopped = false;

    taskMode = false;
    undoLinkedList = new LinkedList<>();
    currentUndoIndex = 0;

    //Creating the MenuBar and adding components
    JMenuBar mb = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenuItem advancedMenu = new JMenu("Advanced");
    JMenu helpMenu = new JMenu("Help");
    JMenu versionMenu = new JMenu("Version 4.1.7");
    mb.add(fileMenu);
    mb.add(advancedMenu);
    mb.add(helpMenu);
    mb.add(versionMenu);
    JMenuItem m11 = new JMenuItem("Open");
    JMenuItem m12 = new JMenuItem("Save as");
    emailToMenuItem = new JMenuItem("Email me");
    counterMenuItem = new JMenuItem("Show counter");
    taskModeMenuItem = new JMenuItem("Enable task mode");
    resetMenuItem = new JMenuItem("Reset");
    undoMenuItem = new JMenuItem("Undo");
    redoMenuItem = new JMenuItem("Redo");
    fileMenu.add(m11);
    fileMenu.add(m12);
    frame.add(resetMenuItem);
    frame.add(undoMenuItem);
    frame.add(redoMenuItem);
    advancedMenu.add(counterMenuItem);
    advancedMenu.add(emailToMenuItem);
    advancedMenu.add(taskModeMenuItem);
    showCounter = false;
    emailAnyone = true;
    taskModeMenuItem.setAccelerator(KeyStroke
        .getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    emailToMenuItem.setAccelerator(KeyStroke
        .getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    resetMenuItem.setAccelerator(KeyStroke
        .getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    undoMenuItem.setAccelerator(KeyStroke
        .getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    redoMenuItem.setAccelerator(KeyStroke
        .getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

    stopButton.setVisible(false);
    countTextField.setVisible(false);

    //Creating the components
    bottomPanel = new JPanel(); // the panel is not visible in output
    headingLabel = new JLabel("Enter heading");
    recipientTextField = new JTextField(16); // accepts upto 10 characters
    headerTextField = new JTextField(20); // accepts upto 10 characters
    sendButton = new JButton("Save");

    resetButton = new JButton("Reset");

    sendButton.setBackground(new Color(255, 255, 255));
    resetButton.setBackground(new Color(238, 238, 238));
    stopButton.setBackground(Color.white);
    recipientLabel = new JLabel();

    // Text Area at the Center
    centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
    textBox = new JTextArea();
    textBox.setLineWrap(true);
    textBox.setWrapStyleWord(true);
    titledBorder = BorderFactory.createTitledBorder("<HTML>Body of the email:</HTML>");
    titledBorder.setTitleColor(Color.DARK_GRAY);
    centerPanel.setBorder(titledBorder);
    JScrollPane scrollPane = new JScrollPane(textBox);
    topPanel = new JPanel();
    topPanel.setLayout(new FlowLayout());

    textBox.setTabSize(2);
    updateRecipientComponents();

    centerPanel.add(scrollPane);
    topPanel.add(headingLabel);
    topPanel.add(headerTextField);
    bottomPanel.add(recipientLabel);
    bottomPanel.add(recipientTextField);
    bottomPanel.add(sendButton);
    bottomPanel.add(resetButton);
    bottomPanel.add(countTextField);
    bottomPanel.add(stopButton);

    // Set theme - experimental
    appleWhite = new Color(238, 238, 238);
    darkLightMenuItem = new JMenuItem("Dark mode");
    darkLightMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    advancedMenu.add(darkLightMenuItem);
    pp = ProgramPreferences.userRoot().node("prefs");
    isDarkMode = !pp.get("dark", "false").equals("false");

    activeColorTheme();
    String ppTextBoxString = pp.get("textbox", "empty");
    if (!ppTextBoxString.equals("empty")) {
      textBox.setText(ppTextBoxString);
    }
    String ppHeaderBoxString = pp.get("headerbox", "empty");
    if (!ppTextBoxString.equals("empty")) {
      headerTextField.setText(ppHeaderBoxString);
    }
    String ppRecipientString = pp.get("recipientbox", "");
    if (!ppRecipientString.isBlank()) {
      sendButton.setText("Send");
    }

    addToTheUndoList();

    initializeActionListeners();
    initializeKeyListeners();
    initializeMouseListeners();

    //Adding Components to the frame.
    frame.getContentPane().add(BorderLayout.SOUTH, bottomPanel);
    frame.getContentPane().add(BorderLayout.CENTER, centerPanel);
    frame.setJMenuBar(mb);
    frame.getContentPane().add(BorderLayout.NORTH, topPanel);
    frame.setVisible(true);

  } // end constructor ----------------------------


  /**
   * Updates the recipient components.
   */
  private void updateRecipientComponents() {
    // changes the recipient label
    if (name.isEmpty()) {
      recipientLabel.setText("Enter recipient:");
      recipientTextField.setText("");
      recipientTextField.setVisible(true);
    } else {
      recipientLabel.setText("Send to ".concat(name).concat(":"));
      recipientTextField.setText(personToEmailD(name));
      recipientTextField.setVisible(false);
    }
    //frame.setName(" Auto Emailer");
  }

  private void initializeMouseListeners() {
    sendButton.addMouseListener(applyDarkThemeMouseListenerOnButton(sendButton));
    resetButton.addMouseListener(applyDarkThemeMouseListenerOnButton(resetButton));
    stopButton.addMouseListener(applyDarkThemeMouseListenerOnButton(stopButton));
  }

  private MouseListener applyDarkThemeMouseListenerOnButton(JButton myButton) {
    return new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {

      }

      @Override
      public void mousePressed(MouseEvent e) {
        if (isDarkMode) {
          myButton.setBackground(new Color(11, 0, 168));
        } else {
          myButton.setBackground(new Color(20, 100, 210));
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (isDarkMode) {
          myButton.setBackground(Color.darkGray);
        } else {
          myButton.setBackground(new Color(250, 250, 250));
        }
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        if (isDarkMode) {
          myButton.setBackground(Color.GRAY);
        } else {
          myButton.setBackground(Color.lightGray);
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        if (isDarkMode) {
          myButton.setBackground(Color.DARK_GRAY);
        } else {
          myButton.setBackground(new Color(250, 250, 250));
        }
      }
    };
  }

  private void initializeActionListeners() {
    sendButton.addActionListener(e -> {
      String r = recipientTextField.getText().strip(); //recipient
      String t = textBox.getText();
      String h = headerTextField.getText();
      if (h.isEmpty() || t.isEmpty()) {
        error("Empty fields!");
        return;
      }

      if (r.isBlank()) {
        try {
          String newH;
          newH = h.replace(":", "");
          FileWriter fw = new FileWriter(newH);
          fw.write(t);
          fw.close();
          if (isDarkMode) {
            recipientLabel.setForeground(new Color(0, 190, 80));
          } else {
            recipientLabel.setForeground(new Color(0, 134, 62));
          }
          recipientLabel.setText("Saved!");
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
        return;
      } else if (r.length() < 7 || !r.contains("@")) {
        error("Check recipient!");
        return;
      }

      recipientLabel.setForeground(Color.BLUE);
      recipientLabel.setText("Sending...");

      SwingWorker<?, ?> sw = new SwingWorker<>() {
        @Override
        protected Object doInBackground() {
          try {
            sendMessage(r, h, t);
          } catch (Exception exception) {
            error();
          }
          return null;
        }
      };
      sw.execute();
    });

    emailToMenuItem.addActionListener(e -> {
      emailAnyone = !emailAnyone;
      if (emailAnyone) {
        emailToMenuItem.setText("Email me");
        name = "";
        sendButton.setText("Save");
      } else {
        emailToMenuItem.setText("Email anyone");
        name = "Zain";
        sendButton.setText("Send");
      }
      updateRecipientComponents();
      setTitle();
    });

    darkLightMenuItem.addActionListener(e -> {
      isDarkMode = !isDarkMode;
      activeColorTheme();
    });

    resetMenuItem.addActionListener(e -> resetFields());

    stopButton.addActionListener(e -> {
      this.isStopped = true;
    });

    counterMenuItem.addActionListener(e -> {
      showCounter = !showCounter;
      if (showCounter) {
        counterMenuItem.setText("Hide counter");
        stopButton.setVisible(true);
        countTextField.setVisible(true);
        if (frame.getWidth() < 610) {
          frame.setSize(610, frame.getHeight());
        }
      } else {
        counterMenuItem.setText("Show counter");
        stopButton.setVisible(false);
        countTextField.setVisible(false);
        frame.setSize(480, frame.getHeight());
      }
    });

    undoMenuItem.addActionListener(e -> {
      if (textBox.hasFocus()) {
        undoActionOnTextBox();
      }
    });

    redoMenuItem.addActionListener(e -> {
      if (textBox.hasFocus()) {
        redoActionOnTextBox();
      }
    });

    taskModeMenuItem.addActionListener(e -> {
      taskMode = !taskMode;
      taskModeMenuAction();
    });
    resetButton.addActionListener(e -> resetFields());
  }

  /**
   * The action for when calling undo.
   */
  private void undoActionOnTextBox() {
    if (currentUndoIndex >= undoLinkedList.size()) {
      return;
    }
    if (currentUndoIndex == 0 && !undoLinkedList.get(0).equals(textBox.getText())) {
      addToTheUndoList();
      textBox.setText(undoLinkedList.get(currentUndoIndex + 1));
      currentUndoIndex += 2;
    }
    else {
      textBox.setText(undoLinkedList.get(currentUndoIndex));
      currentUndoIndex++;
    }
  }

  /**
   * Saves the current state into the undo system when this method is called.
   */
  private void addToTheUndoList() {
    undoLinkedList.push(textBox.getText());
    if(undoLinkedList.size() > 150) {
      undoLinkedList.removeLast();
    }
  }

  /**
   * The action when calling a redo. Should restore the previous text version.
   */
  private void redoActionOnTextBox() {
    if (currentUndoIndex > 0) {
      currentUndoIndex--;
      textBox.setText(undoLinkedList.get(currentUndoIndex));
    }
  }

  /**
   * Clears the 'old' redo data as there is new data being written. Assumes that the
   * currentUndoIndex is not 0.
   */
  private void clearOldRedoData() {
    while (currentUndoIndex > 0) {
      undoLinkedList.pop();
      currentUndoIndex--;
    }

  }

  private void activeColorTheme() {
    // removes the preset button paint
    sendButton.setOpaque(true);
    sendButton.setBorderPainted(false);
    resetButton.setOpaque(true);
    resetButton.setBorderPainted(false);
    stopButton.setOpaque(true);
    stopButton.setBorderPainted(false);

    if (isDarkMode) {
      darkLightMenuItem.setText("Light mode");
      bottomPanel.setBackground(Color.BLACK);
      centerPanel.setBackground(Color.BLACK);
      frame.setBackground(Color.BLACK);
      frame.setForeground(appleWhite);
      topPanel.setBackground(Color.BLACK);
      textBox.setForeground(Color.white);
      recipientTextField.setForeground(Color.white);
      headerTextField.setForeground(Color.white);
      countTextField.setForeground(Color.white);
      textBox.setBackground(Color.DARK_GRAY);
      recipientTextField.setBackground(Color.DARK_GRAY);
      headerTextField.setBackground(Color.DARK_GRAY);
      countTextField.setBackground(Color.DARK_GRAY);
      titledBorder.setTitleColor(appleWhite);
      headingLabel.setForeground(appleWhite);
      textBox.setCaretColor(appleWhite);
      recipientTextField.setCaretColor(appleWhite);
      headerTextField.setCaretColor(appleWhite);
      countTextField.setCaretColor(appleWhite);
      stopButton.setBackground(Color.DARK_GRAY);
      sendButton.setBackground(Color.DARK_GRAY);
      resetButton.setBackground(Color.DARK_GRAY);
      sendButton.setRolloverEnabled(true);
      stopButton.setForeground(appleWhite);
      sendButton.setForeground(appleWhite);
      resetButton.setForeground(appleWhite);
      if (recipientLabel.getForeground().equals(new Color(149, 0, 0))) {
        recipientLabel.setForeground(new Color(245, 50, 50));
      } else if (recipientLabel.getForeground().equals(new Color(0, 134, 62))) {
        recipientLabel.setForeground(new Color(0, 190, 80));
      } else {
        recipientLabel.setForeground(appleWhite);
      }
      pp.put("dark", "true");
    } else {
      darkLightMenuItem.setText("Dark mode");
      frame.setBackground(appleWhite);
      frame.setForeground(Color.black);
      bottomPanel.setBackground(appleWhite);
      centerPanel.setBackground(appleWhite);
      topPanel.setBackground(appleWhite);
      textBox.setForeground(Color.black);
      recipientTextField.setForeground(Color.black);
      headerTextField.setForeground(Color.black);
      countTextField.setForeground(Color.black);
      textBox.setBackground(Color.white);
      recipientTextField.setBackground(Color.white);
      headerTextField.setBackground(Color.white);
      countTextField.setBackground(Color.white);
      titledBorder.setTitleColor(new Color(64, 64, 64));
      headingLabel.setForeground(Color.black);
      textBox.setCaretColor(Color.black);
      recipientTextField.setCaretColor(Color.black);
      headerTextField.setCaretColor(Color.black);
      countTextField.setCaretColor(Color.black);
      stopButton.setBackground(new Color(250, 250, 250));
      sendButton.setBackground(new Color(250, 250, 250));
      resetButton.setBackground(new Color(250, 250, 250));
      stopButton.setForeground(Color.black);
      sendButton.setForeground(Color.black);
      resetButton.setForeground(Color.black);
      if (recipientLabel.getForeground().equals(new Color(245, 50, 50))) {
        recipientLabel.setForeground(new Color(149, 0, 0));
      } else if (recipientLabel.getForeground().equals(new Color(0, 190, 80))) {
        recipientLabel.setForeground(new Color(0, 134, 62));
      } else {
        recipientLabel.setForeground(Color.black);
      }
      pp.put("dark", "false");
    }
  }

  /**
   * Task mode action.
   */
  private void taskModeMenuAction() {
    Date time = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
    String generatedHeader = sdf.format(time) + "'s Plan: ";
    if (taskMode) {
      taskModeMenuItem.setText("Disable Task Mode");
      if (textBox.getText().isEmpty()) {
        textBox.append("-");
      }
      if (headerTextField.getText().isBlank()) {
        headerTextField.setText(generatedHeader);
      }
    } else {
      taskModeMenuItem.setText("Enable Task Mode");
      if (textBox.getText().equals("-")) {
        textBox.setText("");
      }
      if (headerTextField.getText().equals(generatedHeader)) {
        headerTextField.setText("");
      }
    }
    setTitle();
  }

  /**
   * Sets the title depending on the application's settings.
   */
  private void setTitle() {
    StringBuilder sb = new StringBuilder();
    if (emailAnyone) {
      sb.append("Auto Emailer");
    } else {
      sb.append("Auto Email Me");
    }
    if (taskMode) {
      sb.append(" - task mode");
    }
    frame.setTitle(sb.toString());
  }

  /**
   * Initializes all of the key listeners
   */
  private void initializeKeyListeners() {

    KeyListener allComponentsKeyListener = new KeyListener() {
      final Set<Integer> pressedKeys = new HashSet<>();

      @Override
      public void keyTyped(KeyEvent e) {

      }

      @Override
      public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        if (pressedKeys.size() < 3 && pressedKeys.contains(157) && pressedKeys.contains(87)) {
          System.exit(0);
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
      }
    };

    KeyListener textBoxKeyListener = new KeyListener() {
      final Set<Integer> pressedKeys = new HashSet<>();

      @Override
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_SPACE || e.getKeyChar() == KeyEvent.VK_TAB
            || e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
          addToTheUndoList();
          clearOldRedoData();
        } else if (e.getKeyChar() == (KeyEvent.VK_TAB) && !pressedKeys
            .contains(KeyEvent.VK_SHIFT)) {
          if (taskMode) {
            indentLine(true);
          } else if (textBox.getCaretPosition() == textBox.getText().length()) {
            textBox.setText(textBox.getText().substring(0, textBox.getText().length() - 1));
            if (recipientTextField.getText().isBlank()) {
              recipientTextField.grabFocus();
            } else {
              sendButton.grabFocus();
            }
          }
        } else if (taskMode && e.getKeyChar() == KeyEvent.VK_ENTER) {
          StringBuilder sb = new StringBuilder();
          String textBoxText = textBox.getText();
          int cp = textBox.getCaretPosition();
          int addAnIndex = 0;
          if (!textBoxText.isBlank() && textBoxText.substring(cp - 2, cp).contains("-")) {
            sb.append(textBoxText, 0, cp - 2);
            addAnIndex = -2;
          } else {
            sb.append(textBoxText, 0, cp).append("-");
            addAnIndex++;
          }
          sb.append(textBoxText.substring(cp));
          textBox.setText(sb.toString());
          textBox.setCaretPosition(cp + addAnIndex);
        }
        pp.put("textbox", textBox.getText());
        if (recipientLabel.getText().startsWith("Sa") || recipientLabel.getText()
            .startsWith("Sent")) {
          resetLabel();
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        // if pressing tab
        if (pressedKeys.size() < 3 && pressedKeys.contains(KeyEvent.VK_SHIFT) && pressedKeys
            .contains(KeyEvent.VK_TAB)) {
          indentLine(false);
        } else if (pressedKeys.contains(KeyEvent.VK_META) && pressedKeys
            .contains(KeyEvent.VK_SHIFT)) {
          if (pressedKeys.contains(KeyEvent.VK_Z)) {
            redoActionOnTextBox();
          } else if (pressedKeys.contains(KeyEvent.VK_R)) {
            textBox.setText("");
          }
        }
      }


      @Override
      public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
      }
    };

    KeyListener recipientKeyListener = new KeyListener() {
      final Set<Integer> pressedKeys = new HashSet<>();

      @Override
      public void keyTyped(KeyEvent e) {
        String s = recipientTextField.getText();
        String sbt = sendButton.getText();
        if (s.isBlank() && (e.getKeyChar() == KeyEvent.VK_SPACE
            || e.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
          sendButton.setText("Save");
        } else if (sbt.indexOf("a") == 1) {
          sendButton.setText("Send");
        }
        pp.put("recipientbox", recipientTextField.getText());
      }

      @Override
      public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        if (pressedKeys.contains(KeyEvent.VK_META) && pressedKeys
            .contains(KeyEvent.VK_SHIFT)) {
          if (pressedKeys.contains(KeyEvent.VK_R)) {
            recipientTextField.setText("");
          }
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
      }
    };

    KeyListener headerTextFieldKeyListener = new KeyListener() {
      final Set<Integer> pressedKeys = new HashSet<>();

      @Override
      public void keyTyped(KeyEvent e) {
        pp.put("headerbox", headerTextField.getText());
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
          textBox.grabFocus();
        } else if (recipientLabel.getText().startsWith("Sa") || recipientLabel.getText()
            .startsWith("Sent")) {
          resetLabel();
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        if (pressedKeys.contains(KeyEvent.VK_META) && pressedKeys
            .contains(KeyEvent.VK_SHIFT)) {
          if (pressedKeys.contains(KeyEvent.VK_R)) {
            headerTextField.setText("");
          }
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
      }
    };

    headerTextField.addKeyListener(allComponentsKeyListener);
    headerTextField.addKeyListener(headerTextFieldKeyListener);
    textBox.addKeyListener(allComponentsKeyListener);
    textBox.addKeyListener(textBoxKeyListener);
    recipientTextField.addKeyListener(allComponentsKeyListener);
    recipientTextField.addKeyListener(recipientKeyListener);
    sendButton.addKeyListener(allComponentsKeyListener);
    resetButton.addKeyListener(allComponentsKeyListener);
    countTextField.addKeyListener(allComponentsKeyListener);
  }

  private void indentLine(boolean b) {
    // code to remove a tab from the middle of text
    String oldTextBoxText = textBox.getText();
    String text;
    int carrotPositioning = textBox.getCaretPosition();
    if (oldTextBoxText.length() == 0) {
      return;
    }
    if (b) {
      //removing the inputted 'tab'
      if (oldTextBoxText.length() == carrotPositioning) {
        textBox.setText(oldTextBoxText.substring(0, oldTextBoxText.length() - 1));
        text = textBox.getText();
      } else {
        text = oldTextBoxText.substring(0, carrotPositioning - 1)
            + oldTextBoxText.substring(carrotPositioning);
      }
    } else {
      text = textBox.getText();
    }
    String[] textArray = text.split("\n");
    if (textArray.length == 0) {
      return;
    }
    int totalSoFar = 0;
    int arrayIndex = 0;
    //int carrotToEnd = 0;
    for (String s : textArray) {
      totalSoFar += s.length() + 1;
      if (textBox.getCaretPosition() <= totalSoFar) {
        //carrotToEnd = totalSoFar - textBox.getCaretPosition();
        break;
      }
      arrayIndex++;
    }

    StringBuilder newText = new StringBuilder();
    int moveCursorBack = 0;

/*            if(text.length() > textBox.getColumns()) {
              StringBuilder newString = new StringBuilder();
              int j = 0;
              for (int i = text.length()/ textBox.getWidth() - 5 ; i > 0; i--) {
                int newLength = Math.min(textBox.getWidth()*j - 5 * (j + 1), text.length());
                newString.append(text.substring(textBox.getWidth() - 5 * j, newLength));
                newString.append("\t");
                j++;
              }
              text = newString.toString();
            }*/

    if (arrayIndex < textArray.length) {
      moveCursorBack += (textArray[arrayIndex].length() + 1);
    } else {
      moveCursorBack++;
    }
    int beginningCutOff = totalSoFar - moveCursorBack;
    if (b) {
      newText.append(text, 0, beginningCutOff);
      newText.append("\t");
      newText.append(text.substring(beginningCutOff));
      textBox.setText(newText.toString());
      textBox.setCaretPosition(carrotPositioning);
    } else {
      newText.append(text, 0, beginningCutOff);
      newText.append(text.substring(beginningCutOff + 1));
      textBox.setText(newText.toString());
      textBox.setCaretPosition(carrotPositioning - 1);
    }

  }


  private void sendMessage(String recipient, String htfText, String taText) throws Exception {
    int times;
    try {
      times = Integer.parseInt(countTextField.getText());
    } catch (NumberFormatException nfe) {
      recipientLabel.setText("Count failed!");
      return;
    }
    if (times > 1) {

      recipientLabel.setText("Sent 0/" + times);
      for (int i = 1; i <= times; i++) {
        if (isStopped) {
          isStopped = false;
          error("Stopped (" + (i - 1) + ")");
          return;
        }
        if (i > 1) {
          JavaMailUtil.sendMail(recipient, htfText + " " + i, taText);
        } else {
          JavaMailUtil.sendMail(recipient, htfText, taText);
        }
        recipientLabel.setText("Sent " + i + "/" + times);
        if (i != times) {
          Thread.sleep(5700);
        }
      }
    } else {
      JavaMailUtil.sendMail(recipient, htfText, taText);
    }

    //JavaMailUtil.sendMail("sendreplyza@gmail.com", htfText + " - " + name, taText);
    if (isDarkMode) {
      recipientLabel.setForeground(new Color(0, 190, 80));
    } else {
      recipientLabel.setForeground(new Color(0, 134, 62));
    }
    StringBuilder rsb = new StringBuilder();
    if (times == 1) {
      rsb.append("Sent");
    } else {
      rsb.append("Sent (").append(times).append(")");
    }
    if (!this.name.isEmpty()) {
      rsb.append(" to ").append(name).append("!");
    } else {
      rsb.append("!");
    }
    recipientLabel.setText(rsb.toString());
  }

  /**
   * The database of people who can be emailed.
   *
   * @param s the name of the person
   * @return the email address
   */
  private String personToEmailD(String s) {
    if (s.substring(0, 1).equalsIgnoreCase("k")) {
      return "keith.kondapi@gmail.com";
    }
    if (s.substring(0, 1).equalsIgnoreCase("a")) {
      return "replyali10@gmail.com";
    }
    if (s.substring(0, 1).equalsIgnoreCase("z")) {
      return "reply2zain@gmail.com";
    } else {
      throw new IllegalArgumentException("Cannot find person in database.");
    }
  }


  private void resetFields() {
    resetLabel();
    recipientTextField.setText("");
    headerTextField.setText("");
    textBox.setText("");
    taskModeMenuAction();
    pp.remove("textbox");
    pp.remove("headerbox");
  }

  private void resetLabel() {
    if (isDarkMode) {
      recipientLabel.setForeground(appleWhite);
    } else {
      recipientLabel.setForeground(Color.BLACK);
    }
    if (name.isEmpty()) {
      recipientLabel.setText("Enter recipient:");
    } else {
      recipientLabel.setText("Send to " + name);
    }
  }

  private void error() {
    error("Failed!");
  }

  private void error(String s) {
    recipientLabel.setText(s);
    if (isDarkMode) {
      recipientLabel.setForeground(new Color(245, 50, 50));
    } else {
      recipientLabel.setForeground(new Color(149, 0, 0));
    }
  }

}

