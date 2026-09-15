package dev.vanillabowtiers.client;

import dev.vanillabowtiers.TieredArrowItem;
import dev.vanillabowtiers.TieredBowItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

public final class VanillaBowTiersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer("vanilla_bow_tiers").ifPresent(container -> {
            ResourceLoader.registerBuiltinPack(
                    Identifier.fromNamespaceAndPath("vanilla_bow_tiers", "brays_compat"),
                    container,
                    Component.literal("Vanilla Bow Tiers - Bray + Enderite Compatibility"),
                    PackActivationType.NORMAL
            );
            ResourceLoader.registerBuiltinPack(
                    Identifier.fromNamespaceAndPath("vanilla_bow_tiers", "even_better_enchants_compat"),
                    container,
                    Component.literal("Vanilla Bow Tiers - Even Better Enchants Compatibility"),
                    PackActivationType.NORMAL
            );
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.getItem() instanceof TieredBowItem bow) {
                int sharpshooterLevel = getSharpshooterLevel(lines);

                lines.add(Component.empty());
                lines.add(Component.literal("When fired:").withStyle(ChatFormatting.GRAY));
                lines.add(Component.literal(String.format(" %.2f Arrow Damage", bow.damageMultiplier()))
                        .withStyle(ChatFormatting.DARK_GREEN));
                lines.add(Component.literal(String.format(" %.2f Arrow Velocity", bow.velocityMultiplier()))
                        .withStyle(ChatFormatting.DARK_GREEN));
                lines.add(Component.literal(String.format(" %.2f Draw Speed", bow.drawSpeedMultiplier()))
                        .withStyle(ChatFormatting.DARK_GREEN));

                if (sharpshooterLevel > 0) {
                    int accuracyBonus = Math.min(sharpshooterLevel, 4) * 25;
                    lines.add(Component.literal(" +" + accuracyBonus + " Accuracy")
                            .withStyle(ChatFormatting.DARK_GREEN));
                }
            } else if (BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals("enderitemod:enderite_bow")) {
                // Enderite Bow is registered by Enderite Mod, so apply the agreed
                // Epic tooltip-name color without creating a hard dependency.
                if (!lines.isEmpty()) {
                    lines.set(0, lines.get(0).copy().withStyle(ChatFormatting.LIGHT_PURPLE));
                }
                int sharpshooterLevel = getSharpshooterLevel(lines);

                lines.add(Component.empty());
                lines.add(Component.literal("When fired:").withStyle(ChatFormatting.GRAY));
                lines.add(Component.literal(" 1.25 Arrow Velocity")
                        .withStyle(ChatFormatting.DARK_GREEN));
                lines.add(Component.literal(" 1.50 Arrow Damage")
                        .withStyle(ChatFormatting.DARK_GREEN));
                lines.add(Component.literal(" 1.25 Draw Speed")
                        .withStyle(ChatFormatting.DARK_GREEN));

                if (sharpshooterLevel > 0) {
                    int accuracyBonus = Math.min(sharpshooterLevel, 4) * 25;
                    lines.add(Component.literal(" +" + accuracyBonus + " Accuracy")
                            .withStyle(ChatFormatting.DARK_GREEN));
                }
            } else if (stack.getItem() instanceof TieredArrowItem arrow) {
                lines.add(Component.empty());
                lines.add(Component.literal("Projectile:").withStyle(ChatFormatting.GRAY));
                lines.add(Component.literal(String.format(" %.2f Damage", arrow.damageMultiplier()))
                        .withStyle(ChatFormatting.DARK_GREEN));
            }
        });
    }

    private static int getSharpshooterLevel(java.util.List<Component> lines) {
        for (Component line : lines) {
            String text = line.getString();
            if (text.equals("Sharpshooter I")) return 1;
            if (text.equals("Sharpshooter II")) return 2;
            if (text.equals("Sharpshooter III")) return 3;
            if (text.equals("Sharpshooter IV")) return 4;
        }
        return 0;
    }
}
