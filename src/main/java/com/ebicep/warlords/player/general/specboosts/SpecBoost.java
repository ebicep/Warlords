package com.ebicep.warlords.player.general.specboosts;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SpecBoost {

    private static final Map<Specializations, List<SpecBoost>> SPEC_BOOSTS = new HashMap<>();

    public static List<SpecBoost> getSpecBoosts(Specializations specializations) {
        return SPEC_BOOSTS.get(specializations);
    }

    static {

    }

    protected String getName() {
        return "TODO";
    }

//    protected Component getName() {
//        return Component.text("TODO");
//    }

    public abstract TextComponent getDescription();

    public abstract Boost create();

    public interface Boost {

        void apply(WarlordsPlayer warlordsPlayer);

        void unapply(WarlordsPlayer warlordsPlayer);

    }

}
