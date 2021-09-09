package de.jvstvshd.localstream.common.network.packets.elements;


import de.jvstvshd.localstream.common.network.handling.PacketServerHandler;
import de.jvstvshd.localstream.common.network.packets.PacketBuffer;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;
import de.jvstvshd.localstream.common.title.TitleMetadata;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class TitlePacket extends ServerHandlingPacket {

    private TitleAction action;
    private UUID requestId;
    private TitleMetadata metadata;

    public TitlePacket(PacketPriority priority, TitleAction action, TitleMetadata metadata, UUID requestId) {
        super(priority);
        this.action = action;
        this.metadata = metadata;
        this.requestId = requestId;
    }

    public TitlePacket(PacketPriority priority) {
        super(priority);
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.action = TitleAction.getTitleAction(buffer.readInt());
        this.requestId = buffer.readUniqueId();
        if (action == TitleAction.CHECK) {
            this.metadata = TitleMetadata.builder().setUuid(buffer.readUniqueId()).setName(buffer.readString()).build();
            return;
        }
        if (action == TitleAction.ADD_START) {
            metadata = TitleMetadata.builder()
                    .setUuid(buffer.readUniqueId())
                    .setInterpret(buffer.readString())
                    .setTitleName(buffer.readString())
                    .setName(buffer.readString())
                    .setSize(buffer.readLong())
                    .setLength(buffer.readLong())
                    .build();
            return;
        }
        if (action == TitleAction.ADD_END) {
            metadata = TitleMetadata
                    .builder()
                    .setName(buffer.readString())
                    .setUuid(buffer.readUniqueId())
                    .build();
            return;
        }
        if (action == TitleAction.PLAY) {
            metadata = TitleMetadata
                    .builder()
                    .setUuid(buffer.readUniqueId())
                    .build();
            return;
        }
        if (action == TitleAction.NOTHING) {
            System.err.println("Error while reading title transferring: Nothing action was transmitted. Reading " + buffer.readableBytes() + " to avoid complications.");
            buffer.deadRead();
            return;
        }
        if (action == TitleAction.REMOVE) {
            //do nothing
            System.out.println("Not supported. Reading " + buffer.readableBytes() + " to avoid complications.");
            buffer.deadRead();
        }
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeInt(action.getAction());
        buffer.writeUniqueId(requestId);
        if (action == TitleAction.CHECK) {
            buffer.writeUniqueId(metadata.getUuid()).writeString(metadata.getName());
            return;
        }
        if (action == TitleAction.ADD_START) {
            buffer.writeUniqueId(metadata.getUuid())
                    .writeString(metadata.getInterpret())
                    .writeString(metadata.getTitleName())
                    .writeString(metadata.getName())
                    .writeLong(metadata.getSize())
                    .writeLong(metadata.getLength());
            return;
        }
        if (action == TitleAction.ADD_END) {
            buffer.writeString(metadata.getName());
            buffer.writeUniqueId(metadata.getUuid());
            return;
        }
        if (action == TitleAction.PLAY) {
            buffer.writeUniqueId(metadata.getUuid());
            return;
        }
        if (action == TitleAction.NOTHING)
            return;

        if (action == TitleAction.REMOVE) {
            //removal logic
        }

    }

    @Override
    public void process(PacketServerHandler handler) {
        handler.handleTitle(this);
    }

    public UUID getRequestId() {
        return requestId;
    }

    public TitleMetadata getMetadata() {
        return metadata;
    }

    public TitleAction getAction() {
        return action;
    }

    @Deprecated(forRemoval = true)
    public enum TitleAction {
        /**
         * Starts the adding process.<br>
         * Possible response codes:<br>
         * - 0 if the title does not exist<br>
         * - 1 if the title exists.<br>
         */
        ADD_START(de.jvstvshd.localstream.common.title.TitleAction.ADD_START),
        /**
         * Ends the adding process.<br>
         */
        ADD_END(de.jvstvshd.localstream.common.title.TitleAction.ADD_END),
        /**
         * Adds a title.<br>
         * Possible response codes:<br>
         * - 0 for success<br>
         * - 1 for failure<br>
         */
        REMOVE(de.jvstvshd.localstream.common.title.TitleAction.REMOVE),
        /**
         * Checks for the existence of a title.<br>
         * Possible response codes:<br>
         * - 0 if the title exists.<br>
         * - 1 if not.
         */
        CHECK(de.jvstvshd.localstream.common.title.TitleAction.CHECK),
        /**
         * Plays a title.<br>
         * Needed data: {@link UUID}.<br>
         * Possible response codes:<br>
         * - 0 if the title cannot be played<br>
         * - 1 if the title can be played
         */
        PLAY(de.jvstvshd.localstream.common.title.TitleAction.PLAY),
        /**
         * Default value to avoid null.<br>
         * Response code: -1.<br>
         * <b>SHOULD NOT BE USED IF IT IS NOT A REPLACEMENT FOR <code>null</code>!</b>
         */
        PAUSE(de.jvstvshd.localstream.common.title.TitleAction.PAUSE),
        RESUME(de.jvstvshd.localstream.common.title.TitleAction.RESUME),
        STOP(de.jvstvshd.localstream.common.title.TitleAction.STOP),
        ACQUIRE_DATA(de.jvstvshd.localstream.common.title.TitleAction.ACQUIRE_DATA),
        NOTHING(de.jvstvshd.localstream.common.title.TitleAction.NOTHING);

        private final de.jvstvshd.localstream.common.title.TitleAction action;

        TitleAction(de.jvstvshd.localstream.common.title.TitleAction action) {
            this.action = action;
        }

        public int getAction() {
            return action.getAction();
        }

        /**
         * Retrieves a {@link TitleAction} as optional with the given <code>action</code>
         *
         * @param action action as {@link Integer}
         * @return an {@link Optional} of the action matching with <code>action</code> or {@link Optional#empty()} if nothing matches.
         * @see #getTitleAction(int)
         */
        public static Optional<TitleAction> getTitleActionOptional(int action) {
            return Arrays.stream(values()).filter(titleAction -> titleAction.getAction() == action).findFirst();
        }

        /**
         * Retrieves a {@link TitleAction} with the given <code>action</code>.
         *
         * @param action action as {@link Integer}
         * @return the matching action or {@link #NOTHING} if {@link Optional#isEmpty()} of {@link #getTitleActionOptional(int)} returned true.
         */
        public static TitleAction getTitleAction(int action) {
            return getTitleActionOptional(action).orElse(NOTHING);
        }

        public de.jvstvshd.localstream.common.title.TitleAction getTitleAction() {
            return action;
        }

        public static Optional<TitleAction> fromTitleAction(de.jvstvshd.localstream.common.title.TitleAction action) {
            return Arrays.stream(values()).filter(titleAction -> titleAction.action == action).findAny();
        }

        /**
         * Converts {@link #PAUSE}, {@link #RESUME} or {@link #STOP} in its equivalent from {@link TitlePlayPacket.TitlePlayAction}
         * @return the converted {@link TitlePlayPacket.TitlePlayAction}
         * @throws IllegalStateException if this is not one of the above noted states.
         */
        @Deprecated(forRemoval = true)
        public TitlePlayPacket.TitlePlayAction convert() {
            if (this == PAUSE) return TitlePlayPacket.TitlePlayAction.PAUSE;
            if (this == RESUME) return TitlePlayPacket.TitlePlayAction.RESUME;
            if (this == STOP) return TitlePlayPacket.TitlePlayAction.STOP;
            throw new IllegalStateException("Cannot convert " + name() + " to TitlePlayPacket$TitlePlayAction.");
        }
    }
}
