package com.ebicep.warlords.honorifics;

import org.bson.Document;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HonorificProfile {

    private Set<Honorific> unlockedHonorifics = new HashSet<>();
    private Set<HonorificColor> unlockedColors = new HashSet<>();
    private Set<HonorificFont> unlockedFonts = new HashSet<>();

    @Nullable
    private Honorific equippedHonorific;
    private HonorificColor selectedColor = HonorificColor.AQUA;
    private HonorificFont selectedFont = HonorificFont.STANDARD;

    private long itemRerolls;
    private long starPiecesSynth