package forest;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.*;

@SuppressWarnings("serial")
public class CellMap extends JPanel implements ActionListener {
  
  private int vOffset;
  private int hOffset; 
  private int genCount;
  private int genSkip;
  private Image pic;
  private Color col = new Color(0,0,0);
  private JButton startBtn;
  private JButton stopBtn;
  private JButton clearBtn;
  private JButton speedBtn;
  private JButton imageBtn;
  private JButton recoverBtn;
  private JButton changeBtn;
  private int[] speeds = new int[4];
  private int speedIndex;
  private int mapIndex;
  private int changeIndex;
  private JTextField staysAliveTxt;
  private JTextField becomesAliveTxt;
  private JTextField genSkipTxt;
  private JLabel generations;
  private JLabel fracOfDs;
  private JLabel picLabel;
  private JLabel mouseLabel; //show cell coordinates of mouse
  private int[] mouseCoords;
  private int size;
  private int[][] cells;
  private Timer timer;
  private boolean isRunning;
  private boolean mouseDraw;
  private boolean[] staysAlive = new boolean[9];
  private boolean[] becomesAlive = new boolean[9];
  private Cell[][] realCellMap = new Cell[20][20];
  private int dRation;
  
  private int RADIUS = 1;
  
  public void setRadius(int r)
  {
    RADIUS = r;
  }
  
  public CellMap(int xDim, int yDim, int sz) {
    super(new GridBagLayout());                           // set up graphics window
    setBackground(Color.LIGHT_GRAY);
    addMouseListener(new MAdapter());
    addMouseMotionListener(new MAdapter());
    setFocusable(true);
    setDoubleBuffered(true);
    initBtns();
    initTxt();
    initLabels();
    pic = new BufferedImage(sz*xDim, sz*yDim, BufferedImage.TYPE_INT_RGB);
    picLabel = new JLabel(new ImageIcon(pic));
    addThingsToPanel();
    
    col = new Color(0, 0, 0);     // initialize a color
    genCount = 0;
    genSkip = 1;
    
    cells = new int[xDim][yDim];    // initialize the cells
    realCellMap = new Cell[xDim][yDim];
    for (int i = 0; i < xDim; i++) {
      for (int j = 0; j < yDim; j++) {
        //0.5 represents the probability of a cell being filled (=1) without needing an if statement
        cells[i][j] = (int)(Math.random()+0.5);
        realCellMap[i][j] = new Cell();
      }
    }
    
    isRunning = false;
    size = sz;
    for (int i = 0; i < 4; i++) {
      speeds[3-i] = 100 * i * i;
    }
    timer = new Timer(speeds[speedIndex], this);     // initialize the timer
    timer.start();
    
    for(int i = 0; i < 9; i++) {
      staysAlive[i] = staysAliveTxt.getText().replace(",", "").trim().contains(((Integer)i).toString());
    }
    for(int i = 0; i < 9; i++) {
      becomesAlive[i] = becomesAliveTxt.getText().replace(",", "").trim().contains(((Integer)i).toString());
    }
    mouseDraw = true;
    
    drawCells(pic.getGraphics());
  }
  
  public void addThingsToPanel() {
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.FIRST_LINE_START;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(1, 1, 0, 1);
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 6;
    c.gridheight = 9;
    add(picLabel, c);
    c.gridwidth = 1;
    c.gridheight = 1;
    c.insets = new Insets(0, 2, 0, 2);
    c.gridx = 0;
    c.gridy = 0;
    add(startBtn, c);
    c.gridx = 1;
    c.gridy = 0;
    add(stopBtn, c);
    c.gridx = 2;
    add(clearBtn, c);
    c.gridx = 3;
    add(speedBtn, c);
    c.insets = new Insets(0, 10, 0, 10);
    c.gridx = 4;
    c.gridy = 0;
    c.fill = GridBagConstraints.VERTICAL;
    add(generations, c);
    c.gridx = 5;
    add(mouseLabel, c);
    c.gridx = 6;
    c.gridy = 0;
    add(fracOfDs, c);
    c.gridx = 6;
    c.gridy = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    add(imageBtn, c);
    c.gridy = 2;
    c.fill = GridBagConstraints.BOTH;
    add(new JLabel("Skip Generations"), c);
    c.gridy = 4;
    add(new JLabel("Stays Alive"), c);
    c.gridy = 6;
    add(new JLabel("Radius (not working)"), c);
    c.gridx = 6;
    c.gridy = 3;
    add(genSkipTxt, c);     
    c.gridy = 5;
    add(staysAliveTxt, c);
    c.gridy = 7;
    add(becomesAliveTxt, c);
    c.gridy = 8;
    add(changeBtn, c);
  }
  
  public void initTxt() {
    
    staysAliveTxt = new JTextField("23", 8);
    staysAliveTxt.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String good = staysAliveTxt.getText().replace(",", "").trim();
        for(int i = 0; i < 8; i++) {
          staysAlive[i] = good.contains(((Integer)i).toString());
        }
      }
    });
    becomesAliveTxt = new JTextField("1", 8);
    becomesAliveTxt.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //String good = becomesAliveTxt.getText().replace(",", "").trim();
        String text = becomesAliveTxt.getText();
        //for(int i = 0; i < 8; i++) {
        //becomesAlive[i] = good.contains(((Integer)i).toString());
        //}
        RADIUS = Integer.parseInt(text);
      }
    });
    genSkipTxt = new JTextField("1", 4);
    genSkipTxt.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        genSkip = Integer.parseInt(genSkipTxt.getText());
      }
    });
  }
  
  public void initLabels() {
    generations = new JLabel("Generations: " + genCount);
    fracOfDs = new JLabel("D Ration: " + dRation + "/" + (realCellMap.length * realCellMap[0].length * 20));
    mouseCoords = new int[2];
    mouseLabel = new JLabel("Mouse off-grid");
  }
  
  public void initBtns() {
    
    startBtn = new JButton("Start");
    startBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isRunning = true;
      }
    });
    
    stopBtn = new JButton("Stop");
    stopBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isRunning = false;
      }
    }); 
    
    imageBtn = new JButton("Save Picture");
    imageBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          Calendar c = Calendar.getInstance();
          String fileName = ".\\" + staysAliveTxt.getText() + "+" + becomesAliveTxt.getText() + "@" + c.get(Calendar.HOUR) + "." + c.get(Calendar.MINUTE) + "." + c.get(Calendar.SECOND)+ ".png";
          System.out.println(fileName);
          File outputFile = new File(fileName);
          outputFile.createNewFile();
          ImageIO.write((RenderedImage) pic, "png", outputFile);
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });
    
    mapIndex = 0;
    Chromosome white = new Chromosome("CCCCCCCCCCCCCCCCCCCC");
    Chromosome red = new Chromosome("DDDDDDDDDDDDDDDDDDDD");
    clearBtn = new JButton("Map = Random");
    clearBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        genCount = 0;
        mapIndex = (mapIndex + 1) % 5;
        cells = new int[cells.length][cells[0].length];    // initialize the cells
        realCellMap = new Cell[realCellMap.length][realCellMap[0].length];
        switch (mapIndex) {
          case 0 : {
            clearBtn.setText("Map = Random");
            for (int i = 0; i < realCellMap.length; i++) {
              for (int j = 0; j < realCellMap[0].length; j++) {
                //0.5 represents the probability of a cell being filled (=1) without needing an if statement
                cells[i][j] = (int)(Math.random()+0.5);
                realCellMap[i][j] = new Cell();
              }
            }
            break;
          }
          case 1 : {
            clearBtn.setText("Map = One Square");
            for (int i = 0; i < realCellMap.length; i++) {
              for (int j = 0; j < realCellMap[0].length; j++) {
                //0.5 represents the probability of a cell being filled (=1) without needing an if statement
                cells[i][j] = (int)(Math.random()+0.5);
                realCellMap[i][j] = new Cell();
                realCellMap[i][j].setStrategy(white);
              }
            }
            realCellMap[realCellMap.length / 2][realCellMap[0].length / 2].setStrategy(red);
            break;
          }
          case 2 : {
            clearBtn.setText("Map = All White");
            for (int i = 0; i < realCellMap.length; i++) {
              for (int j = 0; j < realCellMap[0].length; j++) {
                //0.5 represents the probability of a cell being filled (=1) without needing an if statement
                realCellMap[i][j] = new Cell();
                  realCellMap[i][j].setStrategy(white);
              }
            }
            break;
          }
          case 3 : {
            clearBtn.setText("Map = One Line");
            for (int i = 0; i < realCellMap.length; i++) {
              for (int j = 0; j < realCellMap[0].length; j++) {
                //0.5 represents the probability of a cell being filled (=1) without needing an if statement
                realCellMap[i][j] = new Cell();
                  realCellMap[i][j].setStrategy(white);
              }
                realCellMap[i][realCellMap[0].length / 2].setStrategy(red);
            }
            break;
          }
          case 4 :
          {
            clearBtn.setText("Map = Halfway");
            for (int i = 0; i < realCellMap.length; i++) {
              for (int j = 0; j < realCellMap[0].length; j++) {
                //0.5 represents the probability of a cell being filled (=1) without needing an if statement
                cells[i][j] = (int)(Math.random()+0.5);
                realCellMap[i][j] = new Cell();
                realCellMap[i][j].setStrategy(new Chromosome("CDCDCDCDCDCDCDCDCDCD"));
              }
            }
            break;
          }
        }
        
        drawCells(pic.getGraphics());
        isRunning = false;
      }
    }); 
    
    recoverBtn = new JButton("Recover Cells"); //maybe one day this will recover cells from a file
    recoverBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
      }
    });
    
    speedBtn = new JButton("Speed = Slow");
    speedIndex = 0;
    speedBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        speedIndex = (speedIndex + 1) % 4;
        timer.setDelay(speeds[speedIndex]);
        switch (speedIndex) {
          case 0 : {
            speedBtn.setText("Speed = Slow");
            break;
          }
          case 1 : {
            speedBtn.setText("Speed = Med");
            break;
          }
          case 2 : {
            speedBtn.setText("Speed = Fast");
            break;
          }
          case 3 : {
            speedBtn.setText("Speed = Whoa");
            break;
          }
        }
      }
    });
    
    changeBtn = new JButton("Change = Cross");
    changeIndex = 0;
    changeBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changeIndex = (changeIndex + 1) % 2;
        switch (changeIndex) {
          case 0 : {
            changeBtn.setText("Change = Cross");
            break;
          }
          case 1 : {
            changeBtn.setText("Change = Copy");
            break;
          }
        }
      }
    });
  }
  
  public CellMap() {
    super();
    setBackground(Color.WHITE);
    addMouseListener(new MAdapter());
    setFocusable(true);
    setDoubleBuffered(true);
  }
  
  public void paintComponent(Graphics g)                    // draw graphics in the panel
  {
    super.paintComponent(g);                                // call superclass to make panel display correctly
//        g.drawImage(pic, hOffset, vOffset, this);
  }
  
  /*
   public void drawCells(Graphics g) {
   for (int i = 0; i < cells.length; i++) {
   for (int j = 0; j < cells[i].length; j++) {
   if (cells[i][j] == 1) {
   g.setColor(col);
   g.fillRect(i*size, j*size, size, size);
   }
   else {
   g.setColor(Color.WHITE);
   g.fillRect(i*size, j*size, size, size);
   }
   }
   }
   }
   */
  
  public void drawCells(Graphics g)
  {
    for (int i = 0; i < realCellMap.length; i++)
    {
      for (int j = 0; j < realCellMap[i].length; j++)
      {
        if (realCellMap[i][j].getNumberOfDs() == 20)
        {
          col = new Color(255, 0, 0);
        }
        else
        {
          col = new Color(255, 255 - realCellMap[i][j].getNumberOfDs() * 10, 255 - realCellMap[i][j].getNumberOfDs() * 10);
        }
        g.setColor(col);
        g.fillRect(i*size, j*size, size, size);
      }
    }
  }
  
  public int countNeighbors(int row, int col) {
    int result = 0;
    for(int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        if (cells[(cells.length + row + i) % cells.length][(cells[0].length + col + j) % cells[0].length] >= 1) {
          result++;
        }
      }
    }
    return result - cells[row][col];
  }
  
  public int duelNeighbors(int row, int col)
  {
    Cell a = realCellMap[row][col];
    for(int i = -1; i <= 1; i++)
    {
      for (int j = -1; j <= 1; j++)
      {
        if ((i != 0) && (j != 0))
        {
          Cell b = realCellMap[(realCellMap.length + row + i) % realCellMap.length][(realCellMap[0].length + col + j) % realCellMap[0].length];
          a.duel(a, b);
        }
      }
    }
    return a.getScore();
  }
  
  public void updateCells() {
    ArrayList<Point> changed = new ArrayList<Point>();
    int sizes = RADIUS * 2 + 1;
    int temp = Integer.MIN_VALUE;
    int temp2 = temp;
    Chromosome temp3 = new Chromosome();
    Chromosome temp4 = new Chromosome();
    Chromosome[][] stratArray = new Chromosome[cells.length][cells[0].length];
    for (int gens = 0; gens < genSkip; gens++)
    {
      changed.clear();
      stratArray = new Chromosome[cells.length][cells[0].length];
      for (int i = 0; i < cells.length; i++)
      {
        for (int j = 0; j < cells[i].length; j++)
        {
          temp = Integer.MIN_VALUE;
          temp2 = temp;
          temp3 = new Chromosome();
          int[][] payoffArray = new int[sizes][sizes];
          int[] payoffArray2 = new int[sizes * sizes];
          Chromosome[][] smallStratArray = new Chromosome[sizes][sizes];
          smallStratArray[RADIUS][RADIUS] = realCellMap[i][j].getStrategy();
          for(int q = -1 * RADIUS; q <= RADIUS; q++)
          {
            for (int k = -1 * RADIUS; k <= RADIUS; k++)
            {
              if ((q != 0) || (k != 0))
              {
                Cell a = realCellMap[i][j];
                Cell b = realCellMap[(realCellMap.length + q + i) % realCellMap.length][(realCellMap[0].length + k + j) % realCellMap[0].length];
                a.setScore(0);
                b.setScore(0);
                smallStratArray[q+RADIUS][k+RADIUS] = a.duel(a, b);
                payoffArray[q+RADIUS][k+RADIUS] = a.getScore() - b.getScore();
                payoffArray2[(sizes * q + k + sizes + 1)] = a.getScore() - b.getScore();
              }
            }
          }
          /*
           if (((cells[i][j] == 1) && (!staysAlive[temp])) || ((cells[i][j] == 0) && becomesAlive[temp])) {
           changed.add(new Point(i,j));
           }
           */
          temp3 = smallStratArray[RADIUS][RADIUS];
          temp2 = 0;
          temp = 0;
          int q = 0;
          int k = 0;
          Arrays.sort(payoffArray2);
          
          while (q < sizes)
          {
            k = 0;
            while (k < sizes)
            {
              if (payoffArray[q][k] < temp)
              {
                temp = payoffArray[q][k];
                temp3 = smallStratArray[q][k];
              }
              k++;
            }
            q++;
          }
          /*
           while (q < 3)
           {
           k = 0;
           while (k < 3)
           {
           if (payoffArray[2-q][2-k] == payoffArray2[8])
           {
           temp = payoffArray[2-q][2-k];
           temp3 = smallStratArray[2-q][2-k];
           }
           if (payoffArray[2-q][2-k] == payoffArray2[7])
           {
           temp2 = payoffArray[2-q][2-k];
           temp4 = smallStratArray[2-q][2-k];
           }
           k++;
           }
           q++;
           }
           */
          q = k + changeIndex;
          if (q == k) // normal
          {
            temp3 = realCellMap[i][j].getStrategy().makeChildren(temp3)[(int) (Math.random() + 0.5)];
            stratArray[i][j] = temp3;
          }
          if (q == k + 1) //more OP but not complete
          {
            stratArray[i][j] = temp3;
          }
          /*
           if (q == k + 2) //too OP, but not working
           {
           stratArray[i][j] = temp3;
           int r = -1 * RADIUS;
           int p = -1 * RADIUS;
           while (r <= RADIUS)
           {
           p = -1 * RADIUS;
           while (p <= RADIUS)
           {
           stratArray[(i + r + stratArray.length) % stratArray.length][(j + p + stratArray[0].length) % (stratArray[0].length)] = temp3;
           p++;
           }
           r++;
           }
           }
           */
          double chance = Math.random();
          if (chance < 0.01)
          {
            realCellMap[i][j].mutate();
          }
          repaint();
        }
      }
      
      for(Point p : changed) {
        cells[p.x][p.y] = 1-cells[p.x][p.y];
      }
      genCount++;
      drawCells(pic.getGraphics());
      repaint();
      
    }
    dRation = 0;
    for (int i = 0; i < realCellMap.length; i++)
    {
      for (int j = 0; j < realCellMap[i].length; j++)
      {
        realCellMap[i][j].setStrategy(stratArray[i][j]);
        dRation += realCellMap[i][j].getNumberOfDs();
      }
    }
  }
  
  public void updateLabels() {
    generations.setText("Generations: " + genCount);
    fracOfDs.setText("D Ration: " + dRation + "/" + (realCellMap.length * realCellMap[0].length * 20));
    if ((mouseCoords[0] >= 0) && (mouseCoords[0] <= cells.length) && (mouseCoords[1] >= 0) && (mouseCoords[1] <= cells[0].length)) {
      mouseLabel.setText("Mouse at (" + mouseCoords[0] + ", " + mouseCoords[1] + ")");           
    } else {
      mouseLabel.setText("Mouse off-grid");
    }
  }
  
  public void updateRules() {
    String good = staysAliveTxt.getText().replace(",", "").trim();
    for(int i = 0; i < 8; i++) {
      staysAlive[i] = good.contains(((Integer)i).toString());
    }
    /*
     good = becomesAliveTxt.getText().replace(",", "").trim();
     for(int i = 0; i < 8; i++) {
     becomesAlive[i] = good.contains(((Integer)i).toString());
     }
     */
    becomesAliveTxt.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //String good = becomesAliveTxt.getText().replace(",", "").trim();
        String text = becomesAliveTxt.getText();
        //for(int i = 0; i < 8; i++) {
        //becomesAlive[i] = good.contains(((Integer)i).toString());
        //}
        RADIUS = Integer.parseInt(text);
      }
    });
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    if (isRunning) {
      updateRules();
      updateCells();   
      drawCells(pic.getGraphics());
    }
    hOffset = picLabel.getLocationOnScreen().x - getLocationOnScreen().x;
    vOffset = picLabel.getLocationOnScreen().y - getLocationOnScreen().y;
    updateLabels();
    drawCells(pic.getGraphics());
    repaint();
  }
  
//where the mouse handler goes
  private class MAdapter extends MouseAdapter {
    
    @Override
    public void mousePressed(MouseEvent e) {
      Point p = new Point((e.getX() - hOffset) / size, (e.getY() - vOffset) / size);
      try {
        cells[p.x][p.y] = 1 - cells[p.x][p.y];
        drawCells(pic.getGraphics());
        mouseDraw = !mouseDraw;
      } catch (ArrayIndexOutOfBoundsException e2) {
      }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
      Point p = new Point((e.getX() - hOffset) / size, (e.getY() - vOffset) / size);
//   System.out.println(hOffset + " " + e.getXOnScreen() + ", " + e.getYOnScreen() + " grid " + p.x*size + ", " + p.y*size);
      mouseCoords[0] = p.x;
      mouseCoords[1] = p.y;   
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
      Point p = new Point((e.getX() - hOffset) / size, (e.getY() - vOffset) / size);
      mouseCoords[0] = p.x;
      mouseCoords[1] = p.y;   
      try {
        if (mouseDraw) {
          realCellMap[p.x][p.y] = new Cell();
          realCellMap[p.x][p.y].setStrategy(new Chromosome("CCCCCCCCCCCCCCCCCCCC"));
        } else {
          realCellMap[p.x][p.y] = new Cell();
          realCellMap[p.x][p.y].setStrategy(new Chromosome("CCCCCCCCCCCCCCCCCCCC"));
        }
        drawCells(pic.getGraphics());
      } catch (ArrayIndexOutOfBoundsException e2) {
      }
    }
    
//  @Override
//  public void mouseReleased(MouseEvent e) {
//  }
  }
}

