package dev.vanillabowtiers;

import dev.vanillabowtiers.enchantment.ModEnchantmentEffects;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VanillaBowTiers implements ModInitializer {
    public static final String MOD_ID = "vanilla_bow_tiers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEnchantmentEffects.initialize();
        ModItems.initialize();
        LOGGER.info("Vanilla Bow Tiers initialized.");
    }
}
