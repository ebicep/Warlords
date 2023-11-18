package com.ebicep.warlords.pve.gameevents.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.player.general.Specializations;

public interface Codex {

    String getName();

    Specializations getSpec();

    Ability[] getAbilities();

}
