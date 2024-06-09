package net.toaru.sidenplugin;

import org.bukkit.Material;

public class Ore {
    public boolean appropriate(Material item, Material block) {
        switch (block) {
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                return ironPickaxe(item);
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
                return stonePickaxe(item);
            default:
                return true;
        }
    }

    public boolean stonePickaxe(Material item) {
        switch (item) {
            case STONE_PICKAXE:
            case GOLDEN_PICKAXE:
                return false;
            default:
                return true;
        }
    }

    public boolean ironPickaxe(Material item) {
        switch (item) {
            case WOODEN_PICKAXE:
                return false;
            default:
                return stonePickaxe(item);
        }
    }
}
