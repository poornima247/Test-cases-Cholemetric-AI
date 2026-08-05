import tensorflow as tf
import numpy as np
import os
from PIL import Image

TFLITE_PATH = os.path.join(
    os.path.dirname(__file__),
    "runs",
    "detect",
    "train",
    "weights",
    "best.tflite",
)

def verify_model():
    print(f"Loading TFLite model from {TFLITE_PATH}")
    interpreter = tf.lite.Interpreter(model_path=TFLITE_PATH)
    interpreter.allocate_tensors()

    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    print("Input details:", input_details)
    print("Output details:", output_details)

    # Create a dummy input
    input_shape = input_details[0]['shape']
    print(f"Expected input shape: {input_shape}")
    
    # Generate random input data (float32, normalized 0-1)
    input_data = np.random.random(input_shape).astype(np.float32)
    
    interpreter.set_tensor(input_details[0]['index'], input_data)
    interpreter.invoke()

    # Get output
    output_data = interpreter.get_tensor(output_details[0]['index'])
    print("Output shape:", output_data.shape)
    print("Output data sample (first 5 values):", output_data.flatten()[:5])
    
    print("Verification successful: Model loaded and inference ran without error.")

if __name__ == "__main__":
    verify_model()
