import io.netty.util.internal.ThreadLocalRandom;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ByteAudioTest {

    @Test
    public void test() throws Exception {
        for (int i = 0; i < 5; i++) {
            System.out.println(ThreadLocalRandom.current().nextInt(1, 13));
        }

    }
}
