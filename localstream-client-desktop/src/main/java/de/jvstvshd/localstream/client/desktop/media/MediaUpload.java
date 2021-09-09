package de.jvstvshd.localstream.client.desktop.media;

import de.jvstvshd.localstream.client.desktop.util.activity.NetworkActivities;
import de.jvstvshd.localstream.client.desktop.util.activity.NetworkActivity;
import de.jvstvshd.localstream.common.network.NetworkManager;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;
import de.jvstvshd.localstream.common.network.packets.elements.TitleDataUploadPacket;
import de.jvstvshd.localstream.common.network.packets.elements.TitlePacket;
import de.jvstvshd.localstream.common.title.TitleAction;
import de.jvstvshd.localstream.common.title.TitleMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioFormat;
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
    private double maxPackets;
    private final NetworkActivities activities;
    private final UUID activityUuid = randomUUID();

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

    private double computeLength() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            double durationInSeconds = (frames + 0.0) / format.getFrameRate();
            System.out.println("durationInSeconds = " + durationInSeconds);
            return durationInSeconds;
        } catch (Exception e) {
            logger.error("Could not read length from audio file " + file.getAbsolutePath(), e);
            return -1;
        }
    }

    public void prepare() {
        String interpret = "Unbekannter Interpret";
        String titleName = "Unbekannter Titelname";
        long length = (long) computeLength();
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
        activities.ensureCreated(activityUuid, NetworkActivity.ActivityType.ADD_TITLE);
        manager.sendPacket(new TitlePacket(priority, TitleAction.ADD_START, metadata, randomUUID()));
        activities.changeActivityState(activityUuid, NetworkActivity.State.STARTED);
    }

    public synchronized UploadResult performUpload() {
        prepare();
        final int dataLength = 4096;
        maxPackets = ((double) file.length() / dataLength);
        long maxPackets = (long) this.maxPackets + 1;
        long packets = calculatePackets();
        System.out.println("packets = " + packets);
        try {

            int readBytes;
            byte[] currentData = new byte[dataLength];
            while (!shouldCancel) {
                sentPackets++;
                readBytes = is.read(currentData, 0, currentData.length);
                activities.changeActivityProgress(activityUuid, progress());
                if (readBytes == -1)
                    break;
                manager.sendPacket(new TitleDataUploadPacket(priority, currentData, metadata.getUuid(), sentPackets));
                System.out.println("packets = " + packets);
                if (packets == 0) {
                    //Thread.sleep(1);
                    packets = calculatePackets();
                }
                packets--;
                //TimeUnit.NANOSECONDS.sleep(1);
            }
            is.close();
        } catch (Exception e) {
            activities.changeActivityState(activityUuid, NetworkActivity.State.COMPUTED_FAIL);
            return UploadResult.get(UploadResult.FAIL, e);
        }
        manager.sendPacket(new TitlePacket(priority, TitleAction.ADD_END, metadata, UUID.randomUUID()));
        activities.changeActivityState(activityUuid, NetworkActivity.State.COMPUTED_SUCCESS);
        System.out.println(sentPackets + " - " + maxPackets);
        return UploadResult.SUCCESS;
    }

    private long calculatePackets() {
        return Math.round(maxPackets < 10 ? maxPackets : this.maxPackets / 10.0);
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
        return sentPackets / maxPackets;
    }
}
