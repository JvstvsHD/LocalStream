package de.jvstvshd.localstream.server.file;

import com.google.common.collect.Lists;
import de.jvstvshd.localstream.common.title.TitleMetadata;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.apache.logging.log4j.LogManager.getLogger;

public class FileUpload {

    private final static Logger logger = getLogger(FileUpload.class);
    private State state;
    private final List<Consumer<FileUpload>> consumers;
    private final File file;
    private final FileOutputStream fos;
    private final TitleMetadata metadata;

    public FileUpload(File file, TitleMetadata metadata) throws IOException {
        this.file = file;
        this.metadata = metadata;
        this.state = State.NOT_STARTED;
        this.consumers = Lists.newArrayList();
        this.fos = new FileOutputStream(file);
        consumers.add(fileUpload -> {
            finish();
           logger.debug("finished file upload " + file.getName());
        });
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        if (state == State.COMPLETED)
            for (Consumer<FileUpload> consumer : consumers) {
                consumer.accept(this);
            }
    }

    public void addSuccessListeners(Consumer<FileUpload>... consumer) {
        this.consumers.addAll(Arrays.asList(consumer));
    }

    public synchronized void queue(byte[] data) {
        try {
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            logger.error("Data could not be written to file " + file.getAbsolutePath() + ".", e);
        }
    }

    private void finish() {
        try {
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String getName() {
        return getFile().getName();
    }

    public File getFile() {
        return file;
    }

    public TitleMetadata getMetadata() {
        return metadata;
    }

    public enum State {
        NOT_STARTED("not_started"),
        RUNNING("running"),
        COMPLETED("completed"),
        FAILED("failed"),
        UNKNOWN("unknown");

        private final String name;

        State(String name) {
            this.name = name;
        }

        public boolean isReady() {
            return this == COMPLETED;
        }

        public static State getState(String name) {
            return Arrays.stream(values()).filter(state -> state.name.equalsIgnoreCase(name)).findFirst().orElse(UNKNOWN);
        }
    }
}
