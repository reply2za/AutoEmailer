import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

public class ViewImpl extends JFrame {

  JLabel recipientL;
  String name;
  JTextArea textBox;
  JTextField rtf;
  JTextField htf;

  ViewImpl() {
    this.name = "";
    JFrame frame = new JFrame(name.concat(" Emailer"));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(480, 390);

    //Creating the MenuBar and adding components
    /*JMenuBar mb = new JMenuBar();
    JMenu m1 = new JMenu("FILE");
    JMenu m2 = new JMenu("Help");
    mb.add(m1);
    mb.add(m2);
    JMenuItem m11 = new JMenuItem("Open");
    JMenuItem m22 = new JMenuItem("Save as");
    m1.add(m11);
    m1.add(m22);
*/
    //Creating the panel at bottom and adding components
    JPanel bottomPanel = new JPanel(); // the panel is not visible in output
    JLabel headingL = new JLabel("Enter heading");
    rtf = new JTextField(16); // accepts upto 10 characters
    htf = new JTextField(20); // accepts upto 10 characters
    JButton send = new JButton("Send");
    JButton reset = new JButton("Reset");

    // Text Area at the Center
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
    textBox = new JTextArea();
    textBox.setLineWrap(true);
    textBox.setWrapStyleWord(true);
    TitledBorder b = BorderFactory.createTitledBorder("<HTML>Body of the email:</HTML>");
    b.setTitleColor(Color.DARK_GRAY);
    centerPanel.setBorder(b);
    JScrollPane scrollPane = new JScrollPane(textBox);
    centerPanel.add(scrollPane);




    JPanel headingBar = new JPanel();
    headingBar.setLayout(new FlowLayout());
    headingBar.add(headingL);
    headingBar.add(htf);

    if (name.isEmpty()) {
      this.recipientL = new JLabel("Enter recipient:");
      bottomPanel.add(recipientL);
      bottomPanel.add(rtf);
    } else {
      this.recipientL = new JLabel("Send to ".concat(name).concat(":"));
      rtf.setText(personToEmailD(name));
    bottomPanel.add(recipientL);
    }
    bottomPanel.add(send);
    bottomPanel.add(reset);

    send.addActionListener(e -> {
      String r = rtf.getText();
      if (r.length() < 7 || !r.contains("@")) {
        recipientL.setText("Failed!");
        recipientL.setForeground(new Color(149,0,0));
        return;
      }
      recipientL.setText("sending...");
      recipientL.setForeground(Color.BLUE);

      SwingWorker<?, ?> sw = new SwingWorker<>() {
        @Override
        protected Object doInBackground() {
          try {
            sendMessage(rtf.getText(), htf.getText(), textBox.getText());
          } catch (Exception exception) {
            recipientL.setText("Failed!");
            recipientL.setForeground(new Color(149,0,0));
          }
          return null;
        }
      };

      sw.execute();
    });


    textBox.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        recipientL.setForeground(Color.BLACK);
      }

      @Override
      public void focusLost(FocusEvent e) {

      }
    });

    htf.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        recipientL.setForeground(Color.black);
      }

      @Override
      public void focusLost(FocusEvent e) {

      }
    });

    reset.addActionListener(e -> {
      resetFields();
    });

    //Adding Components to the frame.
    frame.getContentPane().add(BorderLayout.SOUTH, bottomPanel);
    frame.getContentPane().add(BorderLayout.CENTER, centerPanel);
    frame.getContentPane().add(BorderLayout.NORTH, headingBar);
    frame.setVisible(true);
  }


  private void sendMessage(String text, String htfText, String taText) throws Exception {
    JavaMailUtil.sendMail(text, htfText, taText);
    //JavaMailUtil.sendMail("sendreplyza@gmail.com", htfText + " - " + name, taText);
    recipientL.setForeground(new Color(0,134,62));
    if (this.name.isEmpty()) {
      recipientL.setText("Sent!");
    } else {
      recipientL.setText("Sent to " + name + "!");
    }
    resetFields();
  }

  /**
   * The database of people who can be emailed.
   *
   * @param s the name of the person
   * @return the email address
   */
  private String personToEmailD(String s) {
    if (s.substring(0,1).equalsIgnoreCase("k")) {
      return "keith.kondapi@gmail.com";
    }
    if (s.substring(0,1).equalsIgnoreCase("a")) {
      return "replyali10@gmail.com";
    }
    if (s.substring(0,1).equalsIgnoreCase("z")) {
      return "reply2zain@gmail.com";
    } else {
      throw new IllegalArgumentException("Cannot find person in database.");
    }
  }


  private void resetFields() {
    recipientL.setForeground(Color.BLACK);
    if(name.isEmpty()) {
      recipientL.setText("Enter recipient:");
    } else {
    recipientL.setText("Send to " + name);
    }
    rtf.setText("");
    htf.setText("");
    textBox.setText("");
  }

}

