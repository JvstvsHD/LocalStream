package de.jvstvshd.localstream.common.utils;

import org.tritonus.share.StringHashedSet;
import org.tritonus.share.sampled.Encodings;

import javax.sound.sampled.AudioFormat;
import java.lang.reflect.Field;
import java.util.Optional;

public class AudioUtils {

    public static AudioFormat getFormat(AudioFormat encoded) {
        return encoded;
    }

    public static Optional<AudioFormat.Encoding> getEncoding(String name) {
        StringHashedSet<AudioFormat.Encoding> encodings = null;
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
}
