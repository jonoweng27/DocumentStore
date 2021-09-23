package edu.yu.cs.com1320.project;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.impl.BTreeImpl;
class BTreeImplTest {
    @Test
    void basicGetAndPut() {
        BTreeImpl<Integer, Integer> table = new BTreeImpl<>();
        table.put(1, 1);
        assertEquals(1, (int)table.get(1));
        assertEquals(1, (int)table.put(1, 2));
        assertNull(table.put(2, 2));
        assertEquals(2, (int)table.get(2));
        assertNull(table.get(100));
    }
    @Test
    void basicSameCell() {
        BTreeImpl<Integer, Integer> table = new BTreeImpl<>();
        table.put(0, 0);
        table.put(5, 5);
        assertEquals(0, (int)table.get(0));
        assertEquals(5, (int)table.get(5));
        assertEquals(5, (int)table.put(5, 10));
        assertEquals(10, (int)table.get(5));
        assertEquals(10, (int)table.put(5, 100));
        assertNull(table.put(10, 1000));
        assertEquals(0, (int)table.put(0, 1));
        assertEquals(1, (int)table.get(0));
        table.put(1, 1);
        assertEquals(1, (int)table.put(1, 6));
        assertEquals(6, (int)table.get(1));
        assertEquals(100, (int)table.put(5, 5));
    }
    @Test
    void testGetAndPut() {
        BTreeImpl<Integer, Integer> table = new BTreeImpl<>();
        for (int i=0; i<1000; i++) {
            assertNull(table.put(i, i));
        }
        for (int i=0; i<1000; i++) {
            assertEquals(i, (int)table.get(i));
        }
        for (int i=0; i<1000; i++) {
            assertEquals(i, (int)table.put(i, i+1));
        }
        for (int i=0; i<100; i++) {
            assertEquals(i+1, (int)table.get(i));
        }
    }
    @Test
  void BTreeImplSimplePutAndGet() {
   BTree<Integer,Integer> btree = new BTreeImpl<Integer,Integer>();
   btree.put(1,2);
   btree.put(3,6);
   btree.put(7,14);
   int x = btree.get(1);
   int y = btree.get(3);
   int z = btree.get(7);
   assertEquals(2, x);
   assertEquals(6, y);
   assertEquals(14, z);
   
   
    
  }
  
  @Test
  void BTreeImplALotOfInfoTest() {
   BTree<Integer,Integer> btree = new BTreeImpl<Integer,Integer>();
   for (int i = 0; i<1000; i++) {
    btree.put(i,2*i);
   }
   
   int aa = btree.get(450);
   assertEquals(900, aa);
  }
  
  
  @Test
  void BTreeImplCollisionTest() {
   BTree<Integer,Integer> btree = new BTreeImpl<Integer,Integer>();
   btree.put(1, 9);
   btree.put(6,12);
   btree.put(11,22);
   int a = btree.get(1);
   int b = btree.get(6);
   int c = btree.get(11);
   assertEquals(9, a);
   assertEquals(12, b);
   assertEquals(22, c);
  }
  
  @Test
  void BTreeImplReplacementTest() {
   BTree<Integer,Integer> btree = new BTreeImpl<Integer,Integer>();
   btree.put(1,2);
   int a = btree.put(1, 3);
   assertEquals(2, a);
   int b = btree.put(1, 4);
   assertEquals(3,b);
   int c = btree.put(1, 9);
   assertEquals(4, c);
  }
  @Test
  void btreeDelNullPut() {
   BTree<String,Integer> btree = new BTreeImpl<String,Integer>();
   
   btree.put("Defied", (Integer)22345);
   Integer test1a = btree.get("Defied");
   assertEquals(test1a, (Integer)22345);
   btree.put("Defied", null);
   Integer test1b = btree.get("Defied");
   assertEquals(test1b,null);
   btree.put("Oakland", 87123);
   
   Integer test2a = btree.get("Oakland");
   assertEquals(test2a, (Integer)87123);
   btree.put("Oakland", null);
   btree.get("Oakland");
   Integer test2b = btree.get("Oakland");
   assertEquals(test2b,null);
   
   btree.put("Sanguine", (Integer)4682);
   Integer test3a = btree.get("Sanguine");
   assertEquals(test3a, (Integer)4682);
   btree.put("Sanguine", null);
   btree.get("Sanguine");
   Integer test3b = btree.get("Sanguine");
   assertEquals(test3b,null);
  }
  @Test
  public void BTreeImplReplacementTest2() {
    BTree<Integer, Integer> btree = new BTreeImpl<Integer, Integer>();
    btree.put(1, 2);
    int a = btree.put(1, 3);
    assertEquals(2, a);
    int b = btree.put(1, 4);
    assertEquals(3, b);
    int c = btree.put(1, 9);
    assertEquals(4, c);
  }
  @Test
  public void basicCollision() {
    BTree<Integer, String> btree = new BTreeImpl<Integer, String>();
    btree.put(1, "Avi");
    btree.put(5, "dinsky");
    btree.put(6, "Radinsky");
    btree.put(11, "gami");
    assertEquals("gami", btree.put(11, "gthir"));
    assertEquals("gthir", btree.get(11));
    assertEquals("Avi", btree.get(1));
    assertEquals("Radinsky", btree.get(6));
  }
  
  @Test
  void HashEqualButNotEqual() {
   BTree<String,Integer> btree = new BTreeImpl<String,Integer>();
   
   btree.put("tensada", 3521);
   btree.put("friabili", 1253);
   Integer test1a = btree.get("tensada");
   assertEquals(test1a, (Integer)3521);
   Integer test1b = btree.get("friabili");
   assertEquals(test1b, (Integer)1253);
   
   btree.put("abyz", 8948);
   btree.put("abzj", 84980);
   Integer test2a = btree.get("abyz");
   assertEquals(test2a, (Integer)8948);
   Integer test2b = btree.get("abzj");
   assertEquals(test2b, 84980);
   
   btree.put("Siblings", 27128);
   btree.put("Teheran", 82172);
   Integer test3a = btree.get("Siblings");
   assertEquals(test3a, 27128);
   Integer test3b = btree.get("Teheran");
   assertEquals(test3b, (Integer)82172);
   
  }

}
