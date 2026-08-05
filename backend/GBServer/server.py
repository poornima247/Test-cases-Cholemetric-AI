from flask import Flask, request, jsonify
from PIL import Image
from io import BytesIO
from detect_model import detect_stones

app = Flask(__name__)

@app.route("/analyze", methods=["POST"])
def analyze():
    if "image" not in request.files:
        return jsonify({"error": "no image field"}), 400

    file = request.files["image"]
    try:
        image = Image.open(file.stream).convert("RGB")
    except Exception:
        return jsonify({"error": "invalid image"}), 400

    detections_raw = detect_stones(image)

    detections = []
    for (xc, yc, w, h, size_mm, score) in detections_raw:
        detections.append({
            "x_center": xc,
            "y_center": yc,
            "width": w,
            "height": h,
            "size_mm": size_mm,
            "score": score,
        })

    stone_count = len(detections)
    largest_size_mm = max((d["size_mm"] for d in detections), default=0.0)

    return jsonify({
        "stone_count": stone_count,
        "largest_size_mm": largest_size_mm,
        "detections": detections,
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True)
