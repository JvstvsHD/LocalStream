package de.jvstvshd.localstream.client.desktop.media;

import de.jvstvshd.localstream.client.desktop.util.activity.NetworkActivity;
import de.jvstvshd.localstream.client.desktop.util.activity.NetworkActivities;
import de.jvstvshd.localstream.network.NetworkManager;
import de.jvstvshd.localstream.network.packets.PacketPriority;
import de.jvstvshd.localstream.network.packets.TitleDataUploadPacket;
import de.jvstvshd.localstream.network.packets.TitlePacket;
import de.jvstvshd.localstream.title.TitleMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.AudioFileIO;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.NotYetConnectedException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.UUID.randomUUID;

public class MediaUpload implements Comparable<MediaUpload> {

    private final File file;
    private final InputStream is;
    private final NetworkManager manager;
    private boolean shouldCancel;
    private TitleMetadata metadata;
    private static final Logger logger = LogManager.getLogger();
    private final PacketPriority priority;
    private long sentPackets;
    private long maxPackets;
    private final NetworkActivities activities;

    public MediaUpload(final File file, final NetworkManager manager, PacketPriority priority, NetworkActivities activities) throws IOException {
        this.priority = priority;
        this.activities = activities;
        preCheck(file, manager);
        this.file = file;
        this.is = createStream();
        this.manager = manager;
        this.shouldCancel = false;
        sentPackets = -1;
    }

    private AudioInputStream createStream() throws IOException {
        try {
            return AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException e) {
            throw new IOException(e);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void preCheck(File file, NetworkManager manager) throws IOException {
        Throwable subException = null;
        String mainCause = null;
        if (file == null || !file.exists()) {
            mainCause = "Can't process with not-existing file.";
            subException = new FileNotFoundException("The file " + (file == null ? "null" : file.getAbsolutePath()) + " does not exist.");
        }
        if (manager == null || !manager.channelOpen()) {
            mainCause = "Can't process with non-open network manager";
            subException = new NotYetConnectedException();
        }
        if (subException != null || mainCause != null)
            throw new IOException(mainCause, subException);
    }


    public File getFile() {
        return file;
    }

    private long computeLength() {
        try {
            return AudioFileIO.read(file).getAudioHeader().getTrackLength();
        } catch (Exception e) {
            logger.error("Could not read length from audio file " + file.getAbsolutePath(), e);
            return -1;
        }
    }

    public void prepare() {
        String interpret = "Unbekannter Interpret";
        String titleName = "Unbekannter Titelname";
        long length = computeLength();
        long size = file.length();
        UUID uuid = UUID.randomUUID();
        metadata = TitleMetadata
                .builder()
                .setLength(length)
                .setName(file.getName())
                .setSize(size)
                .setUuid(uuid)
                .setInterpret(interpret)
                .setTitleName(titleName)
                .build();
    }

    public synchronized UploadResult performUpload() {
        prepare();
        final UUID activityUuid = randomUUID();
        activities.ensureCreated(activityUuid, NetworkActivity.ActivityType.ADD_TITLE);
        manager.sendPacket(new TitlePacket(priority, TitlePacket.TitleAction.ADD_START, metadata, randomUUID()));
        activities.changeActivityState(activityUuid, NetworkActivity.State.STARTED);
        try {
            int dataLength = 4096;
            maxPackets = (long) ((double) file.length() / dataLength) - 1;
            /*byte[] data = is.readAllBytes();
            byte[] currentData = new byte[dataLength];
            int currentCopied = 0;
            for (byte datum : data) {
                if (shouldCancel)
                    return UploadResult.get(UploadResult.FAIL, new CancellationException("Upload was cancelled."));
                if (currentCopied == currentData.length) {
                    manager.sendPacket(new TitleDataUploadPacket(priority, currentData, metadata.getUuid()));
                    sentPackets++;
                    currentCopied = 0;
                    currentData = new byte[dataLength];
                }
                currentData[currentCopied] = datum;
                currentCopied++;
            }*/
            int readBytes = 0;
            byte[] currentData = new byte[dataLength];
            int number = 0;
            final double timePerPacket = (double) metadata.getLength() / maxPackets;
            System.out.println("timePerPacket * 1000 * 1000 * 10 = " + timePerPacket * 1000 * 1000 * 10);
            while (!shouldCancel) {
                sentPackets++;
                readBytes = is.read(currentData, 0, currentData.length);
                activities.changeActivityProgress(activityUuid, progress());
                if (readBytes == -1)
                    break;
                //System.out.println("i = " + i);

                manager.sendPacket(new TitleDataUploadPacket(priority, currentData, metadata.getUuid()));
                //TimeUnit.NANOSECONDS.sleep(Math.round(timePerPacket * 1000 * 1000 * 10));
                TimeUnit.NANOSECONDS.sleep(1);
                //line.write(b, 0, i);;
                //TimeUnit.NANOSECONDS.sleep(Math.round(timePerPacket * 1000 * 1000 * 10));
                //System.out.println("packetsSent = " + packetsSent);
            }
            //byte[] data = is.readAllBytes();

            /*int currentCopied = 0;

            while ((readBytes = is.read(currentData)) != -1) {
                manager.sendPacket(new TitleDataUploadPacket(priority, currentData, metadata.getUuid()));
                sentPackets++;
            }
           /* for (byte datum : data) {
                if (shouldCancel)
                    return UploadResult.get(UploadResult.FAIL, new CancellationException("Upload was cancelled."));
                if (currentCopied == currentData.length) {
                    manager.sendPacket(new TitleDataUploadPacket(priority, currentData, metadata.getUuid()));
                    sentPackets++;
                    currentCopied = 0;
                    currentData = new byte[dataLength];
                }
                currentData[currentCopied] = datum;
                currentCopied++;
            }*/
            is.close();
        } catch (Exception e) {
            activities.changeActivityState(activityUuid, NetworkActivity.State.COMPUTED_FAIL);
            return UploadResult.get(UploadResult.FAIL, e);
        }
        manager.sendPacket(new TitlePacket(priority, TitlePacket.TitleAction.ADD_END, metadata, UUID.randomUUID()));
        activities.changeActivityState(activityUuid, NetworkActivity.State.COMPUTED_SUCCESS);
        System.out.println(sentPackets + " - " + maxPackets);
        return UploadResult.SUCCESS;
    }

    public void cancel() {
        this.shouldCancel = true;
    }

    @Override
    public int compareTo(MediaUpload o) {
        return priority.compareTo(o.priority);
    }

    protected record UploadResult(boolean success, Object... objects) {

        public static final UploadResult SUCCESS = create(true);
        public static final UploadResult FAIL = create(false);

        private static UploadResult create(boolean success) {
            return new UploadResult(success);
        }

        public static UploadResult get(UploadResult result, Object... params) {
            return new UploadResult(result.success, params);
        }

        public boolean isSuccess() {
            return success;
        }

        public void throwExceptions() {
            for (Object object : objects) {
                if (object instanceof Throwable throwable)
                    throwable.printStackTrace();
            }
        }
    }

    /**
     * Returns how much of the file was already transferred. The progress is calculated by dividing the current sent packets through the max amount of packets (which is only an approx. value)
     *
     * @return the progress of the media upload
     */
    public double progress() {
        return sentPackets / (double) maxPackets;
    }
}
