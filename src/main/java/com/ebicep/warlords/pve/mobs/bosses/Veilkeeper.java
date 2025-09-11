package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.BindingChainsAbility;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.PairedSequenceAbility;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.ShatteringChainsAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.List;

public class Veilkeeper extends AbstractMob implements BossMob {

    private Location mapCenter;
    //private BindingChainsAbility bindingChainsAbility;
    private PairedSequenceAbility pairedSequenceAbility;
    private ShatteringChainsAbility shatterChainsAbility;

    public Veilkeeper(Location spawnLocation) {
        super(spawnLocation,
                "Veilkeeper",
                300000,
                0.05f,
                50,
                1200,
                2500
        );
    }

    public Veilkeeper(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 11, 62.5);

        pairedSequenceAbility = new PairedSequenceAbility(
                warlordsNPC,
                () -> mapCenter,
                List.of(
                        PairedSequenceAbility.SanctumColor.BLUE,
                        PairedSequenceAbility.SanctumColor.GREEN,
                        PairedSequenceAbility.SanctumColor.RED,
                        PairedSequenceAbility.SanctumColor.YELLOW
                ),
                4,
                35,
                5,
                200,
                200,
                100,
                200,
                2,
                5000,
                25000,
                amt -> warlordsNPC.addInstance(InstanceBuilder.healing().value(25000).source(warlordsNPC).cause("Sequence Fail")
        ));
        shatterChainsAbility = new ShatteringChainsAbility(warlordsNPC, () -> mapCenter.clone().add(0, 1, 0), 18, 35, 100, 200, 5, 1.5, 2, 2, 2000);

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    boolean triggered = false;

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (!triggered) {
            shatterChainsAbility.start(warlordsNPC.getGame());
            triggered = true;
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {

    }

    @Override
    public Component getDescription() {
        return Component.text("The Commandment of Unrivaled Chains", NamedTextColor.RED);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.BLACK;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.VEILKEEPER;
    }
}
