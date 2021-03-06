
import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;


public class AudioPlayer {
    private int bufferSize = 4096; // Tamanho de buffer padrÃ£o 4k
    private volatile boolean paused = false;
    private final Object lock = new Object();
    private SourceDataLine line;
    private int secondsFade = 0;
    //private ArrayList<AudioPlayerListener> _listeners = new ArrayList<AudioPlayerListener>();

    public void stop() {
        if (line != null) {
            line.stop();
            line.close();
        }
    }

    public boolean isPaused() {
        return this.paused;
    }


    public void pause() {
        if (!this.isPaused())
            paused = true;
    }

    public void resume() {
        if (this.isPaused()) {
            synchronized (lock) {
                lock.notifyAll();
                paused = false;
            }
        }
    }

    @Test
    public void test() throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        play(new File("D:\\test.mp3"));
    }

    public void play(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
        AudioInputStream encoded = AudioSystem.getAudioInputStream(file);
        AudioFormat encodedFormat = encoded.getFormat();
        AudioFormat decodedFormat = this.getDecodedFormat(encodedFormat);
        Long duration = null;
        AudioInputStream currentDecoded = AudioSystem.getAudioInputStream(decodedFormat, encoded);
        line = AudioSystem.getSourceDataLine(decodedFormat);
        line.open(decodedFormat);
        line.start();
        boolean fezFadeIn = false;
        boolean fezFadeOut = false;
        byte[] b = new byte[this.bufferSize];
        int i = 0;
        Map properties = null;
        try {
            properties = AudioUtil.getMetadata(file);
            duration = (Long) properties.get("duration");
        } catch (Exception ex) {
            duration = 0L;
        }

        duration = duration < 0 ? 0 : duration;

        synchronized (lock) {
            //Parametro que ativa ou nÃ£o o fade de acordo com o tamanho do Ã¡udio
            long paramFade = (secondsFade * 2 + 1) * 1000000;
            //long paramFade = 0;
            //Logger.getLogger(this.getClass().getName()).info("Arquivo: "+file+", DURACAO DO AUDIO: "+duration+", paramfade: "+paramFade);
            while (true) {
                if (secondsFade > 0 && !fezFadeIn && duration >= paramFade) {
                    fezFadeIn = true;
                    fadeInAsync(this.secondsFade);
                }

                if (secondsFade > 0 &&
                        duration > paramFade &&
                        !fezFadeOut &&
                        line.getMicrosecondPosition() >= duration - ((this.secondsFade + 1) * 1000000L)) {
                    //this.fireAboutToFinish();
                    System.out.println("fire about to finish");
                    fadeOutAsync(this.secondsFade);
                    fezFadeOut = true;
                }

                if (paused) {
                    line.stop();
                    lock.wait();
                    line.start();
                }

                i = currentDecoded.read(b, 0, b.length);
                if (i == -1)
                    break;

                line.write(b, 0, i);
            }
        }

        if (!fezFadeOut && line.isOpen()) {
//            this.fireAboutToFinish();
            System.out.println("fire about to finish");
        }

        line.drain();
        line.stop();
        line.close();
        currentDecoded.close();
        encoded.close();
    }

    public synchronized void fadeInAsync(final int seconds) {
        if (line != null && line.isOpen()) {
            Thread t = new Thread(new Fader(true, this, secondsFade));
            t.start();
        }
    }

    public synchronized void fadeOutAsync(final int seconds) {
        if (line != null && line.isOpen()) {
            Thread t = new Thread(new Fader(false, this, secondsFade));
            t.start();
        }
    }

    public void setVolume(double value) {
        if (!line.isOpen())
            return;
        // value is between 0 and 1
        value = (value <= 0.0) ? 0.0001 : ((value > 1.0) ? 1.0 : value);
        try {
            float dB = (float) (Math.log(value) / Math.log(10.0) * 20.0);
            ((FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN)).setValue(dB);
        } catch (Exception ex) {

        }
    }

    public boolean isPlaying() {
        return (line != null && line.isOpen());
    }


    protected AudioFormat getDecodedFormat(AudioFormat format) {
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


    public int getBufferSize() {
        return bufferSize;
    }


    public void setBufferSize(int bufferSize) {
        if (bufferSize <= 0)
            return;
        this.bufferSize = bufferSize;
    }

    /**
     * @return the secondsFade
     */
    public int getSecondsFade() {
        return secondsFade;
    }

    /**
     * @param secondsFade the secondsFade to set
     */
    public void setSecondsFade(int secondsFade) {
        if (secondsFade < 0 || secondsFade > 10)
            throw new IllegalArgumentException("Erro ao configurar cross-fade com valor em segundos: " + secondsFade);
        this.secondsFade = secondsFade;
    }

}


class Fader implements Runnable {
    private boolean fadeIn;
    private int seconds = 0;
    private final AudioPlayer player;
    private float increaseParam;

    public Fader(boolean fadeIn, AudioPlayer player, int secondsToFade) {
        this.fadeIn = fadeIn;
        this.seconds = secondsToFade;
        this.player = player;
        if (fadeIn)
            increaseParam = 0.01F;
        else
            increaseParam = -0.01F;
    }

    @Override
    public void run() {
        try {
            encapsulateRun();
        } catch (Exception ex) {
            if (fadeIn)
                player.setVolume(1.0F);
            else
                player.setVolume(0.0F);
        }
    }

    private void encapsulateRun() throws Exception {
        synchronized (player) {
            float per;
            if (fadeIn) {
                Logger.getLogger(getClass().getName()).info("Fazendo fade in");
                per = 0.0F;
            } else {
                Logger.getLogger(getClass().getName()).info("Fazendo fade out");
                per = 1.0F;
            }
            player.setVolume(per);
            if (fadeIn) {
                while (per < 1.00F) {
                    per = per + increaseParam;
                    player.setVolume(per);
                    Thread.sleep(10 * seconds);
                }
            } else {
                while (per > 0.00F) {
                    per = per + increaseParam;
                    player.setVolume(per);
                    Thread.sleep(10 * seconds);
                }
            }
        }
    }

}