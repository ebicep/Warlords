package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Keystone extends AbstractMob implements BossMob {

    private static final int WEDGE_DURATION_TICKS = 6 * GameRunnable.SECOND;

    private final Map<UUID, Integer> wedges = new HashMap<>();
    private int wedgesToSettle = 4;
    private boolean cracked = false;

    public Keystone(Location spawnLocation) {
        this(spawnLocation, "Keystone", 22000, 0.34f, 20, 750, 1000);
    }

    public Keystone(
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
                new Load()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.KEYSTONE;
    }

    @Override
    public Component getDescription() {
        return Component.text("Wedge of the Opex arch", NamedTextColor.GRAY);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_GRAY;
    }

    @Override
    public double getMobScale() {
        return 1.2;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (cracked || warlordsNPC.getCurrentHealth() / warlordsNPC.getMaxHealth() > 0.5f) {
            return;
        }
        cracked = true;
        wedgesToSettle = 2;
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_STONE_BREAK, 2, 0.5f);
        warlordsNPC.getGame().warlordsPlayers().forEach(player -> player.sendMessage(
                Component.text("Keystone splits. Two wedges are enough to settle.", NamedTextColor.GRAY)
        ));
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        if (event.getCause() != null && !event.getCause().isEmpty()) {
            return;
        }

        UUID uuid = receiver.getUuid();
        if (!receiver.getCooldownManager().hasCooldown(Wedge.class)) {
            wedges.remove(uuid);
        }

        int stacks = wedges.getOrDefault(uuid, 0) + 1;
        if (stacks >= wedgesToSettle) {
            wedges.remove(uuid);
            receiver.getCooldownManager().removeCooldown(Wedge.class, false);
            receiver.addInstance(InstanceBuilder
                    .damage()
                    .cause("Settle")
                    .source(attacker)
                    .min(900)
                    .max(1200)
            );
            attacker.addSpeedModifier(attacker, "Seated", -60, 2 * GameRunnable.SECOND);
            Utils.playGlobalSound(receiver.getLocation(), Sound.BLOCK_STONE_BREAK, 2, 0.6f);
            receiver.sendMessage(Component.text("Keystone settles into you.", NamedTextColor.RED));
            return;
        }

        wedges.put(uuid, stacks);
        receiver.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Wedge",
                stacks + "/" + wedgesToSettle,
                Wedge.class,
                new Wedge(),
                attacker,
                CooldownTypes.LOW_LEVEL_DEBUFF,
                cooldownManager -> {
                },
                WEDGE_DURATION_TICKS
        ));
        Utils.playGlobalSound(receiver.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1, 1.3f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.BLOCK_STONE_BREAK, 2, 0.4f);
        Utils.playGlobalSound(deathLocation, Sound.BLOCK_ANVIL_LAND, 1, 0.5f);
    }

    private static final class Wedge {
    }

    private static class Load extends AbstractPveAbility {

        public Load() {
            super(AbstractAbilityBuilder.create("keystoneLoad")
                    .pve()
                    .name("Load")
                    .cooldown(12)
                    .energyCost(0));
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            WarlordsEntity target = PlayerFilter.entitiesAround(wp, 18, 8, 18)
                    .aliveEnemiesOf(wp)
                    .stream()
                    .max(Comparator.comparingDouble(WarlordsEntity::getCurrentHealth))
                    .orElse(null);
            if (target == null) {
                return false;
            }

            EffectUtils.playParticleLinkAnimation(target.getLocation(), wp.getLocation(), 170, 165, 150, 2);
            Utils.playGlobalSound(wp.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 2, 0.6f);
            target.sendMessage(Component.text("Keystone loads its weight onto you.", NamedTextColor.GRAY));
            target.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .min(1000)
                    .max(1300)
            );
            wp.addInstance(InstanceBuilder
                    .healing()
                    .cause("Load")
                    .source(wp)
                    .value(600)
            );
            return true;
        }
    }
}
