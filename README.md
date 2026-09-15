# Vanilla Bow Tiers — Fabric 26.2

A small vanilla-style bow/arrow tier mod intended to remain friendly to resource packs such as Bray's Better Bows & Arrows.

## Tiers

| Tier | Bow durability | Bow launch multiplier | Arrow damage multiplier |
|---|---:|---:|---:|
| Iron | 500 | 1.05x | 1.10x |
| Gold | 192 | 1.12x | 1.05x |
| Diamond | 1024 | 1.16x | 1.25x |
| Netherite | 1536 | 1.20x | 1.40x |

Vanilla bow/arrow remain Tier 0.

## Visual design

The mod ships **no copied Minecraft or Bray textures**. Its item definitions point at vanilla bow/arrow model assets.
That means the default look is intentionally vanilla.

### Bray compatibility

Because Bray replaces `minecraft:bow` with a more involved client-item definition rather than merely replacing the old flat model,
a new mod item ID cannot automatically inherit the whole Bray definition.

Run:

`python tools/make_bray_compat.py "path/to/Bray's Bows & Arrows 26.2.zip"`

This creates `generated/Vanilla-Bow-Tiers-Bray-Compat.zip`. Put that generated pack above this mod's resources / alongside Bray,
with Bray enabled. The generated compatibility pack contains only item-definition JSON derived from **your own local copy** and
references Bray's models; it does not bundle Bray's models or textures.

## Build requirements

- Minecraft 26.2
- Fabric Loader 0.19.3+
- Fabric API 0.156.0+26.2
- Java 25
- Loom 1.17
- Gradle 9.5.1

The Gradle wrapper properties are included, but the binary `gradle-wrapper.jar` is intentionally not generated in this environment.
Open the folder in IntelliJ with a local Gradle 9.5.1 install, or regenerate the wrapper with:

`gradle wrapper --gradle-version 9.5.1`

Then:

`./gradlew build`

The output jar will be under `build/libs/`.

## Design notes

- Bows subclass vanilla `BowItem`.
- Arrows subclass vanilla `ArrowItem` and create vanilla `AbstractArrow` projectiles.
- No custom projectile entity or renderer is used.
- Custom arrows are added to `#minecraft:arrows`.
- Custom bows are added to vanilla bow/durability/vanishing enchantable tags.
- Netherite items are fire resistant.
- Netherite bow uses the vanilla smithing upgrade flow.

## Early-build note

This is a source project targeted directly at the 26.2 API. The code was written against the current 26.2 Mojang-named API signatures,
but this environment cannot run the full Gradle/Minecraft toolchain, so the first local Gradle build is the final compile check.
If Loom reports a renamed method/signature, send the build error back and it can be patched quickly.

## v0.1.2 compile patch

Minecraft 26.2 no longer exposes `AbstractArrow#getBaseDamage()` in the official named API.
Tiered arrows now use `setBaseDamageFromMob(float)` with vanilla's 2.0 arrow base damage multiplied
by the tier value. This preserves vanilla Arrow entities and avoids a custom projectile renderer.

## v0.1.4 textures

Added custom 32x32 tier textures for all four bows and all four arrows, based on the approved concept:
iron reinforcement, gold fittings, diamond/cyan reinforcement, and dark netherite reinforcement.
The mod owns these textures. Bray compatibility remains a separate resource-pack layer and no Bray artwork
is redistributed in the mod.

## v0.1.5 texture routing fix

The tier client-item definitions now point directly to `vanilla_bow_tiers:item/<tier>`
instead of falling through to `minecraft:item/bow` / `minecraft:item/arrow`.
This fixes the inventory icons remaining visually vanilla.

A regenerated Bray compatibility pack is included in `generated/`.
Place **Vanilla Bow Tiers - Bray Compat v0.1.5 above Bray** in the resource-pack list.
This revision prioritizes correct tier identity first; Bray continues to style vanilla bows.
The tiered 3D Bray geometry adapter will be refined separately rather than silently replacing
the tier textures with Bray's vanilla bow texture.

## 0.3.3 skeleton tiers
The 0.3.3 line adds occasional tiered bows to naturally spawned Skeletons and Wither Skeletons while keeping Minecraft's vanilla skeleton bow AI. Matching tiered arrows are used for their shots, and the built-in Bray compatibility pack provides tier-specific nocked-arrow visuals while drawing.

### Compatibility note: Timeless
Timeless can intentionally restore classic visual bugs, including purple skeleton arrows. That behavior is external to Vanilla Bow Tiers and is not overridden by VBT. When validating VBT's tiered projectile textures, disable the relevant Timeless classic-bug behavior.
