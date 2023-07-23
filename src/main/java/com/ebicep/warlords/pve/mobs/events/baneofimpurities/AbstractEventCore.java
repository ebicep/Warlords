package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.skeleton.AbstractSkeleton;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;

public abstract class AbstractEventCore extends AbstractSkeleton implements BossMob {

    private final int killTime;
    private final RandomCollection<Mobs> summonList;

    public AbstractEventCore(
            Location spawnLocation,
            String name,
            MobTier mobTier,
            EntityEquipment ee,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            int killTime,
            RandomCollection<Mobs> summonList
    ) {
        super(spawnLocation, name, mobTier, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
        this.killTime = killTime;
        this.summonList = summonList;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        warlordsNPC.setStunTicks(Integer.MAX_VALUE);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        int secondsElapsed = ticksElapsed / 20;
        if (ticksElapsed % 20 == 0) {
            if (secondsElapsed < killTime) {
                for (WarlordsEntity we : PlayerFilter
                        .playingGame(getWarlordsNPC().getGame())
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    we.getEntity().showTitle(Title.title(
                            Component.text("", NamedTextColor.RED),
                            Component.text(killTime - secondsElapsed, NamedTextColor.RED),
                            Title.Times.times(Ticks.duration(10), Ticks.duration(35), Ticks.duration(0))
                    ));
                    Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 500, 0.4f);
                }
            } else if (secondsElapsed == killTime) {
                for (WarlordsEntity we : PlayerFilter
                        .playingGame(getWarlordsNPC().getGame())
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    we.getEntity().clearTitle();
                    we.addDamageInstance(warlordsNPC, "Core Explosion", 25000, 25000, 0, 100, EnumSet.of(InstanceFlags.TRUE_DAMAGE));
                }
            }

            option.spawnNewMob(summonList.next().createMob.apply(warlordsNPC.getLocation()));
            if (secondsElapsed % 15 == 0) {
                for (WarlordsEntity we : PlayerFilter
                        .playingGame(getWarlordsNPC().getGame())
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    we.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                    we.addSpeedModifier(warlordsNPC, "CHAOS", -20, 100, "BASE");
                    we.getCooldownManager().addCooldown(new RegularCooldown<>(
                            "Chaos",
                            "CHAOS",
                            AbstractEventCore.class,
                            null,
                            warlordsNPC,
                            CooldownTypes.DEBUFF,
                            cooldownManager -> {
                            },
                            100
                    ));
                }
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

}
