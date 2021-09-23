package edu.yu.cs.com1320.project.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.yu.cs.com1320.project.Trie;

public class TrieImpl<Value> implements Trie<Value> {
    
    private class Node<Value> {
        private Set<Value> vals;
        private Node[] links;
        private Node() {
            this.vals=null;
            this.links=new Node[36];
        }
    }
    private Set<Value> deleted;
    private Boolean isThere;
    private Node<?> root;
    private Node<?> removed;
    private Value rem;
    public TrieImpl() {
        this.root = new Node();
    }
    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    @Override
    public void put(String key, Value val) {
        if (key==null) {
            throw new IllegalArgumentException();
        }
        if (key.equals("")||val==null) {
            return;
        }
        key = key.toLowerCase();
        if (this.root==null) {
            this.root=new Node();
        }
        Node temp = this.root;
        for (int i=0; i<key.length(); i++) {
            if (temp.links[findPosition(key.charAt(i))]==null) {
                temp.links[findPosition(key.charAt(i))] = new Node();
            }
            temp = temp.links[findPosition(key.charAt(i))];
        }
        if (temp.vals==null) {
            temp.vals = new HashSet<>();
        }
        temp.vals.add(val);
    }

    

    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE INSENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        if (key==null||comparator==null) {
            throw new IllegalArgumentException();
        }
        if (key.equals("")) {
            return Collections.emptyList();
        }
        List<Value> list = new ArrayList<>();
        key=key.toLowerCase();
        Node temp = this.root;
        if (temp==null) {
            return Collections.emptyList();
        }
        for (int i=0; i<key.length(); i++) {
            if (temp.links[findPosition(key.charAt(i))]==null) {
                return Collections.emptyList();
            }
            temp = temp.links[findPosition(key.charAt(i))];
        }
        if (temp.vals!=null) {
            list.addAll(temp.vals);
        }
        list.sort(comparator);
        return list;
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        if (prefix==null||comparator==null) {
            throw new IllegalArgumentException();
        }
        if (prefix.equals("")) {
            return Collections.emptyList();
        }
        List<Value> list = new ArrayList<>();
        prefix=prefix.toLowerCase();
        Node temp = this.root;
        if (temp==null) {
            return Collections.emptyList();
        }
        for (int i=0; i<prefix.length(); i++) {
            if (temp.links[findPosition(prefix.charAt(i))]==null) {
                return Collections.emptyList();
            }
            temp = temp.links[findPosition(prefix.charAt(i))];
        }
        list.addAll(getAllValues(temp));
        list.sort(comparator);
        return list;
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if (prefix==null) {
            throw new IllegalArgumentException();
        }
        if (prefix.equals("")) {
            return Collections.emptySet();
        }
        prefix = prefix.toLowerCase();
        this.root = deleteAllWithPrefix(this.root, prefix, 0);
        deleted = getAllValues(removed);
        return deleted;
        
    }

    private Set<Value> getAllValues(Node node) {
        Set<Value> values = new HashSet<>();
        if (node==null) {
            return Collections.emptySet();
        }
        if (node.vals!=null) {
            values.addAll(node.vals);
        }
        for (int i=0; i<node.links.length; i++) {
            if (node.links[i]!=null) {
                values.addAll(getAllValues(node.links[i]));
            }
        }
        return values;
    }
    private Node deleteAllWithPrefix(Node x, String prefix, int d) {
        if (x == null)
        {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == prefix.length())
        {
            removed=x;
            x=null;
            return x;
        }
        //continue down the trie to the target node
        else
        {
            char c = prefix.charAt(d);
            x.links[findPosition(c)] = this.deleteAllWithPrefix(x.links[findPosition(c)], prefix, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.vals != null)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty	
        for (int c = 0; c <36; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }
    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAll(String key){
        if (key==null) {
            throw new IllegalArgumentException();
        }
        if (key.equals("")) {
            return Collections.emptySet();
        }
        key = key.toLowerCase();
        deleted = new HashSet<>();
        this.root = deleteAll(this.root, key, 0);
        return deleted;
    }

    private Node deleteAll(Node x, String key, int d) {
        if (x == null)
        {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length())
        {
            if (x.vals!=null) {
                deleted.addAll(x.vals);
            }
            x.vals = null;
        }
        //continue down the trie to the target node
        else
        {
            char c = key.charAt(d);
            x.links[findPosition(c)] = this.deleteAll(x.links[findPosition(c)], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.vals != null)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty	
        for (int c = 0; c <36; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    @Override
    public Value delete(String key, Value val){
        if (key==null||val==null) {
            throw new IllegalArgumentException();
        }
        if (key.equals("")) {
            return null;
        }
        key = key.toLowerCase();
        this.root = delete(this.root, key, val, 0);
        if (isThere) {
            return rem;
        } else {
            return null;
        }
    }
    private Node delete(Node x, String key, Value val, int d) {
        isThere=false;
        if (x == null) {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length()) {
            checkIfThereAndRemove(x, key, val);
        } //continue down the trie to the target node
        else {
            char c = key.charAt(d);
            x.links[findPosition(c)] = this.delete(x.links[findPosition(c)], key, val, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.vals != null) {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty	
        for (int c = 0; c <36; c++) {
            if (x.links[c] != null) {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }
    private void checkIfThereAndRemove(Node x, String key, Value val) {
        isThere = (x.vals!=null)&&x.vals.contains(val);
            if (isThere) {
                for (Value v : (Set<Value>)x.vals) {
                    if (v.equals(val)) {
                        rem=v;
                    }
                }
            }
            if (x.vals!=null) {
                x.vals.remove(val);
                if (x.vals.isEmpty()) {
                    x.vals=null;
                }
            }
    }
    private int findPosition(char c) {
        if ((int)c>=48 && (int)c<=57) {
            return (int)c-48;
        }
        else {
            return (int)c-87;
        }
    }
}
