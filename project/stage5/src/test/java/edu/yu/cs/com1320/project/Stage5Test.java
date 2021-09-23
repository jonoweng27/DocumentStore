package edu.yu.cs.com1320.project;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.*;

import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;

public class Stage5Test {
    //variables to hold possible values for doc1
    private URI uri1;
    private byte[] b1;
    String s1;

    //variables to hold possible values for doc2
    private URI uri2;
    byte[] b2;
    String s2;

    private URI uri3;
    byte[] b3;
    String s3;

    private URI uri4;
    byte[] b4;
    String s4;

    private URI uri5;
    byte[] b5;
    String s5;

    private URI uri6;
    byte[] b6;
    String s6;

    DocumentImpl docBin1;
    DocumentImpl docBin2;
    DocumentImpl docBin3;
    DocumentImpl docBin4;
    DocumentImpl docBin5;
    DocumentImpl docBin6;

    DocumentImpl docTxt1;
    DocumentImpl docTxt2;
    DocumentImpl docTxt3;
    DocumentImpl docTxt4;
    DocumentImpl docTxt5;
    DocumentImpl docTxt6;

    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.b1 = new byte[1];
        this.s1 = "pizza pious";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.b2 = new byte[2];
        this.s2 = "Party times";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.b3 = new byte[3];
        this.s3 = "nothi mucho";

        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.b4 = new byte[4];
        this.s4 = "not goingss";

        this.uri5 = new URI("http://edu.yu.cs/com1320/project/doc5");
        this.b5 = new byte[5];
        this.s5 = "p like fizz";

        this.uri6 = new URI("http://edu.yu.cs/com1320/project/doc6");
        this.b6 = new byte[33];
        this.s6 = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz";

        docBin1 = new DocumentImpl(this.uri1, this.b1);
        docBin2 = new DocumentImpl(this.uri2, this.b2);
        docBin3 = new DocumentImpl(this.uri3, this.b3);
        docBin4 = new DocumentImpl(this.uri4, this.b4);
        docBin5 = new DocumentImpl(this.uri5, this.b5);
        docBin6 = new DocumentImpl(this.uri6, this.b6);

        docTxt1 = new DocumentImpl(this.uri1, this.s1);
        docTxt2 = new DocumentImpl(this.uri2, this.s2);
        docTxt3 = new DocumentImpl(this.uri3, this.s3);
        docTxt4 = new DocumentImpl(this.uri4, this.s4);
        docTxt5 = new DocumentImpl(this.uri5, this.s5);
        docTxt6 = new DocumentImpl(this.uri6, this.s6);
    }
    @AfterEach
    void delete() {
        File file = new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project");
        while (!(file.getPath().endsWith("Desktop"))) {
            file.delete();
            file=file.getParentFile();
        }
    }
    @Test
    void basicDocumentSizeSetGetTest() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.putDocument(new ByteArrayInputStream(this.b1),this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.b2),this.uri2, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.b3),this.uri3, DocumentStore.DocumentFormat.BINARY);
        store.setMaxDocumentCount(2);
        assertNotNull(store.getDocument(uri2));
        assertNotNull(store.getDocument(uri3));
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        store.setMaxDocumentCount(100);
        assertEquals(docBin1, store.getDocument(uri1));
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc3.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
    }
    @Test
    void basicDocumentSizeSetSearchTest() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.putDocument(new ByteArrayInputStream(this.s1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.s3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        store.setMaxDocumentCount(2);
        assertNotNull(store.getDocument(uri2));
        assertNotNull(store.getDocument(uri3));
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        store.setMaxDocumentCount(100);
        assertEquals(1, store.search("pizza").size());
        assertEquals(docTxt1, store.search("pizza").get(0));
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
    }
    @Test
    void basicDocumentSizeSetSearchWithPrefixTest() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.putDocument(new ByteArrayInputStream(this.s1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.s3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        store.setMaxDocumentCount(2);
        assertNotNull(store.getDocument(uri2));
        assertNotNull(store.getDocument(uri3));
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        store.setMaxDocumentCount(100);
        assertEquals(1, store.searchByPrefix("piz").size());
        assertEquals(docTxt1, store.searchByPrefix("piz").get(0));
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
    }
    @Test
    void testMultipleDocsRestoredSameUriTime() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.putDocument(new ByteArrayInputStream(this.b1),this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.b2),this.uri2, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.b3),this.uri3, DocumentStore.DocumentFormat.BINARY);
        store.setMaxDocumentBytes(33);
        store.putDocument(new ByteArrayInputStream(this.b6),this.uri6, DocumentStore.DocumentFormat.BINARY);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc3.json").exists());
        store.undo();
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc3.json").exists());
        assertNull(store.getDocument(uri6));
    }
    @Test
    void testNoLimitsGetDocument() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.setMaxDocumentCount(0);  
        store.putDocument(new ByteArrayInputStream(this.b1),this.uri1, DocumentStore.DocumentFormat.BINARY);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertEquals(docBin1, store.getDocument(uri1));
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertTrue(store.deleteDocument(uri1));
        assertFalse(store.deleteDocument(uri2));
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
    }
    @Test
    void challengingDocumentBytesSetTest() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        store.getDocument(uri1);
        store.setMaxDocumentBytes(11);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc3.json").exists());
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc4.json").exists());
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc5.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertTrue(store.deleteDocument(uri1));
        assertTrue(store.deleteDocument(uri2));
        assertTrue(store.deleteDocument(uri3));
        assertTrue(store.deleteDocument(uri4));
        assertTrue(store.deleteDocument(uri5));   
    }
    @Test
    void testSearchWithMoreDocsThanCanHold() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.setMaxDocumentCount(2);
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s5.getBytes()),this.uri5, DocumentStore.DocumentFormat.TXT);
        assertEquals(3, store.searchByPrefix("p").size());
        int count = 0;
        if (new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists()) {
            count++;
        }
        if (new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists()) {
            count++;
        }
        if (new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc5.json").exists()) {
            count++;
        }
        assertEquals(1, count);
        assertEquals(3, store.deleteAllWithPrefix("p").size());
    }
    @Test
    void seeWhichDocMovesToDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.setMaxDocumentCount(2);
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc3.json").exists());
        assertTrue(store.deleteDocument(uri1));
    }
    @Test
    void putReplaceDeletesFromDiskAndUndo() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.setMaxDocumentCount(1);
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s3.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        store.undo();
        assertEquals(docTxt1, store.getDocument(uri1));
        assertEquals(docTxt2, store.getDocument(uri2));
        store.deleteDocument(uri1);
        store.deleteDocument(uri2);
    }
    @Test
    void putReplaceDeletesFromDiskAndUndoUri() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.setMaxDocumentCount(1);
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s3.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        store.undo(uri1);
        assertEquals(docTxt1, store.getDocument(uri1));
        assertEquals(docTxt2, store.getDocument(uri2));
        store.deleteDocument(uri1);
        store.deleteDocument(uri2);
    }
    @Test
    void putReplaceDeletesFromDiskAndUndoUri2() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.setMaxDocumentCount(1);
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s3.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        store.undo(uri1);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        store.deleteDocument(uri1);
        store.deleteDocument(uri2);
    }
    @Test
    void testDeleteAll() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.setMaxDocumentCount(0);
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s5.getBytes()),this.uri5, DocumentStore.DocumentFormat.TXT);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc5.json").exists());
        assertEquals(3, store.deleteAllWithPrefix("p").size());
        assertNull(store.getDocument(uri1));
        assertNull(store.getDocument(uri2));
        assertNull(store.getDocument(uri5));
    }
    @Test
    void testPutBackAtSameTime() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.setMaxDocumentBytes(33);
        store.putDocument(new ByteArrayInputStream(s6.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        store.undo();
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertEquals(docTxt1, store.getDocument(uri1));
        assertEquals(docTxt2, store.getDocument(uri2));
        store.deleteDocument(uri1);
        store.deleteDocument(uri2);
        assertEquals(null, store.getDocument(uri1));
        assertEquals(null, store.getDocument(uri2));
    }
    @Test
    void undoDeleteAllWithPrefix() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s5.getBytes()),this.uri5, DocumentStore.DocumentFormat.TXT);
        store.deleteAllWithPrefix("p");
        assertNull(store.getDocument(uri1));
        assertNull(store.getDocument(uri2));
        assertNull(store.getDocument(uri5));
        store.setMaxDocumentCount(0);
        store.undo();
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc5.json").exists());
        store.deleteAllWithPrefix("p");
    }
    @Test
    void undoURIDeleteAllWithPrefix() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s5.getBytes()),this.uri5, DocumentStore.DocumentFormat.TXT);
        store.deleteAllWithPrefix("p");
        assertNull(store.getDocument(uri1));
        assertNull(store.getDocument(uri2));
        assertNull(store.getDocument(uri5));
        store.setMaxDocumentCount(2);
        store.undo(uri1);
        store.undo(uri2);
        store.undo(uri5);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc5.json").exists());
        store.deleteAllWithPrefix("p");
    }
    @Test
    void testUndoDelete() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.setMaxDocumentCount(1);
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(s5.getBytes()),this.uri5, DocumentStore.DocumentFormat.TXT);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc5.json").exists());
        assertEquals(3, store.deleteAllWithPrefix("p").size());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc2.json").exists());
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc5.json").exists());
        assertNull(store.getDocument(uri1));
        assertNull(store.getDocument(uri2));
        assertNull(store.getDocument(uri5));
        store.undo();
        assertEquals(docTxt1, store.getDocument(uri1));
        assertEquals(docTxt2, store.getDocument(uri2));
        assertEquals(docTxt5, store.getDocument(uri5));
        assertEquals(3, store.deleteAllWithPrefix("p").size());
    }
    @Test
    void putOverwriteOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.setMaxDocumentCount(0);
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertEquals(docTxt1, store.getDocument(uri1));
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        store.putDocument(new ByteArrayInputStream(s2.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertNotNull(store.getDocument(uri1));
        assertNotEquals(docTxt1, store.getDocument(uri1));
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        store.undo();
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertEquals(docTxt1, store.getDocument(uri1));
        assertTrue(store.deleteDocument(uri1));
        assertFalse(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertNull(store.getDocument(uri1));
    }
    @Test
    void testDeleteReturnsAsExpected() throws IOException {
        DocumentStore store = new DocumentStoreImpl(new File(System.getProperty("user.home") + "/Desktop"));
        store.setMaxDocumentCount(0);
        store.putDocument(new ByteArrayInputStream(s1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertEquals(docTxt1, store.getDocument(uri1));
        assertTrue(new File(System.getProperty("user.home") + "/Desktop/edu.yu.cs/com1320/project/doc1.json").exists());
        assertTrue(store.deleteDocument(uri1));
        assertFalse(store.deleteDocument(uri1));
    }
}