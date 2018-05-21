import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TableRank {

  private Table[] tables = new Table[2];

  public static void main (String[] args) {
    TableRank tr = new TableRank("test.csv", "test2.csv");

    System.out.println("Union: " + TableRank.getUnionKeySet(tr.tables));
//    System.out.println(TableRank.getKeysInFirstButNotSecond(tr.tables[0], tr.tables[1]));
//    System.out.println(TableRank.getKeysInFirstButNotSecond(tr.tables[1], tr.tables[0]));
//
//    System.out.println(tr.tables[0].getStats());
//    System.out.println(tr.tables[1].getStats());
    
    System.out.println("Intersection: " + TableRank.getIntersectionKeySet(tr.tables));
    

  }

  TableRank(String fileName1, String fileName2) {
    tables[0] = new Table(fileName1);
    tables[1] = new Table(fileName2);
  }

  protected static Set<String> getUnionKeySet(Table[] tables) {
    Set<String> unionKeySet = new HashSet<String>();
    for (Table t: tables) {
      for (String key : t.getKeySet()) {
        unionKeySet.add(key);
      }
    }
    return unionKeySet;
  }

  protected static Set<String> getKeysInFirstButNotSecond(Table t1, Table t2) {
    Set<String> keysInFirstButNotSecond = new HashSet<String>();

    for (String key : t1.getKeySet()) {
      keysInFirstButNotSecond.add(key);
    }

    for (String key : t2.getKeySet()) {
      if (keysInFirstButNotSecond.contains(key)) {
        keysInFirstButNotSecond.remove(key);
      }
    }

    return keysInFirstButNotSecond;
  }

  protected static Set<String> getIntersectionKeySet(Table[] tables) {
    Set<String> intersectionKeySet = new HashSet<String>();
    Set<String> nonIntersectionKeySet = new HashSet<String>();

    if (tables.length <= 1) {
      return tables[0].getKeySet();
    } else {
      intersectionKeySet = tables[0].getKeySet();

      for (int i = 1; i < tables.length; i++) {
        Iterator<String> intersectionKeyIterator = intersectionKeySet.iterator();

        Set<String> tempKeySet = tables[i].getKeySet();

        while(intersectionKeyIterator.hasNext()) {
          
          String tempKey = intersectionKeyIterator.next();
          
          if (!(tempKeySet.contains(tempKey))) {
            nonIntersectionKeySet.add(tempKey);
          }
        } 
      }
    }
    for (String key : nonIntersectionKeySet) {
      intersectionKeySet.remove(key);
    }
    
    return intersectionKeySet;
  }

  public String printBaseTables() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    for (Table t : this.tables) {
      sb.append("\n    "+t.toString());
    }
    sb.append("\n}");
    return sb.toString();
  }

  public String toString() {
    return "";
  }
}
