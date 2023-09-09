package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.SpawnMobAbility;
import com.ebicep.warlords.pve.mobs.slime.AbstractSlime;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class Chessking extends AbstractSlime implements BossMob {

    public Chessking(Location spawnLocation) {
        super(spawnLocation,
                "Chessking",
                100000,
                0.3f,
                30,
                0,
                0,
                new Belch(),
                new SpawnMobAbility(
                        20,
                        Mob.SLIME_GUARD
                ) {
                    @Override
                    public int getSpawnAmount() {
                        return (int) pveOption.getGame().warlordsPlayers().count();
                    }
                },
                new SpawnMobAbility(
                        60,
                        Mob.SLIMY_CHESS
                ) {
                    @Override
                    public int getSpawnAmount() {
                        return (int) pveOption.getGame().warlordsPlayers().count();
                    }
                }
        );
    }

    public Chessking(
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
                new Belch(),
                new SpawnMobAbility(
                        20,
                        Mob.SLIME_GUARD
                ) {
                    @Override
                    public int getSpawnAmount() {
                        return (int) pveOption.getGame().warlordsPlayers().count();
                    }
                },
                new SpawnMobAbility(
                        60,
                        Mob.SLIMY_CHESS
                ) {
                    @Override
                    public int getSpawnAmount() {
                        return (int) pveOption.getGame().warlordsPlayers().count();
                    }
                }
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.CHESSKING;
    }

    @Override
    public Component getDescription() {
        return Component.text("Goblin from the local basement", NamedTextColor.GRAY);
    }

    @Override
    public NamedTextColor getColor() {
        return NamedTextColor.GREEN;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        this.entity.get().setSize(19, true);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (Utils.isProjectile(event.getAbility())) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ARROW_HIT, 2, 0.1f);
            warlordsNPC.addHealingInstance(warlordsNPC, "Blob Heal", 500, 500, -1, 100);
        } else {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_SLIME_ATTACK, 2, 0.2f);
        }
    }

    private static class Belch extends AbstractAbility {

        public Belch() {
            super("Belch", 2800, 3600, 10, 100);
        }

        @Override
        public void updateDescription(Player player) {

        }

        @Override
        public List<Pair<String, String>> getAbilityInfo() {
            return null;
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
            wp.subtractEnergy(energyCost, false);

            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(wp, 8, 8, 8)
                    .aliveEnemiesOf(wp)
            ) {
                we.addDamageInstance(
                        wp,
                        name,
                        minDamageHeal,
                        maxDamageHeal,
                        critChance,
                        critMultiplier
                );
            }
            return true;
        }
    }

}
