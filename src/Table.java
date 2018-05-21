// Required imports
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Table {

  private int tableSize;
  private ArrayList<Tuple> content = new ArrayList<Tuple>();
  private double maxValue;
  private double minValue;
  private double averageValue;
  private double medianValue;
  
  public static void main(String[] args) {
    Table t = new Table("test.csv");
    System.out.println(t.toString());
    System.out.println(t.getRanks());
    System.out.println(t.getNormalizedRanks());
  } 
  
  Table(String fileName) {
    this.content = readFromFile(fileName);
    this.tableSize = content.size();
  }
  
  private ArrayList<Tuple> readFromFile(String fileName) {
    ArrayList<Tuple> tempContent = new ArrayList<Tuple>();
    Scanner scanner = null;
    
    maxValue = (-1.0)*(Double.MAX_VALUE);
    minValue = Double.MAX_VALUE;
    double tempSum = 0.0;
    
    try {
      scanner = new Scanner(new File(fileName));

      while(scanner.hasNextLine()) {
        String[] lineItems = scanner.nextLine().split(",");
        
        double tempValue = new Double(lineItems[1]);
        
        tempContent.add(new Tuple(lineItems[0], tempValue));
        
        // Set max and min values as we go
        if (tempValue > maxValue) {
          maxValue = tempValue;
        } 
        if (tempValue < minValue) {
          minValue = tempValue;
        }
        
        // Add to tempSum
        tempSum += tempValue;
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }    
    Collections.sort(tempContent);

    averageValue = tempSum/tempContent.size();
    medianValue = tempContent.get(tempContent.size()/2).getValue();
    
    
    return tempContent;
  }
  
  public Set<String> getKeySet() {
    Set<String> keySet = new HashSet<String>();
    for (Tuple t: content) {
      keySet.add(t.getKey());
    }
    return keySet;
  }
  
  public Double getAssociatedValue(String key) {
    for (Tuple t: content) {
      if (t.getKey().equals(key)) {
        return t.getValue();
      }
    }
    return null;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    for (Tuple t : this.content) {
      sb.append("\n    "+t.toString());
    }
    sb.append("\n}");
    return sb.toString();
  }
  
  public String getStats() {
    StringBuilder sb = new StringBuilder();
    sb.append("{max:");
    sb.append(this.maxValue);
    sb.append("\n}");
    sb.append("{min:");
    sb.append(this.minValue);
    sb.append("\n}");
    sb.append("{avg:");
    sb.append(this.averageValue);
    sb.append("\n}");
    sb.append("{median:");
    sb.append(this.medianValue);
    sb.append("\n}");
    return sb.toString();
  }

  public Map<String, Integer> getRanks() {
    Map<String, Integer> rankMap = new HashMap<String, Integer>();
    Collections.sort(this.content);
    int i = 1;
    
    for (Tuple t : content) {
      rankMap.put(t.getKey(), i++);
    }
    
    return rankMap;
  }
  
  public Map<String, Double> getNormalizedRanks() {
    Map<String, Double> normalizedRankMap = new HashMap<String, Double>();
    Collections.sort(this.content);
    double d = 0.0;
    
    for (Tuple t : content) {
      normalizedRankMap.put(t.getKey(), (d++)/(double)(this.tableSize));
    }
    
    
    return normalizedRankMap;
  }
  
  public int getTableSize() {
    return this.tableSize;
  }
  
  public double getMinValue() {
    return this.minValue;
  }
}
