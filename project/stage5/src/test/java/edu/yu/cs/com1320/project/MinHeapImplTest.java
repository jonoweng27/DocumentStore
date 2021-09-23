package edu.yu.cs.com1320.project;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;

public class MinHeapImplTest {
    @Test
    public void basicTest() {
        MinHeap<Integer>heap = new MinHeapImpl<>();
        heap.insert(8);
        heap.insert(4);
        heap.insert(3);
        heap.insert(1);
        heap.insert(6);
        heap.insert(9);
        heap.insert(7);
        heap.insert(5);
        heap.insert(10);
        heap.insert(2);
        assertEquals(1, heap.remove());
        assertEquals(2, heap.remove());
        assertEquals(3, heap.remove());
        assertEquals(4, heap.remove());
        assertEquals(5, heap.remove());
        assertEquals(6, heap.remove());
        assertEquals(7, heap.remove());
        assertEquals(8, heap.remove());
        assertEquals(9, heap.remove());
        assertEquals(10, heap.remove());
    }
    @Test
    public void DocumentTest() throws URISyntaxException {
        MinHeap<Document>heap = new MinHeapImpl<>();
        List<Document> list = new ArrayList<>();
        DocumentImpl doc1 = new DocumentImpl(new URI("1"), "1"); list.add(doc1);
        DocumentImpl doc2 = new DocumentImpl(new URI("2"), "2"); list.add(doc2);
        DocumentImpl doc3 = new DocumentImpl(new URI("3"), "3"); list.add(doc3);
        DocumentImpl doc4 = new DocumentImpl(new URI("4"), "4"); list.add(doc4);
        DocumentImpl doc5 = new DocumentImpl(new URI("5"), "5"); list.add(doc5);
        DocumentImpl doc6 = new DocumentImpl(new URI("6"), "6"); list.add(doc6);
        DocumentImpl doc7 = new DocumentImpl(new URI("7"), "7"); list.add(doc7);
        DocumentImpl doc8 = new DocumentImpl(new URI("8"), "8"); list.add(doc8);
        DocumentImpl doc9 = new DocumentImpl(new URI("9"), "9"); list.add(doc9);
        DocumentImpl doc10 = new DocumentImpl(new URI("10"), "10"); list.add(doc10);
        DocumentImpl doc11 = new DocumentImpl(new URI("11"), "11"); list.add(doc11);
        DocumentImpl doc12 = new DocumentImpl(new URI("12"), "12"); list.add(doc12);
        DocumentImpl doc13 = new DocumentImpl(new URI("13"), "13"); list.add(doc13);
        DocumentImpl doc14 = new DocumentImpl(new URI("14"), "14"); list.add(doc14);
        DocumentImpl doc15 = new DocumentImpl(new URI("15"), "15"); list.add(doc15);
        DocumentImpl doc16 = new DocumentImpl(new URI("16"), "16"); list.add(doc16);
        DocumentImpl doc17 = new DocumentImpl(new URI("17"), "17"); list.add(doc17);
        DocumentImpl doc18 = new DocumentImpl(new URI("18"), "18"); list.add(doc18);
        DocumentImpl doc19 = new DocumentImpl(new URI("19"), "19"); list.add(doc19);
        DocumentImpl doc20 = new DocumentImpl(new URI("20"), "20"); list.add(doc20);
        Collections.shuffle(list);
        for (Document d : list) {
            heap.insert(d);
        }
        assertEquals(doc1, heap.remove());
        doc4.setLastUseTime(System.nanoTime());
        heap.reHeapify(doc4);
        assertEquals(doc2, heap.remove());
        assertEquals(doc3, heap.remove());
        assertEquals(doc5, heap.remove());
        doc6.setLastUseTime(System.nanoTime());
        heap.reHeapify(doc6);
        assertEquals(doc7, heap.remove());
        assertEquals(doc8, heap.remove());
        doc4.setLastUseTime(System.nanoTime());
        heap.reHeapify(doc4);
        assertEquals(doc9, heap.remove());
        doc10.setLastUseTime(System.nanoTime());
        heap.reHeapify(doc10);
        assertEquals(doc11, heap.remove());
        assertEquals(doc12, heap.remove());
        heap.reHeapify(doc14);
        assertEquals(doc13, heap.remove());
        assertEquals(doc14, heap.remove());
        doc20.setLastUseTime(1);
        heap.reHeapify(doc20);
        assertEquals(doc20, heap.remove());
        assertEquals(doc15, heap.remove());
        assertEquals(doc16, heap.remove());
        doc17.setLastUseTime(System.nanoTime());
        heap.reHeapify(doc17);
        doc4.setLastUseTime(doc18.getLastUseTime()+2);
        heap.reHeapify(doc4);
        doc19.setLastUseTime(2);
        heap.reHeapify(doc19);
        assertEquals(doc19, heap.remove());
        assertEquals(doc18, heap.remove());
        assertEquals("4", heap.remove().getDocumentTxt());
        assertEquals(doc6, heap.remove());
        assertEquals(doc10, heap.remove());
        assertEquals(new URI("17"), heap.remove().getKey());
        assertTrue(heap.isEmpty());
    }
}
