from typing import List, Tuple
from PIL import Image
from ultralytics import YOLO
import os

# ---- CONFIG ----
CONF_THRESHOLD = 0.15      # confidence threshold for detections
MM_SCALE = 100.0           # for very small stones: norm_size * 100
MM_OFFSET = 1.0            # +1 mm for very small stones
PIXEL_SPACING_MM = 0.412   # mm per pixel for medium/large stones

# If diameter in pixels is below this -> treat as "very small"
SMALL_DIAMETER_PX = 25.0   # you can tweak (20–30) based on results

# Path to your YOLOv8 model inside the GBServer folder
MODEL_PATH = os.path.join(
    os.path.dirname(__file__),
    "runs",
    "detect",
    "train",
    "weights",
    "best.pt",
)

# Load YOLOv8 model once
model = YOLO(MODEL_PATH)


def detect_stones(
    image: Image.Image,
    conf_threshold: float = CONF_THRESHOLD
) -> List[Tuple[float, float, float, float, float, float]]:
    """
    Run YOLOv8 on the image.

    Returns list of detections:
    (x_center_norm, y_center_norm, width_norm, height_norm, size_mm, score)
    """
    results = model(image, verbose=False)[0]
    boxes = results.boxes

    if boxes is None or len(boxes) == 0:
        print("YOLO: no boxes found")
        return []

    xywhn = boxes.xywhn.cpu().numpy()   # (N, 4) normalized: x_center, y_center, w, h
    confs = boxes.conf.cpu().numpy()    # (N,)

    img_w, img_h = image.size
    print(f"YOLO raw boxes: {len(boxes)}, image size: {img_w}x{img_h}")

    detections: List[Tuple[float, float, float, float, float, float]] = []

    for (xc, yc, w, h), score in zip(xywhn, confs):
        if score < conf_threshold:
            continue

        # Convert normalized width/height to pixels for size logic
        box_width_px = w * img_w
        box_height_px = h * img_h
        diameter_px = float(max(box_width_px, box_height_px))

        # Also keep normalized size (0–1) for your small-stone rule
        norm_size = float(max(w, h))

        # --- SIZE LOGIC ---
        if diameter_px < SMALL_DIAMETER_PX:
            # Very small stones: use your old normalized rule
            size_mm = norm_size * MM_SCALE + MM_OFFSET
        else:
            # Medium/large stones: use pixel spacing 0.412 mm
            size_mm = diameter_px * PIXEL_SPACING_MM

        detections.append((
            float(xc),      # normalized x_center
            float(yc),      # normalized y_center
            float(w),       # normalized width
            float(h),       # normalized height
            size_mm,        # mixed-rule mm size
            float(score),   # confidence
        ))

    print(f"YOLO kept {len(detections)} detections above conf {conf_threshold}")
    return detections