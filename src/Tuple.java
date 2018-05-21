
public class Tuple implements Comparable<Tuple>{

  private final String key;
  private final Double value;
  
  Tuple(String key, Double value) {
    this.key = key;
    this.value = value;
  }
  
  @Override
  public int compareTo(Tuple t) {
    // TODO Auto-generated method stub
    return Double.compare(t.getValue(), this.value);
  }

  public String getKey() {
    return key;
  }

  public Double getValue() {
    return value;
  }

  public String toString() {
    return "{"+this.key+": "+this.value+"}";
  }
  
  
}
