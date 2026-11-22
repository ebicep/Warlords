package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class Gravity extends BaseSet {

    private int radius;
    private int cooldownSeconds;

    @Override
    public void init() {
        super.init();
        this.radius = getValue("radius", int.class);
        this.cooldownSeconds = getValue("cooldownSeconds", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "gravity";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(radius, (float) cooldownSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        private int cooldownTicks;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Gravity.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (cooldownTicks > 0) {
                            cooldownTicks--;
                        }
                    }
            ));
            warlordsPlayer.getGame().registerEvents(new Listener() {

                @EventHandler
                public void onDeath(WarlordsDeathEvent event) {
                    if (cooldownTicks > 0 || !Objects.equals(event.getKiller(), warlordsPlayer)) {
                        return;
                    }
                    if (!(event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC)) {
                        return;
                    }
                    AbstractMob mob = warlordsNPC.getMob();
                    if (mob instanceof BossLike || !(mob instanceof EliteMob || mob instanceof ChampionMob)) {
                        return;
                    }
                    Location deathLocation = warlordsNPC.getLocation();
                    PlayerFilter.entitiesAround(deathLocation, radius, radius, radius)
                                .aliveEnemiesOf(warlordsPlayer)
                                .excluding(warlordsNPC)
                                .forEach(enemy -> {
                                    Vector toDeath = deathLocation.toVector().subtract(enemy.getLocation().toVector());
                                    if (toDeath.lengthSquared() < 0.01) {
                                        return;
                                    }
                                    double strength = Math.min(1.5, toDeath.length() * 0.25);
                                    enemy.setVelocity(getName(), toDeath.normalize().multiply(strength).setY(0.25), false);
                                });
                    warlordsPlayer.playSound(deathLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                    cooldownTicks = cooldownSeconds * 20;
                }

            });
        }

    }

}
