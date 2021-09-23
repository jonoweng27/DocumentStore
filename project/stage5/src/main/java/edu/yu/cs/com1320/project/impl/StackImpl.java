package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    private class Node {
        T data;
        Node next;
        private Node(T data){
            this.data = data;
            this.next = null;
        }
    }
    Node head;
    int top;
    public StackImpl() {
        head = null;
        top = -1;
    }
    /**
     * @param element object to add to the Stack
     */
    @Override
    public void push(T element) {
        if (element==null) {
            throw new IllegalArgumentException();
        }
        Node n = new Node(element);
        n.next=head;
        head = n;
        top++;
    }
    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop() {
        if(top==-1) {
            return null;
        }
        Node value = head;
        head = head.next;
        top--;
        return value.data;
    }
    /**
     *
     * @return the element at the top of the stack without removing it
     */
    @Override
    public T peek() {
        if(top==-1) {
            return null;
        }
        return head.data;
    }
    /**
     *
     * @return how many elements are currently in the stack
     */
    @Override
    public int size() {
        return top+1;
    }
}
