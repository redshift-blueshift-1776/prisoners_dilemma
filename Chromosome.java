package forest;

public class Chromosome {
  
  private String code;  //the encoding of information as a String, each character being a gene
  private int fitness; //the fitness of that information (calculated)
  
  static final String TARGET = "DDDDDDDDDDDDDDDDDDDD"; //...the target
  
  public Chromosome(String in) { //deliberate gene creation
    code = in;
    fitness = calcFitness(in); //immediately calc fitness
  }
  
  public Chromosome() { //random chromosome creation
    char[] charArray = new char[TARGET.length()];
    for (int i = 0; i <TARGET.length(); i++) {
      charArray[i] = randChar();
    }
    code = String.valueOf(charArray);
    fitness = calcFitness(code); //immediately calc fitness
  }
  
  public String getCode() { return code; }
  public void setCode(String in) { code = in; }
  
  public int getFitness() { return fitness; }
  public void setFitness(int fitness) { this.fitness = fitness; }
  
  //helper function for readability, returns an int between min and max (including min)
  public int randInt(int min, int max) { return min + (int)(Math.random()*max); }
  
  //helper function to generate a random character for mutation purposes
  public char randChar() { //using asciitable.com's ascii table representation
    //int rand = randInt(35, 36); //32-126 resolve to safe characters
    int rand = 36;
    if((Math.random()+0.5)>1)
    {
      rand = 35;
    }
    else
    {
      rand = 36;
    }
    return (char)(32 + rand);
  }
  
  //warning, unlike other GAs, in this example
  //a lower fitness is better, due to how fitness is calculated
  //re-implement this function in whatever way is actually appropriate for your purposes
  //currently, this function sums up the differences between the gene and the target
  public int calcFitness(String chromo) {
    int result = 0;
    for (int i = 0; i < chromo.length(); i++) {
      result += Math.abs(chromo.charAt(i) - TARGET.charAt(i)); //ascii table-based distance
    }
    return result;
  }
  
  //returns a chromosome mutated in one place
  //does not mutate chromosome that calls this method
  //does not install adamantium, psychokinetic abilities, or sticky tongues
  public Chromosome mutate() {
    int index = randInt(0, code.length()); //pick where to mutate
    char[] codeArr = code.toCharArray();
    codeArr[index] = randChar();           //mutate
    return new Chromosome(String.valueOf(codeArr));
  }
  
  public Chromosome[] makeChildren(Chromosome partner) {//maybe this should be done in private?
    int crossoverIndex = randInt(1, code.length() - 1); //substring(0, 0) is bad
    //you studied strings and array declaration, right?  So try this next line out
    return new Chromosome[] {new Chromosome(code.substring(0, crossoverIndex).concat(partner.getCode().substring(crossoverIndex))), new Chromosome(partner.getCode().substring(0, crossoverIndex).concat(code.substring(crossoverIndex)))};
  }
  
  //remember, lower fitness scores are better
  public int compareFitness(Chromosome g) {
    if (g.getFitness() > fitness) return -1; //self has a better fitness rating than g
    else if (g.getFitness() < fitness) return 1; //self has a worse fitness rating than g
    else return 0; //self is as fit as g
  }
  
  //since we're comparing things, let's be clearer about what it would mean for two chromosomes to be equal
  //again, implement this however you'd actually need
  //but the input always needs to be of class object
  public boolean equals(Object o) { //a way to invoke .equals on Chromosomes
    return (Chromosome.class.isInstance(o) && (code.equals(((Chromosome) o).getCode()) && ((Chromosome) o).getFitness() == fitness));
  }
  
  public Chromosome duel(Cell a1, Cell b1)
  {
    Chromosome a = a1.getStrategy();
    Chromosome b = b1.getStrategy();
    Chromosome better = new Chromosome();
    int q = 0;
    int payoffA = 0;
    int payoffB = 0;
    while (q < a.getCode().length())
    {
      if (a.getCode().charAt(q) == 'C')
      {
        if (b.getCode().charAt(q) == 'C')
        {
          payoffA += 2;
          payoffB += 2;
        }
        else
        {
          payoffA += 0;
          payoffB += 3;
        }
      }
      else
      {
        if (b.getCode().charAt(q) == 'C')
        {
          payoffA += 3;
          payoffB += 0;
        }
        else
        {
          payoffA += 1;
          payoffB += 1;
        }
      }
      q++;
    }
    a1.setScore(a1.getScore() + payoffA);
    b1.setScore(b1.getScore() + payoffB);
    
    if (payoffA >= payoffB)
    {
      better = a;
    }
    if (payoffA < payoffB)
    {
     better = b;
    }
    return better;
  }
}
