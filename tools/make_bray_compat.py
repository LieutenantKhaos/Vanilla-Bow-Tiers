from pathlib import Path
import json, zipfile, shutil, sys

if len(sys.argv) != 2:
    raise SystemExit("Usage: python tools/make_bray_compat.py <Bray zip>")

source = Path(sys.argv[1])
out = Path("generated/Vanilla Bow Tiers - Bray Compatibility")
if out.exists():
    shutil.rmtree(out)

(out / "assets/vanilla_bow_tiers/items").mkdir(parents=True, exist_ok=True)

with zipfile.ZipFile(source) as z:
    names = z.namelist()
    bow_name = next(n for n in names if n.endswith("assets/minecraft/items/bow.json") and "/26.1/" not in n and "/1.21." not in n)
    arrow_name = next(n for n in names if n.endswith("assets/minecraft/items/arrow.json") and "/26.1/" not in n and "/1.21." not in n)
    bow = json.loads(z.read(bow_name))
    arrow = json.loads(z.read(arrow_name))

for name in ("iron_bow", "golden_bow", "diamond_bow", "netherite_bow"):
    (out / f"assets/vanilla_bow_tiers/items/{name}.json").write_text(json.dumps(bow, indent=2))
for name in ("iron_arrow", "golden_arrow", "diamond_arrow", "netherite_arrow"):
    (out / f"assets/vanilla_bow_tiers/items/{name}.json").write_text(json.dumps(arrow, indent=2))

(out / "pack.mcmeta").write_text(json.dumps({
    "pack": {
        "pack_format": 75,
        "description": "Bray visual compatibility for Vanilla Bow Tiers (generated from your installed Bray pack)"
    }
}, indent=2))

zip_path = Path("generated/Vanilla-Bow-Tiers-Bray-Compat.zip")
zip_path.parent.mkdir(exist_ok=True)
with zipfile.ZipFile(zip_path, "w", zipfile.ZIP_DEFLATED) as z:
    for p in out.rglob("*"):
        if p.is_file():
            z.write(p, p.relative_to(out))
print(zip_path)
