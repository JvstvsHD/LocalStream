package de.jvstvshd.localstream.scheduling;

import java.util.UUID;

public interface ScheduleTask {

    void cancel();

    UUID getUuid();
}
