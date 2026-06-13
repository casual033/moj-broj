"""Generates Google Play store assets from the app icon source image.

Outputs (in this folder):
  - icon-512.png         512x512 app icon (32-bit PNG)
  - feature-1024x500.png feature graphic
"""
import math
import os

from PIL import Image, ImageDraw, ImageFilter, ImageFont

HERE = os.path.dirname(os.path.abspath(__file__))
SRC = "/Users/aleksaantelj/.cursor/projects/Users-aleksaantelj-Workspace-moj-broj/assets/screen-8fa958ec-fec9-41a5-aa5f-9d3c63007d7b.png"
FONT = "/Users/aleksaantelj/Workspace/moj-broj/app-android/src/main/res/font/plus_jakarta_sans.ttf"

src = Image.open(SRC).convert("RGBA")

# ---- 512x512 app icon (full designed artwork) ----
src.resize((512, 512), Image.LANCZOS).convert("RGB").save(os.path.join(HERE, "icon-512.png"))

# ---- hexagon glyph with feathered circular edge (reused from launcher fg) ----
box = (210, 175, 815, 780)
hexi = src.crop(box).convert("RGBA")
S = hexi.size[0]
mask = Image.new("L", (S, S), 0)
cx = cy = S / 2.0
r_full = S * 0.49
for y in range(S):
    for x in range(S):
        dist = math.hypot(x - cx, y - cy)
        if dist <= r_full:
            mask.putpixel((x, y), 255)
        elif dist <= r_full * 1.06:
            t = (dist - r_full) / (r_full * 0.06)
            mask.putpixel((x, y), int(255 * (1 - t)))
mask = mask.filter(ImageFilter.GaussianBlur(3))
hexi.putalpha(mask)

# ---- 1024x500 feature graphic ----
W, H = 1024, 500
fg = Image.new("RGBA", (W, H), (0, 0, 0, 0))
# vertical-ish radial gradient background (dark purple)
bg = Image.new("RGB", (W, H))
bgpx = bg.load()
c_center = (44, 32, 70)    # #2C2046
c_edge = (16, 11, 26)      # #100B1A
gcx, gcy = W * 0.62, H * 0.5
maxd = math.hypot(W, H) * 0.6
for y in range(H):
    for x in range(W):
        d = min(1.0, math.hypot(x - gcx, y - gcy) / maxd)
        r = int(c_center[0] + (c_edge[0] - c_center[0]) * d)
        g = int(c_center[1] + (c_edge[1] - c_center[1]) * d)
        b = int(c_center[2] + (c_edge[2] - c_center[2]) * d)
        bgpx[x, y] = (r, g, b)
fg.alpha_composite(bg.convert("RGBA"))

# hexagon on the right
hex_size = 420
h = hexi.resize((hex_size, hex_size), Image.LANCZOS)
fg.alpha_composite(h, (W - hex_size - 30, (H - hex_size) // 2))

draw = ImageDraw.Draw(fg)

def load_font(size, weight=800):
    f = ImageFont.truetype(FONT, size)
    try:
        f.set_variation_by_axes([weight])
    except Exception:
        pass
    return f

title_font = load_font(120, 800)
tag_font = load_font(40, 600)

# title "Moj broj" (yellow), tagline below (light)
yellow = (226, 198, 45)
light = (232, 223, 238)
draw.text((70, 175), "Moj broj", font=title_font, fill=yellow)
draw.text((76, 310), "Pogodi traženi broj!", font=tag_font, fill=light)

fg.convert("RGB").save(os.path.join(HERE, "feature-1024x500.png"))
print("OK")
