package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.Enavuris;
import com.ebicep.warlords.pve.mobs.flags.Unstunnable;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;

public class VanishingEnavurite extends AbstractMob implements ChampionMob, Unstunnable {

    @Nullable
    private Enavuris enavuris;

    public VanishingEnavurite(Location spawnLocation) {
        this(
                spawnLocation,
                "Vanishing Enavurite",
                12525,
                0.24f,
                5,
                550,
                775
        );
    }

    public VanishingEnavurite(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                45,
                265
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.VANISHING_ENAVURITE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        toggleInvis(true);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (enavuris == null) {
            option.getMobs()
                  .stream()
                  .filter(mob -> mob instanceof Enavuris)
                  .map(mob -> (Enavuris) mob)
                  .findFirst()
                  .ifPresent(enavuris -> this.enavuris = enavuris);
        } else if (!option.getMobs().contains(enavuris)) {
            enavuris = null;
        }
    }

    @Override
    public void onFinalAttack(WarlordsDamageHealingFinalEvent event) {
        if (event.isCrit() && enavuris != null) {
            enavuris.setTarget(event.getWarlordsEntity());
        }
    }

    @Override
    public void onFinalDamageTaken(WarlordsDamageHealingFinalEvent event) {
        toggleInvis(false);
    }

    private void toggleInvis(boolean on) {
        if (on) {
            warlordsNPC.getMobHologram().setHidden(true);
            warlordsNPC.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 0, false, false, false));
            warlordsNPC.addSpeedModifier(warlordsNPC, "Invis", 10, 999999999);
        } else {
            warlordsNPC.getMobHologram().setHidden(false);
            warlordsNPC.removePotionEffect(PotionEffectType.INVISIBILITY);
            warlordsNPC.getSpeed().removeModifier("Invis");
        }
    }

}
