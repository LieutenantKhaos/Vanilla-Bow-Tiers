package dev.vanillabowtiers;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TieredBowItem extends BowItem {
    private final float damageMultiplier;
    private final float velocityMultiplier;
    private final float drawSpeedMultiplier;

    public TieredBowItem(Properties properties, float damageMultiplier, float velocityMultiplier, float drawSpeedMultiplier) {
        super(properties);
        this.damageMultiplier = damageMultiplier;
        this.velocityMultiplier = velocityMultiplier;
        this.drawSpeedMultiplier = drawSpeedMultiplier;
    }

    public float damageMultiplier() {
        return damageMultiplier;
    }

    public float velocityMultiplier() {
        return velocityMultiplier;
    }

    public float drawSpeedMultiplier() {
        return drawSpeedMultiplier;
    }

    @Override
    protected Projectile createProjectile(
            Level level,
            LivingEntity shooter,
            ItemStack weaponStack,
            ItemStack projectileStack,
            boolean critical) {
        Projectile projectile = super.createProjectile(level, shooter, weaponStack, projectileStack, critical);
        if (projectile instanceof AbstractArrow arrow) {
            double arrowMultiplier = projectileStack.getItem() instanceof TieredArrowItem tieredArrow
                    ? tieredArrow.damageMultiplier()
                    : 1.0D;
            arrow.setBaseDamage(2.0D * arrowMultiplier * damageMultiplier);
        }
        return projectile;
    }

    @Override
    protected void shootProjectile(
            LivingEntity shooter,
            Projectile projectile,
            int index,
            float speed,
            float divergence,
            float yaw,
            LivingEntity target) {

        // Scale the authoritative projectile launch speed.
        super.shootProjectile(
                shooter,
                projectile,
                index,
                speed * velocityMultiplier,
                divergence,
                yaw,
                target
        );
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        int vanillaDuration = super.getUseDuration(stack, entity);
        if (drawSpeedMultiplier == 1.0F) {
            return vanillaDuration;
        }
        return Math.max(1, Math.round(vanillaDuration / drawSpeedMultiplier));
    }
}
