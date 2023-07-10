package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.slime.AbstractSlime;
import com.ebicep.warlords.pve.mobs.slime.VoidSlime;
import com.ebicep.warlords.pve.mobs.zombie.SlimeZombie;
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
                MobTier.BOSS,
                null,
                100000,
                0.3f,
                30,
                0,
                0,
                new Belch(), new SpawnSlimeZombies(), new SpawnVoidSlimes()
        );
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
        if (ticksElapsed % 300 == 0) {
            for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
                option.spawnNewMob(new SlimeZombie(warlordsNPC.getLocation()));
            }
        }

        if (ticksElapsed % 1200 == 0) {
            for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
                option.spawnNewMob(new VoidSlime(warlordsNPC.getLocation()));
            }
        }
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
            super("Belch", 15, 100);
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

    private static class SpawnSlimeZombies extends AbstractAbility {

        public SpawnSlimeZombies() {
            super("Spawn Slime Zombies", 5, 100);
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
            PveOption pve = wp.getGame()
                              .getOptions()
                              .stream()
                              .filter(PveOption.class::isInstance)
                              .map(PveOption.class::cast)
                              .findFirst().orElse(null);
            if (pve == null) {
                return false;
            }
            for (int i = 0; i < pve.getGame().warlordsPlayers().count(); i++) {
                pve.spawnNewMob(new SlimeZombie(wp.getLocation()), wp.getTeam());
            }
            return true;
        }
    }

    private static class SpawnVoidSlimes extends AbstractAbility {

        public SpawnVoidSlimes() {
            super("Spawn Void Slimes", 60, 100);
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
            PveOption pve = wp.getGame()
                              .getOptions()
                              .stream()
                              .filter(PveOption.class::isInstance)
                              .map(PveOption.class::cast)
                              .findFirst().orElse(null);
            if (pve == null) {
                return false;
            }
            for (int i = 0; i < pve.getGame().warlordsPlayers().count(); i++) {
                pve.spawnNewMob(new SlimeZombie(wp.getLocation()), wp.getTeam());
            }
            return true;
        }
    }
}
