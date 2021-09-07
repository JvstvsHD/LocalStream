import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioTest {

    @Test
    public void test() throws Exception {
        File file = new File("D:\\test.mp3");
        play(file);
        /*FileInputStream is = new FileInputStream(file);
        AudioInputStream in = AudioSystem.getAudioInputStream(file);

        SourceDataLine line = AudioSystem.getSourceDataLine(in.getFormat());
        line.open(in.getFormat());
        line.start();
        int dataLength = 8192;
        boolean shouldCancel = false;
        byte[] data = is.readAllBytes();
        byte[] currentData = new byte[dataLength];
        int currentCopied = 0;
        for (byte datum : data) {
            if (shouldCancel)
                return;
            if (currentCopied == currentData.length) {
                line.write(currentData, 0, currentCopied);
                currentCopied = 0;
                currentData = new byte[dataLength];
            }
            currentData[currentCopied] = datum;
            currentCopied++;
        }
        line.stop();
        line.close();
        is.close();*/
    }

    public void play(File file) throws Exception {
        AudioInputStream din = null;
        Clip clip = AudioSystem.getClip();

        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                    baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
                    false);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            if (line != null) {
                line.open(decodedFormat);
                byte[] data = new byte[4096];
                // Start
                line.start();

                int nBytesRead;
                while ((nBytesRead = din.read(data, 0, data.length)) != -1) {
                    line.write(data, 0, nBytesRead);
                }
                // Stop
                line.drain();
                line.stop();
                line.close();
                din.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (din != null) {
                try {
                    din.close();
                } catch (IOException e) {

                }
            }
        }
    }

}
