package de.jvstvshd.localstream.common.network.packets.elements;

import de.jvstvshd.localstream.common.network.handling.PacketServerHandler;
import de.jvstvshd.localstream.common.network.packets.PacketBuffer;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;
import de.jvstvshd.localstream.common.title.TitleAction;
import de.jvstvshd.localstream.common.title.TitleMetadata;

import java.util.Optional;

/**
 * A packet for sending actions for the control of the playing of titles.
 *
 * @see TitlePacket
 * @see TitlePlayAction
 * @deprecated Use {@link TitlePacket} with its actions (see inner enum).
 */
@Deprecated(forRemoval = true)
public class TitlePlayPacket extends ServerHandlingPacket {

    private TitleMetadata metadata;
    private TitlePlayAction action;

    public TitlePlayPacket(PacketPriority priority, TitleMetadata metadata, TitlePlayAction action) {
        super(priority);
        this.metadata = metadata;
        this.action = action;
    }

    public TitlePlayPacket(PacketPriority priority) {
        super(priority);
    }

    @Override
    public void read(PacketBuffer buffer) {
        int i = buffer.readInt();
        System.out.println("i = " + i);
        Optional<TitlePlayAction> opt = TitlePlayAction.getTitlePlayAction(i);
        System.out.println(opt.toString());
        if (opt.isEmpty()) {
            buffer.deadRead();
            throw new IllegalStateException("Unexpected value of action int " + i);
        }
        action = opt.get();
        metadata = TitleMetadata
                .builder()
                .setUuid(buffer.readUniqueId())
                .build();

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeInt(action.getIndex());
        buffer.writeUniqueId(metadata.getUuid());
    }

    @Override
    public void process(PacketServerHandler handler) {
        handler.handleTitlePlay(this);
    }

    public TitleMetadata getMetadata() {
        return metadata;
    }

    public TitlePlayAction getAction() {
        return action;
    }

    /**
     * Various actions for controlling playing of titles.
     *
     * @see TitlePacket.TitleAction
     * @see de.jvstvshd.localstream.common.title.TitleAction
     * @deprecated Use {@link de.jvstvshd.localstream.common.title.TitleAction} or
     * {@link TitlePacket.TitleAction} instead.
     */
    @Deprecated(forRemoval = true)
    public enum TitlePlayAction {
        PAUSE(0),
        RESUME(1),
        STOP(2),
        ACQUIRE_DATA(3);
        private final int index;

        TitlePlayAction(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static Optional<TitlePlayAction> getTitlePlayAction(int index) {
            for (TitlePlayAction value : values()) {
                System.out.println(value.name() + ": " + value.getIndex());
                if (value.getIndex() == index)
                    return Optional.of(value);
            }

            return Optional.empty();
        }

        public TitleAction convert() {
            return switch (this) {
                case PAUSE -> TitleAction.PAUSE;
                case RESUME -> TitleAction.RESUME;
                case STOP -> TitleAction.STOP;
                case ACQUIRE_DATA -> TitleAction.ACQUIRE_DATA;
            };
        }

    }
}

