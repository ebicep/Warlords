package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Xoris extends AbstractMob implements BossMob {

    boolean test = false;

    public Xoris(Location spawnLocation) {
        super(
                spawnLocation,
                "Xoris",
                60000,
                0.2f,
                20,
                2000,
                3000
        );
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (test) {
            Location loc = warlordsNPC.getLocation();
            int counter = 0;
            if (ticksElapsed % 4 == 0) {
                counter++;
                loc.setYaw(counter);
                EffectUtils.playHelixAnimation(loc, 20, Particle.FLAME, 1, 8);
            }
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        test = true;
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.GRAY;
    }

    @Override
    public Mob getMobRegistry() {
        return null;
    }

    @Override
    public Component getDescription() {
        return Component.text("Empress of the Envoy Legion", NamedTextColor.DARK_PURPLE);
    }
}