package dev.vanillabowtiers;

import dev.vanillabowtiers.access.EnderiteArrowAccess;
import dev.vanillabowtiers.access.ProjectileTierAccess;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Uses the vanilla Arrow entity. This is important for compatibility:
 * no custom projectile renderer or custom entity type is introduced.
 */
public final class TieredArrowItem extends ArrowItem {
    private final double damageMultiplier;

    public TieredArrowItem(Properties properties, double damageMultiplier) {
        super(properties);
        this.damageMultiplier = damageMultiplier;
    }

    public double damageMultiplier() {
        return damageMultiplier;
    }

    @Override
    public AbstractArrow createArrow(
            Level level,
            ItemStack pickupStack,
            LivingEntity owner,
            @Nullable ItemStack firedFromWeapon
    ) {
        AbstractArrow arrow = super.createArrow(level, pickupStack, owner, firedFromWeapon);
        arrow.setBaseDamageFromMob((float) (2.0D * damageMultiplier));
        if (arrow instanceof ProjectileTierAccess tierAccess) {
            int tier = this == ModItems.IRON_ARROW ? 1
                    : this == ModItems.GOLDEN_ARROW ? 2
                    : this == ModItems.DIAMOND_ARROW ? 3
                    : this == ModItems.NETHERITE_ARROW ? 4
                    : this == ModItems.ENDERITE_ARROW ? 5 : 0;
            tierAccess.vanillaBowTiers$setProjectileTier(tier);
        }

        if (this == ModItems.ENDERITE_ARROW && arrow instanceof EnderiteArrowAccess enderiteArrowAccess) {
            enderiteArrowAccess.vanillaBowTiers$markEnderiteArrow();
        }
        return arrow;
    }
}
