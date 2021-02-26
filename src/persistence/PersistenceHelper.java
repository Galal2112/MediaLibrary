package persistence;

import mediaDB.MediaContent;
import mediaDB.Uploader;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenceHelper {

    public PersistenceHelper() {
    }

    public <T extends Serializable> void saveJOS(List<T> items, String fileName) throws FileNotFoundException, IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        PersistenceManager.saveJOS(oos, items);
    }

    public <T extends Serializable> List<T> loadJOS(String fileName) throws FileNotFoundException, ClassNotFoundException, IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        List<T> result = PersistenceManager.loadJOS(ois);
        return result == null ? new ArrayList<>() : result;
    }

    public void saveMediaUsingJBP(String xmlFileName, List<MediaContent> mediaList) throws IOException, FileNotFoundException {
        XMLEncoder encoder = createXMLEncoder(xmlFileName);
        PersistenceManager.saveMediaUsingJBP(encoder, mediaList);
        encoder.close();
    }

    public List<MediaContent> loadMediaUsingJBP(String xmlFileName) throws IOException, ClassNotFoundException, FileNotFoundException {
        XMLDecoder decoder = createXMLDecoder(xmlFileName);
        return PersistenceManager.loadMediaUsingJBP(decoder);
    }

    public void saveUploadersUsingJBP(String xmlFileName, List<Uploader> uploaders) throws IOException, FileNotFoundException {
        XMLEncoder encoder = createXMLEncoder(xmlFileName);
        PersistenceManager.saveUploadersUsingJBP(encoder, uploaders);
        encoder.close();
    }

    public List<Uploader> loadUploaderUsingJBP(String xmlFileName) throws FileNotFoundException, ClassNotFoundException, IOException {
        XMLDecoder decoder = createXMLDecoder(xmlFileName);
        return PersistenceManager.loadUploaderUsingJBP(decoder);
    }

    public void saveRandom(MediaContent media, String indexFileName, String mediaFileName) throws IOException, FileNotFoundException {
        RandomAccessFile indexRas = new RandomAccessFile(indexFileName, "rw");
        RandomAccessFile mediaRas = new RandomAccessFile(mediaFileName, "rw");
        PersistenceManager.saveRandom(media, indexRas, mediaRas);
    }

    public MediaContent loadRandom(String retrivalAddress, String indexFileName, String mediaFileName) throws IOException, FileNotFoundException {
        RandomAccessFile indexRas = new RandomAccessFile(indexFileName, "r");
        RandomAccessFile mediaRas = new RandomAccessFile(mediaFileName, "r");
        return PersistenceManager.loadRandom(retrivalAddress, indexRas, mediaRas);
    }

    private static XMLEncoder createXMLEncoder(String xmlFileName) throws FileNotFoundException {
        return new XMLEncoder(new BufferedOutputStream(new FileOutputStream(xmlFileName)));
    }

    private static XMLDecoder createXMLDecoder(String xmlFileName) throws FileNotFoundException {
        return new XMLDecoder(new BufferedInputStream(new FileInputStream(xmlFileName)));
    }
}
