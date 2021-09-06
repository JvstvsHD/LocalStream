package de.jvstvshd.localstream.utils;

import org.tritonus.share.sampled.Encodings;

import javax.sound.sampled.AudioFormat;
import java.util.Arrays;
import java.util.Optional;

public class AudioUtils {

    public static AudioFormat getFormat(AudioFormat encoded) {
        return encoded;
    }

    public static Optional<AudioFormat.Encoding> getEncoding(String name) {
        return Arrays.stream(Encodings.getEncodings()).filter(encoding -> encoding.toString().equalsIgnoreCase(name)).findFirst();
    }
}
