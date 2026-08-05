import onnx
from onnx_tf.backend import prepare
import tensorflow as tf
import os

ONNX_PATH = os.path.join(
    os.path.dirname(__file__),
    "runs",
    "detect",
    "train",
    "weights",
    "best.onnx",
)

TF_PATH = os.path.join(
    os.path.dirname(__file__),
    "runs",
    "detect",
    "train",
    "weights",
    "best_saved_model",
)

TFLITE_PATH = os.path.join(
    os.path.dirname(__file__),
    "runs",
    "detect",
    "train",
    "weights",
    "best.tflite",
)

def convert():
    print(f"Loading ONNX model from {ONNX_PATH}")
    onnx_model = onnx.load(ONNX_PATH)
    
    print("Converting ONNX to TensorFlow SavedModel...")
    tf_rep = prepare(onnx_model)
    tf_rep.export_graph(TF_PATH)
    print(f"SavedModel exported to {TF_PATH}")
    
    print("Converting SavedModel to TFLite...")
    converter = tf.lite.TFLiteConverter.from_saved_model(TF_PATH)
    # Optional: Optimizations
    # converter.optimizations = [tf.lite.Optimize.DEFAULT]
    tflite_model = converter.convert()
    
    with open(TFLITE_PATH, "wb") as f:
        f.write(tflite_model)
    print(f"TFLite model saved to {TFLITE_PATH}")

if __name__ == "__main__":
    convert()
