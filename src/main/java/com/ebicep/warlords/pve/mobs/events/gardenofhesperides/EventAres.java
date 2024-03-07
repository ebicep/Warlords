package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.SpawnMobAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.List;

public class EventAres extends AbstractMob implements BossMob, LesserGod {

    public EventAres(Location spawnLocation) {
        this(spawnLocation, "Ares", 25500, .4f, 20, 680, 740);
    }

    public EventAres(
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
                new SpawnMobAbility(10, Mob.INTERMEDIATE_WARRIOR_BERSERKER, 10) {
                    @Override
                    public AbstractMob createMob(@Nonnull WarlordsEntity wp) {
                        return mobToSpawn.createMob(pveOption.getRandomSpawnLocation((WarlordsEntity) null));
                    }

                    @Override
                    public int getSpawnAmount() {
                        return (int) (pveOption.getGame().warlordsPlayers().count() + 1);
                    }
                },
                new SpawnMobAbility(10, Mob.ADVANCED_WARRIOR_BERSERKER, 10) {
                    @Override
                    public AbstractMob createMob(@Nonnull WarlordsEntity wp) {
                        return mobToSpawn.createMob(pveOption.getRandomSpawnLocation((WarlordsEntity) null));
                    }

                    @Override
                    public int getSpawnAmount() {
                        return (int) (pveOption.getGame().warlordsPlayers().count());
                    }
                }
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_ARES;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        new GameRunnable(option.getGame()) {

            @Override
            public void run() {
                List<Location> spawnLocations = LocationUtils.getCircle(warlordsNPC.getLocation(), 3, option.playerCount());
                for (Location location : spawnLocations) {
                    option.spawnNewMob(Mob.ADVANCED_WARRIOR_BERSERKER.createMob(location));
                }
            }
        }.runTaskLater(40);

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        event.getFlags().add(InstanceFlags.PIERCE);
        receiver.getCooldownManager().removeCooldownByName("Ares Wounding");
        receiver.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Ares Wounding",
                "WND",
                EventAres.class,
                null,
                attacker,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                cooldownManager -> {
                    if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("WND").stream().count() == 1) {
                        receiver.sendMessage(
                                Component.text("You are no longer ", NamedTextColor.GRAY)
                                         .append(Component.text("wounded", NamedTextColor.RED))
                                         .append(Component.text(".", NamedTextColor.GRAY))
                        );
                    }
                },
                60
        ) {
            @Override
            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * .5f;
            }
        });
    }

    @Override
    public Component getDescription() {
        return Component.text("God of War", NamedTextColor.RED);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_RED;
    }
}
