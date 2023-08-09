package com.ebicep.warlords.pve.items.types;

import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;
import java.util.List;

public interface BonusLore {

    @Nullable
    List<Component> getBonusLore();

}
