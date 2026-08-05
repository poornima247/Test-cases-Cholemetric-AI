from ultralytics import YOLO
import os

# Path to the model
MODEL_PATH = os.path.join(
    os.path.dirname(__file__),
    "runs",
    "detect",
    "train",
    "weights",
    "best.pt",
)

def export_model():
    print(f"Loading model from {MODEL_PATH}")
    model = YOLO(MODEL_PATH)
    
    print("Exporting to ONNX with opset 12...")
    # Export to ONNX with opset 12 for onnx-tf compatibility
    path = model.export(format="onnx", opset=12)
    print(f"Export completed. Model saved at: {path}")

if __name__ == "__main__":
    export_model()
