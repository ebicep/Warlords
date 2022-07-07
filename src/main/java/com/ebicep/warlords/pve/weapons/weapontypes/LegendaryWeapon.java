package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.weapons.AbstractBetterWeapon;
import com.ebicep.warlords.util.java.Utils;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendaryWeapon extends AbstractBetterWeapon {

    protected String title;
    @Field("skill_boost")
    protected SkillBoosts selectedSkillBoost;
    @Field("unlocked_skill_boosts")
    protected List<SkillBoosts> unlockedSkillBoosts = new ArrayList<>();
    @Field("energy_per_second_bonus")
    protected int energyPerSecondBonus;
    @Field("energy_per_hit_bonus")
    protected int energyPerHitBonus;

    public LegendaryWeapon() {
    }

    public LegendaryWeapon(UUID uuid) {
        super(uuid);
        Specializations selectedSpec = Warlords.getPlayerSettings(uuid).getSelectedSpec();
        List<SkillBoosts> skillBoosts = selectedSpec.skillBoosts;
        this.selectedSkillBoost = skillBoosts.get(Utils.generateRandomValueBetweenInclusive(0, skillBoosts.size() - 1));
        this.unlockedSkillBoosts.add(selectedSkillBoost);
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GOLD;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>(super.getLore());
        lore.addAll(Arrays.asList(
                ChatColor.GRAY + "Energy per Second: " + ChatColor.GREEN + "+" + energyPerSecondBonus + "%",
                ChatColor.GRAY + "Energy per Hit: " + ChatColor.GREEN + "+" + energyPerHitBonus + "%",
                "",
                ChatColor.GREEN + Specializations.getClass(specialization).name + " (" + specialization.name + "):",
                ChatColor.GRAY + selectedSkillBoost.name + " - Description placeholder"
        ));
        return lore;
    }

    @Override
    public void generateStats() {

    }
}
