package de.jvstvshd.localstream.common.scheduling;

import java.util.UUID;

public interface ScheduleTask {

    void cancel();

    UUID getUuid();
}
