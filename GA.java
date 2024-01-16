package forest;

public class GA {

 public Chromosome[] pop;      //the array of strings that represent chromosomes
 private int numTries;       //the number of tries to get parents
 private double crossProb;      //the probability of getting children
 private double mutProb;       //the probability of a child mutating
 private String target;
 
 public GA(int popSize, int tries, double crossover, double mutRate, String tarStr) {
  pop = new Chromosome[popSize];
  numTries = tries;
  crossProb = crossover;
  mutProb = mutRate;
  target = tarStr;
 }
 
 public void initialize() {
  for (int i = 0; i < pop.length; i++) {
   pop[i] = new Chromosome();    //create random starting chromosomes
  }
 }
 
 public void createNewPop() {        //make the next generation
  Chromosome[] newPop = new Chromosome[pop.length];  //start with a clean slate
  int index = 0;
  while (index < pop.length) {       //GA population creation goes through four stages
   //1) selection
   Chromosome[] parents = selectParents();
   //2) crossover
   Chromosome[] result = parents.clone();    //default is to just use the parents
   if (Math.random() < crossProb) {  
    result = parents[0].makeChildren(parents[1]);   //so THAT's how it happens!
   }
   //3) mutation
   for (int i = 0; i < result.length; i++) {
    if (index < pop.length) {
     if (Math.random() < mutProb) {    //maybe evolution is an intelligent design
      newPop[index] = result[i].mutate();
     } else {
      newPop[index] = result[i];      
     }
     index++;
    }
   }
  }
  //4) insertion
  pop = newPop.clone();
 }
 
 public Chromosome[] selectParents() {       //randomly select parents, this always works out well
  Chromosome[] result = new Chromosome[2];
  for (int i = 0; i < 2; i++) {
   result[i] = pop[randInt(0, pop.length)];
   for (int j = 0; j < numTries; j++) {     //especially if we go through 'matchmaking'
    int index = randInt(0, pop.length);
    if (pop[index].compareFitness(result[i]) < 0) { //if we find a better parent, switch
     result[i] = pop[index];
    }
   }
  }
  return result;
 }

 public Chromosome findBest() { //which one has the best fitness
  int minIndex = 0;
  for (int i = 0; i < pop.length; i++) {
   if (pop[i].compareFitness(pop[minIndex]) == -1) {
    minIndex = i;
   }
  }
  return pop[minIndex];
 }

 public Chromosome findWorst() { //for amusement purposes
  int maxIndex = 0;
  for (int i = 0; i < pop.length; i++) {
   if (pop[i].compareFitness(pop[maxIndex]) == 1) {
    maxIndex = i;
   }
  }
  return pop[maxIndex];
 }

 //helper function for readability, returns an int between min and max (including min, excluding max)
 public int randInt(int min, int max) { return min + (int)(Math.random()*max); }
}
