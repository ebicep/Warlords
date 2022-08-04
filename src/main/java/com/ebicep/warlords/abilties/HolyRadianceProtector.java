package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractHolyRadianceBase;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HolyRadianceProtector extends AbstractHolyRadianceBase {
    private boolean pveUpgrade = false;

    private final int markRadius = 15;
    private int markDuration = 6;

    public HolyRadianceProtector(float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super("Holy Radiance", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, 6);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Radiate with holy energy, healing\n" +
                "§7yourself and all nearby allies for\n" +
                "§a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health." +
                "\n\n" +
                "§7You may look at an ally to mark\n" +
                "§7them for §6" + markDuration + " §7seconds. Mark has an\n" +
                "§7optimal range of §e" + markRadius + " §7blocks. Your marked\n" +
                "§7ally will emit a second Holy Radiance\n" +
                "§7for §a50% §7of the original healing amount\n" +
                "§7after the mark ends.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));
        info.add(new Pair<>("Players Marked", "" + playersMarked));

        return info;
    }

    @Override
    public boolean chain(WarlordsEntity wp, Player player) {
        if (pveUpgrade) {
            for (WarlordsEntity circleTarget : PlayerFilter
                    .entitiesAround(wp, 6, 6, 6)
                    .aliveTeammatesOfExcludingSelf(wp)
            ) {
                emitMarkRadiance(wp, circleTarget);
            }

            return true;
        }

        for (WarlordsEntity markTarget : PlayerFilter
                .entitiesAround(player, markRadius, markRadius, markRadius)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (pveUpgrade) return true;
            if (Utils.isLookingAtMark(player, markTarget.getEntity()) && Utils.hasLineOfSight(player, markTarget.getEntity())) {
                Utils.playGlobalSound(player.getLocation(), "paladin.consecrate.activation", 2, 0.65f);
                // chain particles
                EffectUtils.playParticleLinkAnimation(player.getLocation(), markTarget.getLocation(), 0, 255, 70, 1);
                EffectUtils.playChainAnimation(wp.getLocation(), markTarget.getLocation(), new ItemStack(Material.RED_ROSE), 8);
                emitMarkRadiance(wp, markTarget);

                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                        ChatColor.GRAY + " You have marked " +
                        ChatColor.GREEN + markTarget.getName() +
                        ChatColor.GRAY + "!"
                );

                markTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN +
                        ChatColor.GRAY + " You have been granted " +
                        ChatColor.GREEN + "Protector's Mark" +
                        ChatColor.GRAY + " by " + wp.getName() + "!"
                );

                return true;
            } else {
                player.sendMessage("§cYour mark was out of range or you did not target a player!");
            }
        }
        return false;
    }

    private void emitMarkRadiance(WarlordsEntity giver, WarlordsEntity target) {
        HolyRadianceProtector tempMark = new HolyRadianceProtector(
                minDamageHeal,
                maxDamageHeal,
                cooldown,
                energyCost,
                critChance,
                critMultiplier
        );
        target.getCooldownManager().addRegularCooldown(
                name,
                "PROT MARK",
                HolyRadianceProtector.class,
                tempMark,
                giver,
                CooldownTypes.BUFF,
                cooldownManager -> {
                    if (target.isDead()) return;

                    ParticleEffect.SPELL.display(1, 1, 1, 0.06F, 12, target.getLocation(), 500);
                    Utils.playGlobalSound(target.getLocation(), "paladin.holyradiance.activation", 2, 0.95f);
                    for (WarlordsEntity waveTarget : PlayerFilter
                            .entitiesAround(target, 6, 6, 6)
                            .aliveTeammatesOf(target)
                    ) {
                        target.getGame().registerGameTask(
                                new FlyingArmorStand(
                                        target.getLocation(),
                                        waveTarget,
                                        giver,
                                        1.1,
                                        minDamageHeal * 0.5f,
                                        maxDamageHeal * 0.5f
                                ).runTaskTimer(Warlords.getInstance(), 1, 1)
                        );
                    }
                },
                markDuration * 20,
                (cooldown, ticksLeft, counter) -> {
                    if (counter % 10 == 0) {
                        Location playerLoc = target.getLocation();
                        Location particleLoc = playerLoc.clone();
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 10; j++) {
                                double angle = j / 9D * Math.PI * 2;
                                double width = 1;
                                particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                particleLoc.setY(playerLoc.getY() + i / 6D);
                                particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(0, 255, 70), particleLoc, 500);
                            }
                        }
                    }
                }
        );
    }

    public void setMarkDuration(int markDuration) {
        this.markDuration = markDuration;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}