package edu.yu.cs.com1320.project.stage5.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;

import edu.yu.cs.com1320.project.stage5.Document;

public class DocumentImpl implements Document {
    URI uri;
    String txt;
    byte[] binaryData;
    long time;
    Map<String, Integer> wordCount;
    public DocumentImpl(URI uri, String txt) {
        if ((uri==null)||(txt==null)||(uri.toString().equals(""))||(txt.equals(""))) {
            throw new IllegalArgumentException();
        }
        this.uri=uri;
        this.txt=txt;
        this.binaryData=null;
        this.wordCount=new HashMap<>();
        String[] words=txt.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().trim().split("\\s+");
        for (int i=0; i<words.length; i++) {
            this.wordCount.put(words[i], this.wordCount.getOrDefault(words[i], 0)+1);
        }
        this.time = System.nanoTime();
    }

    public DocumentImpl(URI uri, byte[] binaryData) {
        if ((uri==null)||(binaryData==null)||(uri.toString().equals(""))||(binaryData.length==0)) {
            throw new IllegalArgumentException();
        }
        this.uri=uri;
        this.binaryData=binaryData;
        this.txt=null;
        this.wordCount=new HashMap<>();
        this.time = System.nanoTime();
    }
    /**
     * @return content of text document
     */
    @Override
    public String getDocumentTxt() {
        return this.txt;
    }

    /**
     * @return content of binary data document
     */
    @Override
    public byte[] getDocumentBinaryData(){
        return this.binaryData;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    @Override
    public URI getKey() {
        return this.uri;
    }

    @Override
    public boolean equals(Object o) {
        if (o==null) {
            return false;
        }
        if (! (o instanceof DocumentImpl)) {
            return false;
        }
        DocumentImpl other = (DocumentImpl)o;
        return this.hashCode()== other.hashCode();
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (txt != null ? txt.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

    /**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    @Override
    public int wordCount(String word) {
        if (word==null) {
            throw new IllegalArgumentException();
        }
        if (this.binaryData!=null) {
            return 0;
        }
        String s = word.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        Integer result = this.wordCount.get(s);
        if (result==null) {
            return 0;
        }
        return result;
    }
    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords() {
        return this.wordCount.keySet();
    }

    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
    @Override
    public long getLastUseTime() {
        return this.time;
    }
    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.time = timeInNanoseconds;
    }

    @Override
    public int compareTo(Document o) {
        if (o==null) {
            throw new IllegalArgumentException();
        }
        Long a = this.getLastUseTime();
        Long b = o.getLastUseTime();
        return a.compareTo(b);
    }
    /**
     * @return a copy of the word to count map so it can be serialized
     */
    @Override
    public Map<String,Integer> getWordMap() {
        return new HashMap(this.wordCount);
    }

    /**
     * This must set the word to count map during deserialization
     * @param wordMap
     */
    @Override
    public void setWordMap(Map<String,Integer> wordMap) {
        this.wordCount=wordMap;
    }
}
