package com.ebicep.warlords.pve.newitems.gems;

import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.util.java.NamedEnum;
import org.bukkit.Material;

public enum GemType implements NamedEnum {

    IMPAIRMENT("Gem of Impairment", NewItemAttribute.DAMAGE, .5f, Material.REDSTONE),
    ALLEVIATION("Gem of Alleviation", NewItemAttribute.HEALING, .5f, Material.GLOWSTONE_DUST),
    SURGES("Gem of Surges", NewItemAttribute.MAX_ENERGY, 5, Material.GLOW_INK_SAC),
    NOURISHMENT("Gem of Nourishment", NewItemAttribute.HEALTH, 10, Material.GHAST_TEAR),
    SPEED("Gem of Speed", NewItemAttribute.ATTACK_SPEED, 2, Material.SUGAR),

    ;

    public static final GemType[] VALUES = values();

    private final String name;
    private final NewItemAttribute attribute;
    private final float valuePerTier;
    private final Material material;

    GemType(String name, NewItemAttribute attribute, float valuePerTier, Material material) {
        this.name = name;
        this.attribute = attribute;
        this.valuePerTier = valuePerTier;
        this.material = material;
    }

    @Override
    public String getName() {
        return name;
    }

    public NewItemAttribute getAttribute() {
        return attribute;
    }

    public float getValuePerTier() {
        return valuePerTier;
    }

    public Material getMaterial() {
        return material;
    }

}
