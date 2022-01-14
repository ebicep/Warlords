package com.ebicep.warlords.maps;

public enum GameAddon {

    PRIVATE_GAME() {
        @Override
        public void modifyGame(Game game) {
        }  
    };

    public abstract void modifyGame(Game game);
}
