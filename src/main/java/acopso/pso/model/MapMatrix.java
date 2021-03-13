package acopso.pso.model;

import java.util.HashMap;
import java.util.Map;

public class MapMatrix<A, B, V> {
   Map<A, Map<B, V>> mapData = new HashMap<A, Map<B, V>>();

   public V get(A x, B y) {
      if (mapData.containsKey(x) && mapData.get(x).containsKey(y)) {
         return mapData.get(x).get(y);
      }
      return null;

   }

   public Map<B, V> get(A x) {
      if (mapData.containsKey(x)) {
         return mapData.get(x);
      }
      return null;
   }

   public void set(A x, B y, V value) {
      Map<B, V> data = new HashMap<B, V>();
      if (mapData.containsKey(x)) {
         data = mapData.get(x);
      }
      data.put(y, value);
      mapData.put(x, data);
   }

}