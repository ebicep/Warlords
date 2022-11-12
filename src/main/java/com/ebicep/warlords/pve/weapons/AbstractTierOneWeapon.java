package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public abstract class AbstractTierOneWeapon extends AbstractWeapon {

    @Field("melee_damage")
    protected float meleeDamage;
    @Field("health_bonus")
    protected float healthBonus;

    public AbstractTierOneWeapon() {
    }

    public AbstractTierOneWeapon(UUID uuid) {
        super(uuid);
    }

    public AbstractTierOneWeapon(WarlordsPlayer warlordsPlayer) {
        super(warlordsPlayer);
    }
}
