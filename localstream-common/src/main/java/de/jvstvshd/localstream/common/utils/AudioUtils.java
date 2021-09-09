package de.jvstvshd.localstream.common.utils;


import org.tritonus.share.StringHashedSet;
import org.tritonus.share.sampled.Encodings;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

public class AudioUtils {

    public static AudioFormat getFormat(AudioFormat encoded) {
        return encoded;
    }

    @SuppressWarnings("unchecked")
    public static Optional<AudioFormat.Encoding> getEncoding(String name) {
        StringHashedSet<AudioFormat.Encoding> encodings;
        try {
            Field field = Encodings.class.getDeclaredField("encodings");
            field.setAccessible(true);
            encodings = (StringHashedSet<AudioFormat.Encoding>) field.get(Encodings.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(encodings.get(name));
        //return Arrays.stream(Encodings.getEncodings()).filter(encoding -> encoding.toString().equalsIgnoreCase(name)).findFirst();
    }

    public static long getDurationMp3(File file) throws IOException {
        try {
            return (long) AudioSystem.getAudioFileFormat(file).properties().get("duration");
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
