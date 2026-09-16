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
    private final int projectileTier;

    public TieredArrowItem(Properties properties, double damageMultiplier, int projectileTier) {
        super(properties);
        this.damageMultiplier = damageMultiplier;
        this.projectileTier = projectileTier;
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
            tierAccess.vanillaBowTiers$setProjectileTier(projectileTier);
        }

        if (this == ModItems.ENDERITE_ARROW && arrow instanceof EnderiteArrowAccess enderiteArrowAccess) {
            enderiteArrowAccess.vanillaBowTiers$markEnderiteArrow();
        }
        return arrow;
    }
}
