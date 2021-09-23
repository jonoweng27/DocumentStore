package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {

    public MinHeapImpl() {
        this.elements = (E[]) Array.newInstance(Comparable.class, 2);
    }

    @Override
    public void reHeapify(E element) {
        if (element==null) {
            throw new IllegalArgumentException();
        }
        int index = this.getArrayIndex(element);
        this.upHeap(index);
        index = this.getArrayIndex(element);
        this.downHeap(index);
    }

    @Override
    protected int getArrayIndex(E element) {
        if (element==null) {
            throw new IllegalArgumentException();
        }
        for (int i=1; i<elements.length; i++) {
            if (elements[i]==null) {
                throw new NoSuchElementException();
            }
            if (elements[i].equals(element)) {
                return i;
            }
        }
        //UNCLEAR WHAT TO RETURN HERE
        throw new NoSuchElementException();
    }

    @Override
    protected void doubleArraySize() {
        this.elements = Arrays.copyOf(this.elements, this.elements.length*2);
    }
}
