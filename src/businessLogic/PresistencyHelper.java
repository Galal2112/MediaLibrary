package businessLogic;

import mediaDB.MediaContent;
import mediaDB.Uploader;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class PresistencyHelper {

    private PresistencyHelper() {}

    public static <T extends Serializable> void saveJOS(List<T> items, String fileName) throws FileNotFoundException, IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        PresistencyManager.saveJOS(oos, items);
    }

    public static <T extends Serializable> List<T> loadJOS(String fileName) throws FileNotFoundException, ClassNotFoundException, IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        List<T> result = PresistencyManager.loadJOS(ois);
        return result == null ? new ArrayList<>() : result;
    }

    public static void saveMediaUsingJBP(String xmlFileName, List<MediaContent> mediaList) throws IOException, FileNotFoundException {
        XMLEncoder encoder = createXMLEncoder(xmlFileName);
        PresistencyManager.saveMediaUsingJBP(encoder, mediaList);
        encoder.close();
    }

    public static List<MediaContent> loadMediaUsingJBP(String xmlFileName) throws IOException, ClassNotFoundException, FileNotFoundException {
        XMLDecoder decoder = createXMLDecoder(xmlFileName);
        return PresistencyManager.loadMediaUsingJBP(decoder);
    }

    public static void saveUploadersUsingJBP(String xmlFileName, List<Uploader> uploaders) throws IOException, FileNotFoundException {
        XMLEncoder encoder = createXMLEncoder(xmlFileName);
        PresistencyManager.saveUploadersUsingJBP(encoder, uploaders);
        encoder.close();
    }

    public static List<Uploader> loadUploaderUsingJBP(String xmlFileName) throws FileNotFoundException, ClassNotFoundException, IOException {
        XMLDecoder decoder = createXMLDecoder(xmlFileName);
        return PresistencyManager.loadUploaderUsingJBP(decoder);
    }

    private static XMLEncoder createXMLEncoder(String xmlFileName) throws FileNotFoundException {
        return new XMLEncoder(new BufferedOutputStream(new FileOutputStream(xmlFileName)));
    }

    private static XMLDecoder createXMLDecoder(String xmlFileName) throws FileNotFoundException {
        return new XMLDecoder(new BufferedInputStream(new FileInputStream(xmlFileName)));
    }
}
