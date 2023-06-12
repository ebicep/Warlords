package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Soulbinding extends AbstractAbility implements Duration {

    public int playersBinded = 0;
    public int soulProcs = 0;
    public int linkProcs = 0;
    public int soulTeammatesCDReductions = 0;
    public int linkTeammatesHealed = 0;
    private final List<SoulBoundPlayer> soulBindedPlayers = new ArrayList<>();
    private final List<WarlordsEntity> playersProcedBySouls = new ArrayList<>();
    private final List<WarlordsEntity> playersProcedByLink = new ArrayList<>();
    private int tickDuration = 240;
    private float bindDuration = 2;

    public Soulbinding() {
        super("Soulbinding Weapon", 0, 0, 21.92f, 30, 0, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Your melee attacks ")
                               .append(Component.text("BIND", NamedTextColor.LIGHT_PURPLE))
                               .append(Component.text(" enemies for "))
                               .append(Component.text(format(bindDuration), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Against "))
                               .append(Component.text("BOUND", NamedTextColor.LIGHT_PURPLE))
                               .append(Component.text(" targets, your next Spirit Link will heal you for "))
                               .append(Component.text("400", NamedTextColor.GREEN))
                               .append(Component.text(" health (half for "))
                               .append(Component.text("2", NamedTextColor.YELLOW))
                               .append(Component.text(" nearby allies). Your next Fallen Souls will reduce the cooldown of all abilities by "))
                               .append(Component.text("1.5", NamedTextColor.GOLD))
                               .append(Component.text(" seconds. ("))
                               .append(Component.text("1", NamedTextColor.GOLD))
                               .append(Component.text(" second for "))
                               .append(Component.text("2", NamedTextColor.YELLOW))
                               .append(Component.text(" nearby allies). Both buffs may be activated for every melee hit. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."))
                               .append(Component.newline())
                               .append(Component.text("Successful soulbind procs will grant you "))
                               .append(Component.text("25%", NamedTextColor.GOLD))
                               .append(Component.text(" knockback resistance for "))
                               .append(Component.text("1.2 seconds", NamedTextColor.GOLD))
                               .append(Component.text(" (Max "))
                               .append(Component.text("3.6 ", NamedTextColor.GOLD))
                               .append(Component.text("seconds)."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Binded", "" + playersBinded));
        info.add(new Pair<>("Soul Procs", "" + soulProcs));
        info.add(new Pair<>("Soul Teammates CD Reductions", "" + soulTeammatesCDReductions));
        info.add(new Pair<>("Link Procs", "" + linkProcs));
        info.add(new Pair<>("Link Teammates Healed", "" + linkTeammatesHealed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "paladin.consecrate.activation", 2, 2);

        Soulbinding tempSoulBinding = new Soulbinding();
        tempSoulBinding.setPveUpgrade(pveUpgrade);
        if (wp.isInPve()) {
            wp.getCooldownManager().limitCooldowns(PersistentCooldown.class, Soulbinding.class, 2);
        }
        wp.getCooldownManager().addPersistentCooldown(
                name,
                "SOUL",
                Soulbinding.class,
                tempSoulBinding,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    if (new CooldownFilter<>(cooldownManager, PersistentCooldown.class).filterCooldownClass(Soulbinding.class).stream().count() == 1) {
                        if (wp.getEntity() instanceof Player) {
                            ((Player) wp.getEntity()).getInventory().getItem(0).removeEnchantment(Enchantment.OXYGEN);
                        }
                    }
                },
                tickDuration,
                soulbinding -> soulbinding.getSoulBindedPlayers().isEmpty(),
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.2, 0);
                        location.getWorld().spawnParticle(
                                Particle.SPELL_WITCH,
                                location,
                                2,
                                0.2,
                                0,
                                0.2,
                                0.1,
                                null,
                                true
                        );
                    }
                })
        );

        ItemMeta newItemMeta = player.getInventory().getItem(0).getItemMeta();
        newItemMeta.addEnchant(Enchantment.OXYGEN, 1, true);
        player.getInventory().getItem(0).setItemMeta(newItemMeta);

        return true;
    }

    public List<SoulBoundPlayer> getSoulBindedPlayers() {
        return soulBindedPlayers;
    }

    public void addPlayersBinded() {
        playersBinded++;
    }

    public void addSoulProcs() {
        soulProcs++;
    }

    public void addLinkProcs() {
        linkProcs++;
    }

    public void addSoulTeammatesCDReductions() {
        soulTeammatesCDReductions++;
    }

    public void addLinkTeammatesHealed() {
        linkTeammatesHealed++;
    }

    public boolean hasBoundPlayer(WarlordsEntity warlordsPlayer) {
        for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBoundPlayerSoul(WarlordsEntity warlordsPlayer) {
        for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                if (!soulBindedPlayer.isHitWithSoul()) {
                    soulBindedPlayer.setHitWithSoul(true);
                    playersProcedBySouls.add(warlordsPlayer);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public boolean hasBoundPlayerLink(WarlordsEntity warlordsPlayer) {
        for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                if (!soulBindedPlayer.isHitWithLink()) {
                    soulBindedPlayer.setHitWithLink(true);
                    playersProcedByLink.add(warlordsPlayer);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public float getBindDuration() {
        return bindDuration;
    }

    public void setBindDuration(float bindDuration) {
        this.bindDuration = bindDuration;
    }

    public List<WarlordsEntity> getAllProcedPlayers() {
        List<WarlordsEntity> procedPlayers = new ArrayList<>();
        procedPlayers.addAll(playersProcedBySouls);
        procedPlayers.addAll(playersProcedByLink);
        return procedPlayers;

    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public static class SoulBoundPlayer {
        private WarlordsEntity boundPlayer;
        private float timeLeft;
        private boolean hitWithLink;
        private boolean hitWithSoul;

        public SoulBoundPlayer(WarlordsEntity boundPlayer, float timeLeft) {
            this.boundPlayer = boundPlayer;
            this.timeLeft = timeLeft;
            hitWithLink = false;
            hitWithSoul = false;
        }

        public WarlordsEntity getBoundPlayer() {
            return boundPlayer;
        }

        public void setBoundPlayer(WarlordsEntity boundPlayer) {
            this.boundPlayer = boundPlayer;
        }

        public float getTimeLeft() {
            return timeLeft;
        }

        public void setTimeLeft(float timeLeft) {
            this.timeLeft = timeLeft;
        }

        public void decrementTimeLeft() {
            this.timeLeft -= .5;
        }

        public boolean isHitWithLink() {
            return hitWithLink;
        }

        public void setHitWithLink(boolean hitWithLink) {
            this.hitWithLink = hitWithLink;
        }

        public boolean isHitWithSoul() {
            return hitWithSoul;
        }

        public void setHitWithSoul(boolean hitWithSoul) {
            this.hitWithSoul = hitWithSoul;
        }
    }
}
