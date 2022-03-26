package dev.geri.crescentcurrency.utils;

import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoinItem extends Item {

    private final Tier tier;

    public CoinItem(Settings settings, Tier tier) {
        super(settings);
        this.tier = tier;
    }

    public Tier getHigherTier() {

        ArrayList<Tier> tiers = new ArrayList<>(List.of(Tier.values()));
        Collections.reverse(tiers);

        Tier higher= null;
        for (Tier tier : tiers) {
            if (tier == this.tier) return higher;
            higher = tier;
        }

        return null;
    }

    public Tier getLowerTier() {

        Tier lower = null;
        for (Tier tier : CoinItem.Tier.values()) {
            if (tier == this.tier) return lower;
            lower = tier;
        }

        return null;
    }

    public enum Tier {
        COPPER,
        SILVER,
        GOLD,
        PLATINUM
    }

}
