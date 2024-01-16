package forest;

import java.awt.EventQueue;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class CAWrapper extends JFrame {
  
  public final int CELLSIZE = 10;
  public final int FRAMESIZE = 600; //1500 on surface, 600 otherwise
  public final int BTNSPACE = 60; //126 on surface, 60 otherwise
  public final int HRZSPACE = 8;
  public CAWrapper() {
    setSize(3*FRAMESIZE/2+HRZSPACE, FRAMESIZE+BTNSPACE);
    add(new CellMap(FRAMESIZE / CELLSIZE, FRAMESIZE / CELLSIZE, CELLSIZE));
    setResizable(false);
    setTitle("Prisoner's Dilemma");
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
  
  
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        CAWrapper go = new CAWrapper();
        go.setVisible(true);
      }
    });
  }
}
