package de.jvstvshd.localstream.common.title;

import com.google.common.base.Objects;
import org.apache.commons.lang3.Validate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Contains metadata for titles (the rate, the name of the file/de.jvstvshd.localstream.scheduling.title, the length in seconds, the size in bytes and the internal id).
 * <b>Important note: This class contains several deprecated features that are marked with <code>forRemoval = true.</code> These features will be removed and should <i>not</i> be used.</b>
 *
 * @see Builder
 * @see #builder()
 */
public class TitleMetadata implements Cloneable {

    private final String name;
    private final long length;
    private final long size;
    private final UUID uuid;
    private final String interpret;
    private final String titleName;

    public static final String DEFAULT_INTERPRET = "Unbekannter Interpret";
    public static final String DEFAULT_TITLE_NAME = "Unbekannter Titelname";

    public TitleMetadata(String name, long length, long size, UUID uuid, String interpret, String titleName) {
        this.name = name;
        this.length = length;
        this.size = size;
        this.uuid = uuid;
        this.interpret = interpret;
        this.titleName = titleName;
        checkArguments();
    }


    private void checkArguments() {
        if (this.uuid == null)
            throw new IllegalArgumentException("UUID cannot be null.");
    }

    public String getName() {
        return name;
    }

    public long getLength() {
        return length;
    }

    public long getSize() {
        return size;
    }

    public UUID getUuid() {
        if (uuid == null)
            throw new IllegalArgumentException("UUID cannot be null");
        return uuid;
    }

    /**
     * Creates a new {@link Builder} for easily creating {@link TitleMetadata}s.
     *
     * @return a new instance of the Builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getInterpret() {
        return interpret;
    }

    public String getTitleName() {
        return titleName;
    }

    @Override
    public String toString() {
        return "TitleMetadata{" +
                "name='" + name + '\'' +
                ", length=" + length +
                ", size=" + size +
                ", uuid=" + uuid +
                ", interpret='" + interpret + '\'' +
                ", titleName='" + titleName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TitleMetadata that = (TitleMetadata) o;
        return length == that.length && size == that.size && Objects.equal(name, that.name) && Objects.equal(uuid, that.uuid) && Objects.equal(interpret, that.interpret) && Objects.equal(titleName, that.titleName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, length, size, uuid, interpret, titleName);
    }

    /**
     * @param name      Name of the de.jvstvshd.localstream.scheduling.title.
     * @param length    Length in seconds.
     * @param size      Size in bytes.
     * @param interpret Interpret of the title.
     * @param titleName The name of this title without any file extension or interpret.
     * @return The created de.jvstvshd.localstream.scheduling.title metadata.
     */
    public static TitleMetadata create(final String name, final long length, final long size, final String interpret, final String titleName) {
        return builder()
                .setName(name)
                .setLength(length)
                .setSize(size)
                .setUuid(UUID.randomUUID())
                .setInterpret(interpret)
                .setTitleName(titleName)
                .build();
    }

    /**
     * @param name      Name of the de.jvstvshd.localstream.scheduling.title.
     * @param length    Length in seconds.
     * @param size      Size in bytes.
     * @param uuid      title id
     * @param interpret Interpret of the title.
     * @param titleName The name of this title without any file extension or interpret.
     * @return The created de.jvstvshd.localstream.scheduling.title metadata.
     */
    public static TitleMetadata create(final String name, final long length, final long size, final String interpret, final String titleName, final UUID uuid) {
        return builder()
                .setName(name)
                .setLength(length)
                .setSize(size)
                .setUuid(uuid)
                .setInterpret(interpret)
                .setTitleName(titleName)
                .build();
    }

    public static CompletableFuture<TitleMetadata> resolveAsync(UUID uuid, Connection connection) {
        return CompletableFuture.supplyAsync(() -> resolve(uuid, connection));
    }

    public static TitleMetadata resolve(UUID uuid, Connection connection) {
        Validate.notNull(uuid, "Cannot resolve title metadata from empty uuid.");
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM titles WHERE id = ?")) {
            statement.setString(1, uuid.toString().toLowerCase());
            ResultSet rs = statement.executeQuery();
            if (!rs.next())
                return builder().setUuid(uuid).build();
            String name = rs.getString(1);
            long length = rs.getLong(3);
            long size = rs.getLong(4);
            String interpret = rs.getString(5);
            String titleName = rs.getString(6);
            return builder()
                    .setUuid(uuid)
                    .setName(name)
                    .setLength(length)
                    .setSize(size)
                    .setInterpret(interpret)
                    .setTitleName(titleName)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return builder().setUuid(uuid).build();
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * A simple builder for the {@link TitleMetadata}.
     */
    public static final class Builder {

        private String name = "Unknown Title, name was not set.";
        private long length = -1;
        private long size = -1;
        private UUID uuid = null;
        private String interpret;
        private String titleName;

        private Builder() {

        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLength(long length) {
            this.length = length;
            return this;
        }

        public Builder setSize(long size) {
            this.size = size;
            return this;
        }

        public Builder setUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setInterpret(String interpret) {
            this.interpret = interpret;
            return this;
        }

        public Builder setTitleName(String titleName) {
            this.titleName = titleName;
            return this;
        }

        public TitleMetadata build() {
            return new TitleMetadata(name, length, size, uuid, interpret, titleName);
        }

        @Override
        public String toString() {
            return "partially built " + build();
        }
    }
}
