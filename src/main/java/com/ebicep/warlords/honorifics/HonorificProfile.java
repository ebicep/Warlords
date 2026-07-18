package com.ebicep.warlords.honorifics;

import org.bson.Document;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HonorificProfile {

    private Set<Honorific> unlockedHonorifics = new HashSet<>();
    private Set<HonorificColor> unlockedColors = new HashSet<>();
