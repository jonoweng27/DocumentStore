package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import javax.xml.bind.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    private File baseDir;
    public DocumentPersistenceManager(File baseDir){
        if (baseDir==null) {
            this.baseDir=new File(System.getProperty("user.dir"));
        } else {
            this.baseDir=baseDir;
        }
        if (!this.baseDir.exists()) {
            this.baseDir.mkdirs();
        }   
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        if (uri==null||val==null) {
            throw new IllegalArgumentException();
        }
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.registerTypeAdapter(Document.class, (JsonSerializer<Document>) (document, type, jsonSerializationContext) -> {
            JsonObject json = new JsonObject();
            json.addProperty("Text", val.getDocumentTxt());
            if (val.getDocumentBinaryData()==null) {
                json.addProperty("Byte Array", (String)null);
            } else {
                json.addProperty("Byte Array", DatatypeConverter.printBase64Binary(val.getDocumentBinaryData()));
            }
            json.add("URI", new Gson().toJsonTree(uri));
            json.add("Word Count Map", new Gson().toJsonTree(val.getWordMap()));
            return json;
        }).serializeNulls().create();
        File file = extracted(uri);
        FileWriter fileWriter = new FileWriter(file);
        gson.toJson(val, Document.class, fileWriter);
        fileWriter.close();
    }

    private File extracted(URI uri) {
        File directory = new File(baseDir.toString()+File.separator+uri.getHost()+File.separator+uri.getPath().replace("/", File.separator)+".json");
        //make directory if it doesn't exist
        directory.getParentFile().mkdirs();
        return directory;
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        if (uri==null) {
            throw new IllegalArgumentException();
        }
        File file = extracted(uri);
        if (!file.exists()) {
            return null;
        }
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.registerTypeAdapter(Document.class, (JsonDeserializer<Document>) (element, type, jsonDeserializationContext) -> {
            return extracted(element);
        }).create();
        FileReader filereader = new FileReader(file);
        Document retrivedDoc = gson.fromJson(filereader, Document.class);
        filereader.close();
        this.delete(uri);
        return retrivedDoc;
    }

    private Document extracted(JsonElement element) {
        if (element==null) {
            return null;
        }
        JsonElement jsonText = element.getAsJsonObject().get("Text");
        String documentText = null;
        if (!(jsonText instanceof JsonNull)) {
            documentText = jsonText.getAsString();
        }
        JsonElement jsonByteArray = element.getAsJsonObject().get("Byte Array");
        byte[] byteArray = null;
        if (!(jsonByteArray instanceof JsonNull)) {
            byteArray = DatatypeConverter.parseBase64Binary(jsonByteArray.getAsString());
        }
        URI docURI = new Gson().fromJson(element.getAsJsonObject().get("URI") , URI.class);
        Map<String, Integer> countMap = new Gson().fromJson(element.getAsJsonObject().get("Word Count Map"), new TypeToken<Map<String , Integer>>() {}.getType());
        DocumentImpl updated;
        if (documentText==null) {
            updated = new DocumentImpl(docURI, byteArray);
        } else {
            updated = new DocumentImpl(docURI, documentText);
            updated.setWordMap(countMap);
        }
        updated.setLastUseTime(System.nanoTime());
        return updated;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        if (uri==null) {
            return false;
        }
        File doc = extracted(uri);
        boolean b = doc.delete();
        File file = doc.getParentFile();
        while (!(file.getPath().endsWith(baseDir.getPath()))) {
            file.delete();
            file=file.getParentFile();
        }
        return b;
    }
}
