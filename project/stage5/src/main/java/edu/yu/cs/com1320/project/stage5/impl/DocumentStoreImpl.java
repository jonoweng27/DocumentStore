package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage5.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentStoreImpl implements DocumentStore {
    private StackImpl<Undoable> stack;
    private BTreeImpl<URI, Document> documents;
    private TrieImpl<URI> trie;
    private MinHeapImpl<Entry> heap;
    private Map<URI, Entry> uriToEntry;
    private int maxDocumentCount;
    private int maxDocumentBytes;
    private int documentCount;
    private int documentBytes;
    private Set<URI> urisOnDisk;
    private class Entry implements Comparable<Entry> {
        private URI uri;
        private Entry (URI uri) {
            this.uri=uri;
        }
        @Override
        public int compareTo(Entry e) {
            return documents.get(uri).compareTo(documents.get(e.uri));
        }
    }
    public DocumentStoreImpl() {
       this.documents = new BTreeImpl<>();
       this.documents.setPersistenceManager(new DocumentPersistenceManager(null));
       this.stack = new StackImpl<>();
       this.trie = new TrieImpl<>();
       this.heap = new MinHeapImpl<>();
       this.maxDocumentCount=-1;
       this.maxDocumentBytes=-1;
       this.documentCount=0;
       this.documentBytes=0;
       this.urisOnDisk=new HashSet<>();
       this.uriToEntry=new HashMap<>();
    }
    public DocumentStoreImpl(File baseDir) {
        this.documents = new BTreeImpl<>();
        this.documents.setPersistenceManager(new DocumentPersistenceManager(baseDir));
        this.stack = new StackImpl<>();
        this.trie = new TrieImpl<>();
        this.heap = new MinHeapImpl<>();
        this.maxDocumentCount=-1;
        this.maxDocumentBytes=-1;
        this.documentCount=0;
        this.documentBytes=0;
        this.urisOnDisk=new HashSet<>();
        this.uriToEntry=new HashMap<>();
     }
    
     /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     */
    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if ((uri==null)||(format==null)) {
            throw new IllegalArgumentException();
        }
        //this is a delete
        if (input==null) {
            return this.documentPlace(null, uri);
        }
        byte[] docContents = input.readAllBytes();
        DocumentImpl doc;
        if (format==DocumentFormat.BINARY) {
            doc = new DocumentImpl(uri, docContents);
        } else {
            doc = new DocumentImpl(uri, new String(docContents));
        }
        return this.documentPlace(doc, uri);
    }
    
    private int documentPlace(DocumentImpl doc, URI uri) {
        Document prevDoc = this.documents.get(uri);
        //if there is a previous doc, remove it from heap and from document count and bytes
        if (prevDoc!=null) {
            prevDocNotNull(doc, uri, prevDoc);
        }
        this.documents.put(uri, doc);
        //Add new doc to heap
        if (doc!=null) {
            currentDocNotNull(doc, uri, prevDoc);
        }
        Set<URI> docsMovedToDisk = this.checkAndMoveExtraDocs();
        stack.push(new GenericCommand<URI>(uri, a -> documentPut(uri, prevDoc, docsMovedToDisk)));
        return (prevDoc==null ? 0 : prevDoc.hashCode());
    }
    private void prevDocNotNull(DocumentImpl doc, URI uri, Document prevDoc) {
        //HAVE TO CHECK AND SEE IF THIS DOCUMENT WAS DESERILIZED
        if (!this.urisOnDisk.contains(uri)) {
            removeUriInMemory(uri, prevDoc);
            this.zeroAndRemoveFromHeap(prevDoc.getKey());
            if (doc==null) {
                this.uriToEntry.put(uri, null);
            }
        } else {
            this.urisOnDisk.remove(uri);
        }
        //If previous is a string doc, delete from trie
        if (prevDoc.getDocumentTxt()!=null) {
            for (String s : prevDoc.getWords()) {
                trie.delete(s, prevDoc.getKey());
            }
        }
    }
    private void currentDocNotNull(DocumentImpl doc, URI uri, Document prevDoc) {
        doc.setLastUseTime(System.nanoTime());
        Entry e = new Entry(uri);
        this.uriToEntry.put(uri, e);
        heap.insert(e);
        this.documentCount++;
        this.documentBytes+=this.getDocumentBytes(doc);
        //If new is a string doc, add to trie
        if (doc.getDocumentTxt()!=null) {
            for (String s : doc.getWords()) {
                trie.put(s, doc.getKey());
            }
        }
    }
    private void removeUriInMemory(URI uri, Document doc) {
            this.documentCount--;
            this.documentBytes-=this.getDocumentBytes(doc);
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document getDocument(URI uri) {
        if (uri==null) {
            throw new IllegalArgumentException();
        }
        Document doc = this.documents.get(uri);
        takeOffDiskAndCheck(uri, doc);
        if (doc!=null) {  
            this.changeTimeAndReheapify(doc, System.nanoTime());
        }
        this.checkAndMoveExtraDocs();
        return doc;
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean deleteDocument(URI uri) {
        if (uri==null) {
            throw new IllegalArgumentException();
        }
        Document previousDoc = this.documents.get(uri);
        //no-op command - delete didn't do anything
        if (previousDoc==null) {
            stack.push(new GenericCommand<URI>(uri, a -> doNothing()));
        } else {
            //if old is a string doc, remove from trie
            if (previousDoc.getDocumentTxt()!=null) {
                for (String s : previousDoc.getWords()) {
                    trie.delete(s, previousDoc.getKey());
                }
            }
            if (!this.urisOnDisk.contains(uri)) { //the document is in memory - remove from heap and update size
                removeUriInMemory(uri, previousDoc);
                this.zeroAndRemoveFromHeap(previousDoc.getKey());
            } else {
                this.urisOnDisk.remove(uri);
            }
            this.uriToEntry.put(uri, null);
            //put from null to something
            stack.push(new GenericCommand<URI>(uri, a -> documentPut(a, previousDoc, null)));
        }
        return (this.documents.put(uri, null)!=null);
    }

    private boolean documentPut(URI uri, Document doc, Set<URI> docsPutBackInMemory) {
        Document prevDoc = this.documents.get(uri);
        //if there is a previous doc, remove it from heap and from document count and bytes
        if (prevDoc!=null) {
            prevDocNotNull2(uri, doc, prevDoc);
        }
        this.documents.put(uri, doc);
        //Add new doc to heap
        if (doc!=null) {
            docNotNull2(uri, doc);
        }
        if (docsPutBackInMemory!=null) {
            long time = System.nanoTime();
            for (URI u : docsPutBackInMemory) {
                Document retrieved = this.documents.get(u);
                this.documentCount++;
                this.documentBytes+=this.getDocumentBytes(retrieved);
                retrieved.setLastUseTime(time);
                Entry e = new Entry(u);
                this.uriToEntry.put(u, e);
                heap.insert(e);
                this.urisOnDisk.remove(u);
            }
            if (doc!=null) {
                doc.setLastUseTime(time);
                heap.reHeapify(this.uriToEntry.get(uri));
            }
        }
        return true;
    }
    private void prevDocNotNull2(URI uri, Document doc, Document prevDoc) {
        //HAVE TO CHECK AND SEE IF THIS DOCUMENT WAS DESERILIZED
        if (!this.urisOnDisk.contains(uri)) {
            removeUriInMemory(uri, prevDoc);
            this.zeroAndRemoveFromHeap(prevDoc.getKey());
            if (doc==null) {
                this.uriToEntry.put(uri, null);
            }
        } else {
            this.urisOnDisk.remove(uri);
        }
        //If previous is a string doc, delete from trie
        if (prevDoc.getDocumentTxt()!=null) {
            for (String s : prevDoc.getWords()) {
                trie.delete(s, prevDoc.getKey());
            }
        }
    }
    private void docNotNull2(URI uri, Document doc) {
        doc.setLastUseTime(System.nanoTime());
        Entry e = new Entry(uri);
        this.uriToEntry.put(uri, e);
        heap.insert(e);
        this.documentCount++;
        this.documentBytes+=this.getDocumentBytes(doc);
        //If new is a string doc, add to trie
        if (doc.getDocumentTxt()!=null) {
            for (String s : doc.getWords()) {
                trie.put(s, doc.getKey());
            }
        }
    }
    private boolean doNothing() {
        return true;
    }
    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    @Override
    public void undo() throws IllegalStateException {
        if (stack.size()==0) {
            throw new IllegalStateException();
        }
        Undoable undoable = stack.pop();
        if (undoable instanceof GenericCommand) {
            URI uri = (URI)((GenericCommand)undoable).getTarget();
            undoable.undo();
        } else {
            long time = System.nanoTime();
            CommandSet c = (CommandSet)undoable;
            Iterator iterator = c.iterator();
            Set<URI> uris = new HashSet<>();
            while (iterator.hasNext()) {
                GenericCommand g = (GenericCommand)iterator.next();
                uris.add((URI)g.getTarget());
            }
            for (URI uri1 : uris) {
                c.undo(uri1);
                Document doc = this.documents.get(uri1);
                if (doc!=null) {
                    this.changeTimeAndReheapify(doc, time);
                }
            }
        }
        this.checkAndMoveExtraDocs();
    }
    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    @Override
    public void undo(URI uri) throws IllegalStateException {
        this.checkAndThrowExceptions(uri);
        StackImpl<Undoable> temp = new StackImpl<>();
        while(!containsURI(stack.peek(), uri)) {
            temp.push(stack.pop());
            if (stack.size()==0) {
                while (temp.size()!=0) {
                    stack.push(temp.pop());
                }
                throw new IllegalStateException();
            }
        }
        if (stack.peek() instanceof GenericCommand) {
            stack.pop().undo();
        } else {
            CommandSet g= (CommandSet)stack.peek();
            g.undo(uri);
            if (g.size()==0) {
                stack.pop();
            }
        }
        while (temp.size()!=0) {
            stack.push(temp.pop());
        }
        this.checkAndMoveExtraDocs();
    }

    private void checkAndThrowExceptions(URI uri) {
        if (uri==null) {
            throw new IllegalArgumentException();
        }
        if (stack.size()==0) {
            throw new IllegalStateException();
        }
    }

    private boolean containsURI(Undoable u, URI uri) {
        if (u instanceof GenericCommand) {
            GenericCommand g = (GenericCommand)u;
            return g.getTarget().equals(uri);
        } else {
            CommandSet c = (CommandSet)u;
            return c.containsTarget(uri);
        }
    }
    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> search(String keyword) {
        if (keyword==null) {
            throw new IllegalArgumentException();
        }
        keyword=keyword.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        String temp = keyword;
        List<URI> result = trie.getAllSorted(keyword, (a, b) -> a.compareTo(b));
        List<Document> solution = new ArrayList<>();
        long time = System.nanoTime();
        for (URI uri : result) {
            Document doc = this.documents.get(uri);
            takeOffDiskAndCheck(uri, doc);
            this.changeTimeAndReheapify(doc, time);
            //HAVE TO CHECK AND SEE IF THIS DOCUMENT WAS DESERILIZED
            solution.add(doc);
        }
        solution.sort((a,b) -> Integer.compare(b.wordCount(temp), a.wordCount(temp)));
        this.checkAndMoveExtraDocs();
        return solution;
    }
    
    private void takeOffDiskAndCheck(URI uri, Document doc) {
        if (this.urisOnDisk.contains(uri)) {
            this.urisOnDisk.remove(uri);
            this.documentCount++;
            this.documentBytes+=this.getDocumentBytes(doc);
            Entry e = new Entry(uri);
            this.uriToEntry.put(uri, e);
            this.heap.insert(e);
        }
    }

    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        if (keywordPrefix==null) {
            throw new IllegalArgumentException();
        }
        keywordPrefix=keywordPrefix.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        String t = keywordPrefix;
        Set<URI> uris = new HashSet(trie.getAllWithPrefixSorted(t, (a,b) -> a.compareTo(b)));
        HashMap<Document, Integer> prefixCounter = new HashMap<>();
        List<Document> solution = new ArrayList<>();
        for (URI uri : uris) {
            Document doc = this.documents.get(uri);
            solution.add(doc);
            for (String s: doc.getWords()) {
                if (s.length()>=keywordPrefix.length()&&s.substring(0, keywordPrefix.length()).equals(keywordPrefix)) {
                    prefixCounter.put(doc, prefixCounter.getOrDefault(doc, 0)+doc.wordCount(s));
                }
            }
        }
        long time = System.nanoTime();
        solution.sort((a, b) -> Integer.compare(prefixCounter.get(b), prefixCounter.get(a)));
        for (Document d : solution) {
            //HAVE TO CHECK AND SEE IF THIS DOCUMENT WAS DESERILIZED
            takeOffDiskAndCheck(d.getKey(), d);
            this.changeTimeAndReheapify(d, time);
        }
        this.checkAndMoveExtraDocs();
        return solution;
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAll(String keyword) {
        if (keyword==null) {
            throw new IllegalArgumentException();
        }
        keyword=keyword.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        Set<URI> deletedURIs = trie.deleteAll(keyword);
        CommandSet<URI> set = new CommandSet<>();
        //for each doc that will be deleted, remove from trie, remove from heap (if stored in memory), remove from btree, and add to command set of undo's
        for (URI uri : deletedURIs) {
            Document d = this.documents.get(uri);
            for (String s : d.getWords()) {
                trie.delete(s, d.getKey());
            }
            //HAVE TO CHECK AND SEE IF THIS DOCUMENT WAS DESERILIZED
            if (!this.urisOnDisk.contains(uri)) {
                removeUriInMemory(uri, d);
                this.zeroAndRemoveFromHeap(d.getKey());
            } else {
                this.urisOnDisk.remove(uri);
            }
            this.documents.put(d.getKey(), null);
            set.addCommand(new GenericCommand<URI>(d.getKey(), a -> documentPut(a, d, null)));
        }
        stack.push(set);
        return deletedURIs;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        if (keywordPrefix==null) {
            throw new IllegalArgumentException();
        }
        keywordPrefix=keywordPrefix.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        Set<URI> deletedURIs = trie.deleteAllWithPrefix(keywordPrefix);
        CommandSet<URI> set = new CommandSet<>();
        //for each doc that will be deleted, remove from trie, remove from heap (if stored in memory), remove from btree, and add to command set of undo's
        for (URI uri : deletedURIs) {
            Document d = this.documents.get(uri);
            for (String s : d.getWords()) {
                trie.delete(s, d.getKey());
            }
            //HAVE TO CHECK AND SEE IF THIS DOCUMENT WAS DESERILIZED
            if (!this.urisOnDisk.contains(uri)) {
                removeUriInMemory(uri, d);
                this.zeroAndRemoveFromHeap(d.getKey());
            } else {
                this.urisOnDisk.remove(uri);
            }
            this.documents.put(d.getKey(), null);
            set.addCommand(new GenericCommand<URI>(d.getKey(), a -> documentPut(a, d, null)));
        }
        stack.push(set);
        return deletedURIs;
    }
    /**
     * set maximum number of documents that may be stored
     * @param limit
     */
    @Override
    public void setMaxDocumentCount(int limit) {
        this.maxDocumentCount=limit;
        this.checkAndMoveExtraDocs();
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     */
    @Override
    public void setMaxDocumentBytes(int limit) {
        this.maxDocumentBytes=limit;
        this.checkAndMoveExtraDocs();
    }
    private void changeTimeAndReheapify(Document doc, long time) {
        doc.setLastUseTime(time);
        heap.reHeapify(this.uriToEntry.get(doc.getKey()));
    }
    private void zeroAndRemoveFromHeap(URI uri) {
        Document doc = this.documents.get(uri);
        doc.setLastUseTime(0);
        heap.reHeapify(this.uriToEntry.get(uri));
        heap.remove();
    }
    private int getDocumentBytes(Document doc) {
        if (doc.getDocumentBinaryData()!=null) {
            return doc.getDocumentBinaryData().length;
        }
        return doc.getDocumentTxt().getBytes().length;
    }
    
    private Set<URI> checkAndMoveExtraDocs() {
        Set<URI> urisMovedToDisk = new HashSet<>();
        while(((this.maxDocumentCount!=-1)&&this.documentCount>this.maxDocumentCount)||((this.maxDocumentBytes!=-1)&&this.documentBytes>this.maxDocumentBytes)) {
            URI toMove = heap.remove().uri;
            urisMovedToDisk.add(toMove);
            this.uriToEntry.put(toMove, null);
            this.moveToDisk(toMove);
        }
        return urisMovedToDisk;
    }

    private void moveToDisk(URI toMove) {
        Document deleting = this.documents.get(toMove);
        //moves to disk
        try {
            this.documents.moveToDisk(toMove);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //updates sizes
        this.documentCount--;
        this.documentBytes-=this.getDocumentBytes(deleting);
        this.urisOnDisk.add(toMove);
    }
    /*
    private void removeAllFromStack(Document toDelete) {
        URI uri = toDelete.getKey();
        if (stack.size()==0) {
            return;
        }
        StackImpl<Undoable> temp = new StackImpl<>();
        while(stack.size()!=0) {
            Undoable current = stack.pop();
            if ((current instanceof GenericCommand) && (!((URI)((GenericCommand)current).getTarget()).equals(uri))) {
                temp.push(current);
            } else if (current instanceof CommandSet) {
                Iterator iterator = ((CommandSet)current).iterator();
                while (iterator.hasNext()) {
                    GenericCommand g = (GenericCommand)iterator.next();
                    if (g.getTarget().equals(uri)) {
                        iterator.remove();
                        break;
                    }
                }
                if (!((CommandSet) current).isEmpty()) {
                    temp.push(current);
                }
            }
        }
        while (temp.size()!=0) {
            stack.push(temp.pop());
        }
    }*/
}