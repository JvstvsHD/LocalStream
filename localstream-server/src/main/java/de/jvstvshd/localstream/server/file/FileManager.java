package de.jvstvshd.localstream.server.file;

import de.jvstvshd.localstream.common.title.TitleMetadata;
import de.jvstvshd.localstream.server.LocalStreamServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    public static final File parentDir = new File("D:\\upload\\");
    private static final Logger logger = LogManager.getLogger();
    private final LocalStreamServer lss;

    private final Map<FileUpload, Boolean> fileUploads;

    public FileManager(LocalStreamServer lss) {
        this.lss = lss;
        this.fileUploads = new HashMap<>();
    }

    public FileUpload startFileUpload(String fileName, byte[] firstData, TitleMetadata metadata) throws IOException {
        logger.debug("Starting file upload for file " + metadata.getName() + " with id " + metadata.getUuid());
        lss.getScheduler().runAsync(() -> {
            try (Connection connection = lss.getDataSourceManager().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO titles (name, id, length, size, interpret, title_name) VALUES (?, ?, ?, ?, ?, ?)");
                 PreparedStatement statement1 = connection.prepareStatement("DELETE FROM titles WHERE name='" + metadata.getName() + "'")) {
                int updates = statement1.executeUpdate();
                if (updates != 0)
                    logger.debug(updates + " entries with existing titles were deleted.");
                statement.setString(1, fileName);
                statement.setString(2, metadata.getUuid().toString().toLowerCase());
                statement.setLong(3, metadata.getLength());
                statement.setLong(4, metadata.getSize());
                statement.setString(5, metadata.getInterpret());
                statement.setString(6, metadata.getTitleName());
                statement.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        File file = new File(parentDir, fileName);
        if (file.delete())
            logger.debug("File for " + fileName + " existed, deleted file.");
        if (file.getParentFile().mkdirs() && (file.createNewFile())) {
           logger.debug("The file and/or the directory of it's parents were successfully created.");
        }
        FileUpload fileUpload = new FileUpload(file, metadata);
        fileUpload.queue(firstData);
        this.fileUploads.put(fileUpload, false);
        return fileUpload;
    }

    public FileUpload getFileUpload(String fileName, TitleMetadata metadata) {
        for (FileUpload fileUpload : fileUploads.keySet()) {
            if (fileUpload.getMetadata().getUuid().equals(metadata.getUuid()))
                return fileUpload;
        }
        try {
            return startFileUpload(fileName, new byte[0], metadata);
        } catch (IOException e) {
            throw new RuntimeException("Could not start file upload!", e);
        }
    }
}
