package com.rolnix.gra;

public class Boost {
    public enum BoostType {
        ammo,
        clear,
    }

    public BoostType getBoostType() {
        return boostType;
    }

    BoostType boostType;

    public String getBoostName() {
        return boostName;
    }

    private String boostName = "";

    public void setBoost(int boostIndex) {
        if (boostIndex == BoostType.ammo.ordinal()) {
            boostType = BoostType.ammo;
            boostName = "AMMO!";
        } else if (boostIndex == BoostType.clear.ordinal()) {
            boostType = BoostType.clear;
            boostName = "BOOOM!";
        }
    }
}
