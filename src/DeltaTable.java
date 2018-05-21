import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DeltaTable {

  // Data structure: Deltas are expressed in a Map of {String key, Double[] delta}

  // Double[] delta = {"absolute_change", "percentage_change", "rank_change", "normalized_rank_change"}

  // If in both tables:
  // * absolute_change will be (table2_value-table1_value)
  // * relative change will be (table2_value/table1_value)-1
  // * rank change will be -1*(table2_rank-table1_rank)
  // * normalized_rank_change will be -1*(table2_normalizedRank-table1_normalizedRank) 

  // If in table1 but not in table2:
  // * absolute_change will assume to have gone to zero
  // * relative change will be -100%
  // * rank change will be 1+(table2_size - table1_rank)
  // * normalized_rank_change will be -1.0; 

  // If not in table1 but in table2:
  // * absolute_change will be value in table2
  // * relative_change will be percentage change of minimum item in table1 to reach value of this item in table2
  // * rank_change will be rank in table2; 
  // * normalized_rank_change will be normalized_rank in table2 


  Map<String, Double[]> deltaMap = new HashMap<String, Double[]>(); 
  Map<String, String> tableContinuity = new HashMap<String, String>();

  ArrayList<Tuple> absoluteChanges = new ArrayList<Tuple>();
  ArrayList<Tuple> relativeChanges = new ArrayList<Tuple>();
  ArrayList<Tuple> rankChanges = new ArrayList<Tuple>();
  ArrayList<Tuple> normalizedRankChanges = new ArrayList<Tuple>();

  Map<String, Integer> table1_Ranks;
  Map<String, Integer> table2_Ranks;
  Map<String, Double> table1_normalizedRanks;
  Map<String, Double> table2_normalizedRanks;
  Set<String> unionKeySet;
  Table[] twoTables;




  DeltaTable (Table table1, Table table2) {
    twoTables = new Table[2];
    twoTables[0] = table1;
    twoTables[1] = table2;

    unionKeySet = TableRank.getUnionKeySet(twoTables);
    table1_Ranks = twoTables[0].getRanks();
    table2_Ranks = twoTables[1].getRanks();

    table1_normalizedRanks = twoTables[0].getNormalizedRanks();
    table2_normalizedRanks = twoTables[1].getNormalizedRanks();

    deltaMap = getStats();


  }

  public static void main (String[] args) {
    String fileName1 = "test.csv";
    String fileName2 = "test2.csv";

    DeltaTable dt = new DeltaTable(new Table(fileName1), new Table(fileName2));

    System.out.println(dt.toString());
  }

  private Map<String, Double[]> getStats () {
    Map<String, Double[]> deltaMap = new HashMap<String, Double[]>();

    for (String key : unionKeySet) {
      Set<String> intersectionKeySet = TableRank.getIntersectionKeySet(twoTables);
      Set<String> onlyInFirst = TableRank.getKeysInFirstButNotSecond(twoTables[0], twoTables[1]);
      Set<String> onlyInSecond = TableRank.getKeysInFirstButNotSecond(twoTables[1], twoTables[0]);

      Double absoluteChange = null;
      Double relativeChange = null;
      Double rankChange = null;
      Double normalizedRankChange = null;
      Double[] delta = new Double[4];

      // If in both tables;
      // * absolute_change will be (table2_value-table1_value)
      // * relative change will be (table2_value/table1_value)-1
      // * rank change will be -1*(table2_rank-table1_rank)
      // * normalized_rank_change will be -1*(table2_normalizedRank-table1_normalizedRank)
      if (intersectionKeySet.contains(key)) {
        // Double[] delta = {"absolute_change", "percentage_change", "rank_change", "normalized_rank_change"}

        absoluteChange = 
            twoTables[1].getAssociatedValue(key) - twoTables[0].getAssociatedValue(key);

        relativeChange = 
            (twoTables[1].getAssociatedValue(key)/twoTables[0].getAssociatedValue(key))-1.0;

        rankChange = 
            -1.0*(table2_Ranks.get(key)-table1_Ranks.get(key));

        normalizedRankChange = 
            -1.0*(table2_normalizedRanks.get(key)-table1_normalizedRanks.get(key));

        tableContinuity.put(key, "A-->B");
      }


      // If in table1 but not in table2:
      // * absolute_change will assume to have gone to zero
      // * relative change will be -100%
      // * rank change will be 1+(table2_size - table1_rank)
      // * normalized_rank_change will be -1.0 
      if (onlyInFirst.contains(key)) {
        absoluteChange = 
            0 - twoTables[0].getAssociatedValue(key);

        relativeChange = 
            -1.0;

        rankChange = 
            -1.0 * (twoTables[1].getTableSize()-table1_Ranks.get(key));

        normalizedRankChange = 
            -1.0;

        tableContinuity.put(key, "A only");

      }


      // If not in table1 but in table2:
      // * absolute_change will be value in table2
      // * relative_change will be table2_value / minimum_value(table1) -1.0 (percentage change of minimum item in table1 to reach value of this item in table2)
      // * rank_change will be rank in table2; 
      // * normalized_rank_change will be normalized_rank in table2 
      if (onlyInSecond.contains(key)) {
        absoluteChange = 
            twoTables[1].getAssociatedValue(key);

        relativeChange = 
            Double.NaN;
            //(twoTables[1].getAssociatedValue(key)/twoTables[0].getMinValue())-1.0;

        rankChange = 
            0.0+table2_Ranks.get(key);

        normalizedRankChange = 
            0.0+table2_normalizedRanks.get(key);

        deltaMap.put(key, delta);

        tableContinuity.put(key, "B only"); 
      }

      delta[0] = absoluteChange;
      delta[1] = relativeChange;
      delta[2] = rankChange;
      delta[3] = normalizedRankChange;
      deltaMap.put(key, delta);

      absoluteChanges.add(new Tuple(key, absoluteChange));
      relativeChanges.add(new Tuple(key, relativeChange));
      rankChanges.add(new Tuple(key, rankChange));
      normalizedRankChanges.add(new Tuple(key, normalizedRankChange));

    }

    // Sort
    Collections.sort(absoluteChanges);
    Collections.sort(relativeChanges);
    Collections.sort(rankChanges);
    Collections.sort(normalizedRankChanges);

    return deltaMap;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("TABLE RANK V 0.0.1\n");
    sb.append("\n---DELTA TABLE---\n");
    sb.append(String.format("%20s%20s%20s%20s%30s%20s\n", "Key", "Absolute Change", "Relative Change", "Rank Change", "Norm. Rank Change", "Notes"));
    for (String key : deltaMap.keySet()) {
      Double[] tempValues = deltaMap.get(key);
      sb.append(String.format("%20s%20f%20f%20f%30f%20s\n", key, tempValues[0], tempValues[1], tempValues[2], tempValues[3], tableContinuity.get(key)));
    }


    sb.append("\n---ABSOLUTE CHANGES---\n");
    sb.append(String.format("%20s%30s%20s\n", "Key", "Absolute Change", "Notes"));
    for (Tuple t : absoluteChanges) {
      sb.append(String.format("%20s%30f%20s\n", t.getKey(), t.getValue(), tableContinuity.get(t.getKey())));
    }


    sb.append("\n---RELATIVE CHANGES---\n");
    sb.append(String.format("%20s%30s%20s\n", "Key", "Relative Change", "Notes"));
    for (Tuple t : relativeChanges) {
      sb.append(String.format("%20s%30f%20s\n", t.getKey(), t.getValue(), tableContinuity.get(t.getKey())));
    }

    
    sb.append("\n---RANK CHANGES---\n");
    sb.append(String.format("%20s%30s%20s\n", "Key", "Rank Change", "Notes"));
    for (Tuple t : rankChanges) {
      sb.append(String.format("%20s%30f%20s\n", t.getKey(), t.getValue(), tableContinuity.get(t.getKey())));
    }


    sb.append("\n---NORMALIZED RANK CHANGES---\n");
    sb.append(String.format("%20s%30s%20s\n", "Key", "Normalized Rank Change", "Notes"));
    for (Tuple t : normalizedRankChanges) {
      sb.append(String.format("%20s%30f%20s\n", t.getKey(), t.getValue(), tableContinuity.get(t.getKey())));
    }



    return sb.toString();
  }

}
