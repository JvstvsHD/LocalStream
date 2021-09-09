package de.jvstvshd.localstream.common.network.packets.elements;

import de.jvstvshd.localstream.common.network.handling.PacketServerHandler;
import de.jvstvshd.localstream.common.network.packets.PacketBuffer;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;
import de.jvstvshd.localstream.common.title.TitleAction;
import de.jvstvshd.localstream.common.title.TitleMetadata;

import java.util.UUID;

public class TitlePacket extends ServerHandlingPacket {

    private de.jvstvshd.localstream.common.title.TitleAction action;
    private UUID requestId;
    private TitleMetadata metadata;

    public TitlePacket(PacketPriority priority, de.jvstvshd.localstream.common.title.TitleAction action, TitleMetadata metadata, UUID requestId) {
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
        this.action = de.jvstvshd.localstream.common.title.TitleAction.getTitleAction(buffer.readInt());
        this.requestId = buffer.readUniqueId();
        if (action == de.jvstvshd.localstream.common.title.TitleAction.CHECK) {
            this.metadata = TitleMetadata.builder().setUuid(buffer.readUniqueId()).setName(buffer.readString()).build();
            return;
        }
        if (action == de.jvstvshd.localstream.common.title.TitleAction.ADD_START) {
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
}
