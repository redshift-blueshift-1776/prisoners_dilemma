package forest;

public class Cell extends Chromosome
{
  private int score;
  private Chromosome strategy;
  private int numberOfDs;
  
  public Cell()
  {
    strategy = new Chromosome();
    score = 0;
    numberOfDs = 0;
    Chromosome a = strategy;
    int q = 0;
    while (q < a.getCode().length())
    {
      if (a.getCode().charAt(q) == 'D')
      {
        numberOfDs += 1;
      }
      q++;
    }
  }
  
  public void setStrategy(Chromosome a)
  {
    strategy = a;
    numberOfDs = 0;
    int q = 0;
    while (q < a.getCode().length())
    {
      if (a.getCode().charAt(q) == 'D')
      {
        numberOfDs += 1;
      }
      q++;
    }
    a.mutate();
  }
  public void setScore(int a)
  {
    score = a;
  }
  
  public Chromosome mutate()
  {
    strategy = strategy.mutate();
    return strategy;
  }
  
  public Chromosome getStrategy()
  {
    return strategy;
  }
  public int getScore()
  {
    return score;
  }
  public int getNumberOfDs()
  {
    Chromosome a = strategy;
    numberOfDs = 0;
    int q = 0;
    while (q < a.getCode().length())
    {
      if (a.getCode().charAt(q) == 'D')
      {
        numberOfDs += 1;
      }
      q++;
    }
    return numberOfDs;
  }
  
}