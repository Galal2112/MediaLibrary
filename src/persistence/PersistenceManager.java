package persistence;

import mediaDB.*;
import model.*;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public final class PersistenceManager {
    public static final int INDEX_SIZE_SEEK = Long.SIZE / 8;
    public static final int INDEX_SIZE = (Long.SIZE + Long.SIZE) / 8;

    private PersistenceManager() {
    }

    public static void saveMediaUsingJBP(XMLEncoder encoder, List<MediaContent> mediaList) throws IOException {
        encoder.setPersistenceDelegate(BigDecimal.class, encoder.getPersistenceDelegate(Double.class));
        encoder.writeObject(mediaList);
    }

    public static List<MediaContent> loadMediaUsingJBP(XMLDecoder decoder) throws ClassNotFoundException, IOException {
        return (LinkedList<MediaContent>) decoder.readObject();
    }

    public static void saveUploadersUsingJBP(XMLEncoder encoder, List<Uploader> uploaders) throws IOException {
        encoder.writeObject(uploaders);
    }

    public static List<Uploader> loadUploaderUsingJBP(XMLDecoder decoder) throws IOException, ClassNotFoundException {
        return (LinkedList<Uploader>) decoder.readObject();
    }

    public static <T extends Serializable> void saveJOS(ObjectOutput objectOutput, List<T> items) throws IOException {
        objectOutput.writeObject(items);
    }

    public static <T extends Serializable> List<T> loadJOS(ObjectInput objectInput) throws ClassNotFoundException, IOException {
        return (List<T>) objectInput.readObject();
    }

    public static void saveRandom(MediaContent media, RandomAccessFile indexRas, RandomAccessFile mediaRas) throws IOException {
        long seek = 0;
        boolean found = false;
        long address = Long.parseLong(media.getAddress().split("@")[0]);

        indexRas.seek(0);
        while (indexRas.getFilePointer() <= indexRas.length() - INDEX_SIZE) {
            long currentddress = indexRas.readLong();
            if (currentddress == address) {
                found = true;
                seek = indexRas.readLong();
                break;
            }
            indexRas.skipBytes(INDEX_SIZE_SEEK);
        }

        if (!found) {
            indexRas.writeLong(address);
            indexRas.writeLong(mediaRas.length());
            indexRas.close();
            seek = mediaRas.length();
        }
        if (media instanceof LicensedAudioVideoImpl) {
            LicensedAudioVideoImpl licensedAudioVideo = (LicensedAudioVideoImpl) media;
            saveLicensedAudioVideo(mediaRas, seek, licensedAudioVideo);
        } else if (media instanceof InteractiveVideoImpl) {
            InteractiveVideoImpl interactiveVideo = (InteractiveVideoImpl) media;
            saveInteractiveVideo(mediaRas, seek, interactiveVideo);
        } else if (media instanceof LicensedVideoImpl) {
            LicensedVideoImpl licensedVideo = (LicensedVideoImpl) media;
            saveLicensedVideo(mediaRas, seek, licensedVideo);
        } else if (media instanceof LicensedAudioImpl) {
            LicensedAudioImpl licensedAudio = (LicensedAudioImpl) media;
            saveLicensedAudio(mediaRas, seek, licensedAudio);
        } else if (media instanceof AudioVideoImpl) {
            AudioVideoImpl audioVideo = (AudioVideoImpl) media;
            saveAudioVideo(mediaRas, seek, audioVideo);
        } else if (media instanceof AudioImpl) {
            AudioImpl audio = (AudioImpl) media;
            saveAudio(mediaRas, seek, audio);
        } else if (media instanceof VideoImpl) {
            VideoImpl video = (VideoImpl) media;
            saveVideo(mediaRas, seek, video);
        }
        mediaRas.close();
    }

    public static MediaContent loadRandom(String retrivalAddress, RandomAccessFile indexRas, RandomAccessFile mediaRas) throws IOException {
        long seek = -1;
        long address = Long.parseLong(retrivalAddress.split("@")[0]);

        indexRas.seek(0);
        while (indexRas.getFilePointer() <= indexRas.length() - INDEX_SIZE) {
            long currentddress = indexRas.readLong();
            if (currentddress == address) {
                seek = indexRas.readLong();
                break;
            }
            indexRas.skipBytes(INDEX_SIZE_SEEK);
        }

        if (seek < 0) {
            throw new IllegalArgumentException("Address not found");
        }

        mediaRas.seek(seek);
        String className = mediaRas.readUTF();
        MediaContent mediaContent = null;
        if (className.equals(LicensedAudioVideoImpl.class.getSimpleName())) {
            mediaContent = loadLicensedAudioVideo(mediaRas);
        } else if (className.equals(InteractiveVideoImpl.class.getSimpleName())) {
            mediaContent = loadInteractiveVideo(mediaRas);
        } else if (className.equals(LicensedVideoImpl.class.getSimpleName())) {
            mediaContent = loadLicensedVideo(mediaRas);
        } else if (className.equals(LicensedAudioImpl.class.getSimpleName())) {
            mediaContent = loadLicensedAudio(mediaRas);
        } else if (className.equals(AudioVideoImpl.class.getSimpleName())) {
            mediaContent = loadAudioVideo(mediaRas);
        } else if (className.equals(AudioImpl.class.getSimpleName())) {
            mediaContent = loadAudio(mediaRas);
        } else if (className.equals(VideoImpl.class.getSimpleName())) {
            mediaContent = loadVideo(mediaRas);
        }
        return mediaContent;
    }

    private static void saveLicensedVideo(RandomAccessFile randomAccessFile, long seekPos, LicensedVideoImpl licensedVideo) throws IOException {
        saveVideo(randomAccessFile, seekPos, licensedVideo, LicensedVideoImpl.class);
        randomAccessFile.writeUTF(licensedVideo.getHolder());
    }

    private static void saveLicensedAudio(RandomAccessFile randomAccessFile, long seekPos, LicensedAudioImpl licensedAudio) throws IOException {
        saveAudio(randomAccessFile, seekPos, licensedAudio, LicensedAudioImpl.class);
        randomAccessFile.writeUTF(licensedAudio.getHolder());
    }

    private static void saveAudioVideo(RandomAccessFile randomAccessFile, long seekPos, AudioVideoImpl audioVideo) throws IOException {
        saveAudio(randomAccessFile, seekPos, audioVideo, AudioVideoImpl.class);
        randomAccessFile.writeInt(audioVideo.getWidth());
        randomAccessFile.writeInt(audioVideo.getHeight());
    }

    private static void saveAudio(RandomAccessFile randomAccessFile, long seekPos, AudioImpl audio) throws IOException {
        saveAudio(randomAccessFile, seekPos, audio, AudioImpl.class);
    }

    private static void saveVideo(RandomAccessFile randomAccessFile, long seekPos, VideoImpl video) throws IOException {
        saveVideo(randomAccessFile, seekPos, video, VideoImpl.class);
    }

    private static void saveInteractiveVideo(RandomAccessFile randomAccessFile, long seekPos, InteractiveVideoImpl interactiveVideo) throws IOException {
        saveVideo(randomAccessFile, seekPos, interactiveVideo, InteractiveVideoImpl.class);
        randomAccessFile.writeUTF(interactiveVideo.getType());
    }

    private static void saveLicensedAudioVideo(RandomAccessFile randomAccessFile, long seekPos, LicensedAudioVideoImpl licensedAudioVideo) throws IOException {
        saveAudio(randomAccessFile, seekPos, licensedAudioVideo, LicensedAudioVideoImpl.class);
        randomAccessFile.writeInt(licensedAudioVideo.getWidth());
        randomAccessFile.writeInt(licensedAudioVideo.getHeight());
        randomAccessFile.writeUTF(licensedAudioVideo.getHolder());
    }

    private static InteractiveVideoImpl loadInteractiveVideo(RandomAccessFile randomAccessFile) throws IOException {
        InteractiveVideoImpl interactiveVideo = new InteractiveVideoImpl();
        interactiveVideo.setWidth(randomAccessFile.readInt());
        interactiveVideo.setHeight(randomAccessFile.readInt());
        interactiveVideo.setEncoding(randomAccessFile.readUTF());
        interactiveVideo.setBitrate(randomAccessFile.readLong());
        interactiveVideo.setLength(randomAccessFile.readLong());
        interactiveVideo.setSize(new BigDecimal(randomAccessFile.readDouble()));
        interactiveVideo.setAddress(randomAccessFile.readUTF());
        interactiveVideo.setAccessCount(randomAccessFile.readLong());
        interactiveVideo.setUploader(new Producer(randomAccessFile.readUTF()));
        interactiveVideo.setTags(tagsFromString(randomAccessFile.readUTF()));
        interactiveVideo.setUploadDate(new Date(randomAccessFile.readLong()));
        interactiveVideo.setType(randomAccessFile.readUTF());
        return interactiveVideo;
    }

    private static LicensedVideoImpl loadLicensedVideo(RandomAccessFile randomAccessFile) throws IOException {
        LicensedVideoImpl licensedVideo = new LicensedVideoImpl();
        licensedVideo.setWidth(randomAccessFile.readInt());
        licensedVideo.setHeight(randomAccessFile.readInt());
        licensedVideo.setEncoding(randomAccessFile.readUTF());
        licensedVideo.setBitrate(randomAccessFile.readLong());
        licensedVideo.setLength(randomAccessFile.readLong());
        licensedVideo.setSize(new BigDecimal(randomAccessFile.readDouble()));
        licensedVideo.setAddress(randomAccessFile.readUTF());
        licensedVideo.setAccessCount(randomAccessFile.readLong());
        licensedVideo.setUploader(new Producer(randomAccessFile.readUTF()));
        licensedVideo.setTags(tagsFromString(randomAccessFile.readUTF()));
        licensedVideo.setUploadDate(new Date(randomAccessFile.readLong()));
        licensedVideo.setHolder(randomAccessFile.readUTF());
        return licensedVideo;
    }

    private static LicensedAudioImpl loadLicensedAudio(RandomAccessFile randomAccessFile) throws IOException {
        LicensedAudioImpl licensedAudio = new LicensedAudioImpl();
        licensedAudio.setSamplingRate(randomAccessFile.readInt());
        licensedAudio.setEncoding(randomAccessFile.readUTF());
        licensedAudio.setBitrate(randomAccessFile.readLong());
        licensedAudio.setLength(randomAccessFile.readLong());
        licensedAudio.setSize(new BigDecimal(randomAccessFile.readDouble()));
        licensedAudio.setAddress(randomAccessFile.readUTF());
        licensedAudio.setAccessCount(randomAccessFile.readLong());
        licensedAudio.setUploader(new Producer(randomAccessFile.readUTF()));
        licensedAudio.setTags(tagsFromString(randomAccessFile.readUTF()));
        licensedAudio.setUploadDate(new Date(randomAccessFile.readLong()));
        licensedAudio.setHolder(randomAccessFile.readUTF());
        return licensedAudio;
    }

    private static AudioVideoImpl loadAudioVideo(RandomAccessFile randomAccessFile) throws IOException {
        AudioVideoImpl audioVideo = new AudioVideoImpl();
        audioVideo.setSamplingRate(randomAccessFile.readInt());
        audioVideo.setEncoding(randomAccessFile.readUTF());
        audioVideo.setBitrate(randomAccessFile.readLong());
        audioVideo.setLength(randomAccessFile.readLong());
        audioVideo.setSize(new BigDecimal(randomAccessFile.readDouble()));
        audioVideo.setAddress(randomAccessFile.readUTF());
        audioVideo.setAccessCount(randomAccessFile.readLong());
        audioVideo.setUploader(new Producer(randomAccessFile.readUTF()));
        audioVideo.setTags(tagsFromString(randomAccessFile.readUTF()));
        audioVideo.setUploadDate(new Date(randomAccessFile.readLong()));
        audioVideo.setWidth(randomAccessFile.readInt());
        audioVideo.setHeight(randomAccessFile.readInt());
        return audioVideo;
    }

    private static AudioImpl loadAudio(RandomAccessFile randomAccessFile) throws IOException {
        AudioImpl audio = new AudioImpl();
        audio.setSamplingRate(randomAccessFile.readInt());
        audio.setEncoding(randomAccessFile.readUTF());
        audio.setBitrate(randomAccessFile.readLong());
        audio.setLength(randomAccessFile.readLong());
        audio.setSize(new BigDecimal(randomAccessFile.readDouble()));
        audio.setAddress(randomAccessFile.readUTF());
        audio.setAccessCount(randomAccessFile.readLong());
        audio.setUploader(new Producer(randomAccessFile.readUTF()));
        audio.setTags(tagsFromString(randomAccessFile.readUTF()));
        audio.setUploadDate(new Date(randomAccessFile.readLong()));
        return audio;
    }

    private static VideoImpl loadVideo(RandomAccessFile randomAccessFile) throws IOException {
        VideoImpl video = new VideoImpl();
        video.setWidth(randomAccessFile.readInt());
        video.setHeight(randomAccessFile.readInt());
        video.setEncoding(randomAccessFile.readUTF());
        video.setBitrate(randomAccessFile.readLong());
        video.setLength(randomAccessFile.readLong());
        video.setSize(new BigDecimal(randomAccessFile.readDouble()));
        video.setAddress(randomAccessFile.readUTF());
        video.setAccessCount(randomAccessFile.readLong());
        video.setUploader(new Producer(randomAccessFile.readUTF()));
        video.setTags(tagsFromString(randomAccessFile.readUTF()));
        video.setUploadDate(new Date(randomAccessFile.readLong()));
        return video;
    }

    private static LicensedAudioVideoImpl loadLicensedAudioVideo(RandomAccessFile randomAccessFile) throws IOException {
        LicensedAudioVideoImpl licensedAudioVideo = new LicensedAudioVideoImpl();
        licensedAudioVideo.setSamplingRate(randomAccessFile.readInt());
        licensedAudioVideo.setEncoding(randomAccessFile.readUTF());
        licensedAudioVideo.setBitrate(randomAccessFile.readLong());
        licensedAudioVideo.setLength(randomAccessFile.readLong());
        licensedAudioVideo.setSize(new BigDecimal(randomAccessFile.readDouble()));
        licensedAudioVideo.setAddress(randomAccessFile.readUTF());
        licensedAudioVideo.setAccessCount(randomAccessFile.readLong());
        licensedAudioVideo.setUploader(new Producer(randomAccessFile.readUTF()));
        licensedAudioVideo.setTags(tagsFromString(randomAccessFile.readUTF()));
        licensedAudioVideo.setUploadDate(new Date(randomAccessFile.readLong()));
        licensedAudioVideo.setWidth(randomAccessFile.readInt());
        licensedAudioVideo.setHeight(randomAccessFile.readInt());
        licensedAudioVideo.setHolder(randomAccessFile.readUTF());

        return licensedAudioVideo;
    }

    private static String tagsToString(Collection<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return ",";
        }
        String[] tagsArr = tags.stream().map(Enum::toString).collect(Collectors.toList()).toArray(new String[tags.size()]);
        return String.join(",", tagsArr);
    }

    private static List<Tag> tagsFromString(String tagsString) {
        if (tagsString == null) {
            return new ArrayList<>();
        }
        String[] inputTags = tagsString.split(",");
        List<Tag> tags = new ArrayList<>();
        for (String inputTag : inputTags) {
            try {
                tags.add(Tag.valueOf(inputTag));
            } catch (IllegalArgumentException e) {
                // Non existing tags
            }
        }

        return tags;
    }

    private static <T extends Video> void saveVideo(RandomAccessFile randomAccessFile, long seekPos, VideoImpl video, Class<T> clazz) throws IOException {
        randomAccessFile.seek(seekPos);
        randomAccessFile.writeUTF(clazz.getSimpleName());
        randomAccessFile.writeInt(video.getWidth());
        randomAccessFile.writeInt(video.getHeight());
        randomAccessFile.writeUTF(video.getEncoding());
        randomAccessFile.writeLong(video.getBitrate());
        randomAccessFile.writeLong(video.getLength());
        randomAccessFile.writeDouble(video.getSize().doubleValue());
        randomAccessFile.writeUTF(video.getAddress());
        randomAccessFile.writeLong(video.getAccessCount());
        randomAccessFile.writeUTF(video.getUploader().getName());
        randomAccessFile.writeUTF(tagsToString(video.getTags()));
        randomAccessFile.writeLong(video.getUploadDate().getTime());
    }

    private static <T extends Audio> void saveAudio(RandomAccessFile randomAccessFile, long seekPos, AudioImpl audio, Class<T> clazz) throws IOException {
        randomAccessFile.seek(seekPos);
        randomAccessFile.writeUTF(clazz.getSimpleName());
        randomAccessFile.writeInt(audio.getSamplingRate());
        randomAccessFile.writeUTF(audio.getEncoding());
        randomAccessFile.writeLong(audio.getBitrate());
        randomAccessFile.writeLong(audio.getLength());
        randomAccessFile.writeDouble(audio.getSize().doubleValue());
        randomAccessFile.writeUTF(audio.getAddress());
        randomAccessFile.writeLong(audio.getAccessCount());
        randomAccessFile.writeUTF(audio.getUploader().getName());
        randomAccessFile.writeUTF(tagsToString(audio.getTags()));
        randomAccessFile.writeLong(audio.getUploadDate().getTime());
    }
}