package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

public class EarthenSpikeBlock {

    FallingBlock block;
    Player player;
    WarlordsPlayer user;

    public EarthenSpikeBlock(FallingBlock block, Player player, WarlordsPlayer user) {
        this.block = block;
        this.player = player;
        this.user = user;
    }

    public FallingBlock getBlock() {
        return block;
    }

    public void setBlock(FallingBlock block) {
        this.block = block;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public WarlordsPlayer getUser() {
        return user;
    }

    public void setUser(WarlordsPlayer user) {
        this.user = user;
    }
}
