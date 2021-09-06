package de.jvstvshd.localstream.server.title;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AudioUtil {
    private static final ArrayList<FileMap> cache = new ArrayList<>();

    public static Map<String, Object> getMetadata(File filename) throws Exception {
        FileMap fm = new FileMap(filename, null);
        int index = cache.indexOf(fm);
        if (index >= 0)
            return cache.get(index).getMap();

        AudioFileFormat format = AudioSystem.getAudioFileFormat(filename);

        Map<String, Object> mapa = new HashMap<>(format.properties());


        if (mapa.get("author") == null && filename.getName().contains(" - ")) {
            mapa = new HashMap<>();
            String[] s = filename.getName().split(" - ");
            mapa.put("author", s[0]);
            s[1] = s[1].substring(0, s[1].length() - 4);
            mapa.put("title", s[1]);
        }

        if (mapa.get("author") == null) {
            mapa.put("author", "Desconhecido");
            mapa.put("title", "Desconhecido");
        }

        Object o = format.properties().get("duration");

        if (o == null)
            mapa.put("duration", 0);

        fm.setMap(mapa);
        cache.add(fm);
        int _CACHE_SIZE = 5;
        while (cache.size() > _CACHE_SIZE)
            cache.remove(0);

        return mapa;
    }
}

class FileMap {
    private File file;
    private Map<String, Object> map;

    public FileMap(File file, Map<String, Object> map) {
        this.file = file;
        this.map = map;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.file, ((FileMap) obj).file);
    }

}