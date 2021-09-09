package de.jvstvshd.localstream.server.title;

import de.jvstvshd.localstream.common.network.NetworkManager;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;
import de.jvstvshd.localstream.common.network.packets.elements.StartPlayPacket;
import de.jvstvshd.localstream.common.network.packets.elements.TitleDataPacket;
import de.jvstvshd.localstream.common.network.util.NetworkTask;
import de.jvstvshd.localstream.common.scheduling.Scheduler;
import de.jvstvshd.localstream.common.title.TitleMetadata;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class TitlePlayer implements NetworkTask {

    private final static Logger logger = LogManager.getLogger();
    private final File file;
    private final NetworkManager manager;
    private boolean shouldPause;
    private boolean stop;
    private final TitleMetadata metadata;
    private final TitleManager titleManager;
    private long byteCount;
    private final Scheduler scheduler;
    private boolean next = false;

    public TitlePlayer(File file, NetworkManager manager, TitleMetadata metadata, TitleManager titleManager, Scheduler scheduler) throws IOException {
        this.file = file;
        this.manager = manager;
        this.metadata = metadata;
        this.titleManager = titleManager;
        this.scheduler = scheduler;
        this.shouldPause = false;
        this.stop = false;
    }

    public void play() throws Exception {
        play(0, true);
    }

    public void play(long offset, boolean firstStart) {
        scheduler.runAsync(() -> {
            try {
                play0(offset, firstStart);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void play0(long offset, boolean firstStart) throws Exception {
        //SourceDataLine line;
        long number = 0;
        AudioInputStream encoded = AudioSystem.getAudioInputStream(file);
        AudioFormat encodedFormat = encoded.getFormat();
        AudioFormat decodedFormat = decodeFormat(encodedFormat, FilenameUtils.getExtension(file.getName()));
        AudioInputStream currentDecoded = AudioSystem.getAudioInputStream(decodedFormat, encoded);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
        int bufferSize = 4096;
        double maxPackets = (double) file.length() / (double) bufferSize;
        final double timePerPacket = (double) metadata.getLength() / maxPackets;
        //DEBUG AREA: START
        System.out.println("file.length() = " + file.length());
        System.out.println("bufferSize = " + bufferSize);

        System.out.println("maxPackets = " + maxPackets);
        System.out.println("Lange: " + metadata.getLength() + "s");
        System.out.println("timePerPacket = " + timePerPacket);
        //DEBUG AREA: END
        if (firstStart) {
            manager.sendPacket(new StartPlayPacket(PacketPriority.HIGHEST, decodedFormat, (long) maxPackets + 1, metadata));
        }
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(decodedFormat);
        line.start();
        long packetsSent = 0;
        byte[] b = new byte[bufferSize];
        int i;
        System.out.println("currentDecoded.available() = " + currentDecoded.available());
        long skipped = currentDecoded.skip(offset);
        System.out.println("skipped = " + skipped);
        byteCount = skipped;
        logger.debug("Skipped " + skipped + " bytes, " + (file.length() - skipped) + " available.");
        while (!shouldPause && !stop) {
            packetsSent++;
            i = currentDecoded.read(b, 0, b.length);
            if (i == -1)
                break;
            //System.out.println("i = " + i);
            byteCount += i;
            System.out.println("byteCount = " + byteCount);
            manager.sendPacket(new TitleDataPacket(PacketPriority.HIGH, b, number++));
            //line.write(b, 0, i);;
            //TimeUnit.NANOSECONDS.sleep(Math.round(timePerPacket * 1000 * 1000 * 10));
            while (!next) {
                Thread.sleep(0, 1);
            }
            next = false;
            //System.out.println("packetsSent = " + packetsSent);
        }
        System.out.println("byteCount at stop = " + byteCount);
        shouldPause = false;
        if (stop) {
            manager.sendPacket(new TitleDataPacket(PacketPriority.NORMAL, new byte[0], -1));
            titleManager.unregisterPlayer(metadata.getUuid());
        }
        line.drain();
        line.stop();
        line.close();
        currentDecoded.close();
        encoded.close();
    }

    private AudioFormat decodeFormat(AudioFormat format, String extension) throws UnsupportedAudioFileException {
        if (extension.equalsIgnoreCase("ogg"))
            throw new UnsupportedAudioFileException("ogg vorbis is not supported at the moment");
        if (extension.equalsIgnoreCase("wav"))
            return format;
        if (!extension.equalsIgnoreCase("mp3"))
            throw new UnsupportedAudioFileException("Unsupported audio file type: " + extension);
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,  // Encoding to use
                format.getSampleRate(),           // sample rate (same as base format)
                16,               // sample size in bits (thx to Javazoom)
                format.getChannels(),             // # of Channels
                format.getChannels() * 2,           // Frame Size
                format.getSampleRate(),           // Frame Rate
                false                 // Big Endian
        );
    }

    public void pause() {
        System.out.println("pasuning...");
        this.shouldPause = true;
    }

    public void resume(long skipBytes) {
        try {
            this.shouldPause = false;
            System.out.println("byteCount = " + byteCount);
            play(skipBytes, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void shutdown() {
        pause();
        stop();
    }

    public void stop() {
        System.out.println("stopping...");
        stop = true;
    }

    public void next() {
        System.out.println("next");
        next = true;
    }
}
