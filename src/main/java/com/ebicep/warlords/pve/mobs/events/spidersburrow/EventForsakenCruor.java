package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilities.WoundingStrikeBerserker;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.flags.Spider;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class EventForsakenCruor extends AbstractMob implements BossMinionMob, Spider {


    public EventForsakenCruor(Location spawnLocation) {
        super(
                spawnLocation,
                "Forsaken Respite",
                2700,
                0.45f,
                0,
                300,
                450,
                new WoundAll()
        );
    }

    public EventForsakenCruor(
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
                maxMeleeDamage,
                new WoundAll()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_MITHRA_FORSAKEN_CRUOR;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        int currentWave = option.getWaveCounter();
        if (currentWave % 5 == 0 && currentWave > 5) {
            float additionalHealthMultiplier = 1 + .15f * (currentWave / 5f - 1);
            warlordsNPC.setMaxHealthAndHeal(warlordsNPC.getMaxBaseHealth() * additionalHealthMultiplier);
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        // Applies wounding to enemies for 3s.

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    private static class WoundAll extends AbstractPveAbility {

        public WoundAll() {
            super("Wound ALl", 7, 50);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {


            PlayerFilterGeneric.playingGameWarlordsPlayers(pveOption.getGame())
                               .enemiesOf(wp)
                               .forEach(receiver -> {
                                   receiver.getCooldownManager().removePreviousWounding();
                                   receiver.getCooldownManager().addCooldown(new RegularCooldown<>(
                                           name,
                                           "WND",
                                           WoundingStrikeBerserker.class,
                                           new WoundingStrikeBerserker(),
                                           wp,
                                           CooldownTypes.DEBUFF,
                                           cooldownManager -> {
                                           },
                                           cooldownManager -> {
                                               if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("WND")
                                                                                                               .stream()
                                                                                                               .count() == 1) {
                                                   receiver.sendMessage(Component.text("You are no longer ", NamedTextColor.GRAY)
                                                                                 .append(Component.text("wounded"))
                                                                                 .append(Component.text(".")));
                                               }
                                           },
                                           3 * 20
                                   ) {
                                       @Override
                                       public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                                           return currentHealValue * .5f;
                                       }
                                   });
                               });
            return true;
        }

    }
}
