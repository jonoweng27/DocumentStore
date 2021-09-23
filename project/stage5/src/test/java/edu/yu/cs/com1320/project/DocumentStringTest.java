package edu.yu.cs.com1320.project;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;

public class DocumentStringTest {
    @Test
    void testAStringWithNonAlphaNumeric() throws URISyntaxException {
        DocumentImpl doc = new DocumentImpl(new URI("Apple"), "I LiKe Pizza! I also like it with FREn@*$&ch FrieS! and 2 sodas!");
        assertEquals(2, doc.wordCount("i"));
        assertEquals(2, doc.wordCount("I"));
        assertEquals(0, doc.wordCount("Apple"));
        assertEquals(1, doc.wordCount("FREnch"));
        assertEquals(1, doc.wordCount("french"));
        assertEquals(1, doc.wordCount("2"));
        assertEquals(11, doc.getWords().size());
    }
    @Test
 public void wordCountAndGetWordsTest() throws URISyntaxException {
  DocumentImpl txtDoc = new DocumentImpl(new URI("placeholder"), " The!se ARE? sOme   W@o%$rds with^ s**ymbols (m)ixed [in]. Hope    this test test passes!");
  assertEquals(0, txtDoc.wordCount("bundle"));
  assertEquals(1, txtDoc.wordCount("these"));
  assertEquals(1, txtDoc.wordCount("WORDS"));
  assertEquals(1, txtDoc.wordCount("S-Y-M-B-O-??-LS"));
  assertEquals(1, txtDoc.wordCount("p@A$$sse$s"));
  assertEquals(2, txtDoc.wordCount("tEst"));
  Set<String> words = txtDoc.getWords();
  assertEquals(12, words.size());
  assertTrue(words.contains("some"));

  DocumentImpl binaryDoc = new DocumentImpl(new URI("0110"), new byte[] {0,1,1,0});
  assertEquals(0, binaryDoc.wordCount("anythingYouPutHereShouldBeZero"));
  Set<String> words2 = binaryDoc.getWords();
  assertEquals(0, words2.size());
 }
    
}
