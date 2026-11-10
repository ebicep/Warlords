package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.ItemAdditiveCooldown;
import com.ebicep.warlords.pve.newitems.attributes.NewItemCooldown;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class CrownOfThorns extends BaseSet {

    private static final int THORN_DAMAGE_CAP_MULTIPLIER = 2;

    private int thornDamageBoost;

    @Override
    public void init() {
        super.init();
        this.thornDamageBoost = getValue("thornDamageBoost", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "crownOfThorns";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(thornDamageBoost);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            float damageMultiplier = 1 + (thornDamageBoost / 100f);
            new CooldownFilter<>(warlordsPlayer, PermanentCooldown.class)
                    .filterCooldownName("Item")
                    .findAny()
                    .ifPresent(cooldown -> {
                        NewItemCooldown itemCooldown = (NewItemCooldown) cooldown;
                        itemCooldown.multiplyMaxThornsDamage(THORN_DAMAGE_CAP_MULTIPLIER);
                        itemCooldown.multiplyThornsDamage(damageMultiplier);
                    });
            new CooldownFilter<>(warlordsPlayer, PermanentCooldown.class)
                    .filterCooldownName("Item Additive")
                    .findAny()
                    .ifPresent(cooldown -> ((ItemAdditiveCooldown) cooldown).multiplyThornsDamage(damageMultiplier));
        }

    }

}
