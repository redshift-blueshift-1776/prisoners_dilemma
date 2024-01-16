package forest;

public class GAWrapper {
 
 private final static int POP_SIZE = 1500;    //number of chromosomes to try
 private final static int NUM_TRIES = 3;     //tries to get good parents
 private final static double crossProb = 0.8;   //chance for children to happen
 private final static double mutProb = 0.001;    //chance for mutation to happen
 private final static String TARGET = "DDDDDDDDDDDDDDDDDDDD";  //...the target
 
 public static void main(String[] args) {
  /*
   GA yay = new GA(POP_SIZE, NUM_TRIES, crossProb, mutProb, TARGET);
  yay.initialize();
  int countGen = 0;
  while (!yay.findBest().getCode().equals(TARGET)) { //go until it works
   countGen++;
   yay.createNewPop();
   if (countGen % 1 == 0) {
    System.out.println("best: " + yay.findBest().getCode());  //how are we doing?
//    System.out.println("worst: " + yay.findWorst().getCode()); //hey Darwin, got one
   }
  }
  System.out.println("Generation " + countGen + " produced " + yay.findBest().getCode());
  */
   
  Cell a = new Cell();
  Cell b = new Cell();
  System.out.println(a.getStrategy().getCode());
  System.out.println(b.getStrategy().getCode());
  a.duel(a, b);
  System.out.println("a got " + a.getScore());
  System.out.println("b got " + b.getScore());
  
 } 
}
