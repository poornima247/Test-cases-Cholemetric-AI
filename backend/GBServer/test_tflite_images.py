import tensorflow as tf
import numpy as np
import cv2
import os
from PIL import Image, ImageDraw, ImageFont

# Constants from detect_model.py
CONF_THRESHOLD = 0.15
MM_SCALE = 100.0
MM_OFFSET = 1.0
PIXEL_SPACING_MM = 0.412
SMALL_DIAMETER_PX = 25.0

# Paths
MODEL_PATH = os.path.join(os.path.dirname(__file__), "runs", "detect", "train", "weights", "best.tflite")
IMAGE_PATHS = [
    "/Users/saill1/.gemini/antigravity/brain/b6aca644-e4ed-4071-914f-32e35a21fa2d/uploaded_image_0_1763802611371.png",
    "/Users/saill1/.gemini/antigravity/brain/b6aca644-e4ed-4071-914f-32e35a21fa2d/uploaded_image_1_1763802611371.png",
    "/Users/saill1/.gemini/antigravity/brain/b6aca644-e4ed-4071-914f-32e35a21fa2d/uploaded_image_2_1763802611371.png"
]
OUTPUT_DIR = os.path.join(os.path.dirname(__file__), "output_results")

os.makedirs(OUTPUT_DIR, exist_ok=True)

def preprocess_image(image_path, input_shape):
    original_image = cv2.imread(image_path)
    original_image = cv2.cvtColor(original_image, cv2.COLOR_BGR2RGB)
    original_h, original_w = original_image.shape[:2]
    
    # Determine target size based on input_shape
    if input_shape[1] == 3: # NCHW
        target_h, target_w = input_shape[2], input_shape[3]
    else: # NHWC
        target_h, target_w = input_shape[1], input_shape[2]
        
    # Resize to model input shape
    resized_image = cv2.resize(original_image, (target_w, target_h))
    
    # Normalize to 0-1
    input_data = resized_image.astype(np.float32) / 255.0
    input_data = np.expand_dims(input_data, axis=0)
    
    return original_image, input_data, original_w, original_h

def nms(boxes, scores, iou_threshold=0.45):
    # boxes: [x1, y1, x2, y2]
    indices = cv2.dnn.NMSBoxes(boxes, scores, score_threshold=CONF_THRESHOLD, nms_threshold=iou_threshold)
    if len(indices) > 0:
        return indices.flatten()
    return []

def run_inference():
    print(f"Loading model from {MODEL_PATH}")
    interpreter = tf.lite.Interpreter(model_path=MODEL_PATH)
    interpreter.allocate_tensors()
    
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    input_shape = input_details[0]['shape'] # [1, 3, 640, 640] usually, but check
    
    # TFLite input might be NHWC or NCHW depending on conversion. 
    # Ultralytics export usually keeps NCHW if not specified otherwise, but TFLite prefers NHWC.
    # Let's check input details shape.
    print(f"Model Input Shape: {input_shape}")
    
    for i, img_path in enumerate(IMAGE_PATHS):
        print(f"\nProcessing {os.path.basename(img_path)}...")
        original_image, input_data, orig_w, orig_h = preprocess_image(img_path, input_shape)
        
        # If model expects NCHW (1, 3, 640, 640), transpose input
        if input_shape[1] == 3:
            print("Transposing input to NCHW...")
            input_data = np.transpose(input_data, (0, 3, 1, 2))
            
        print(f"Input data shape: {input_data.shape}")
        interpreter.set_tensor(input_details[0]['index'], input_data)
        interpreter.invoke()
        
        output_data = interpreter.get_tensor(output_details[0]['index']) # (1, 6, 8400)
        
        # Transpose to (8400, 6) -> [cx, cy, w, h, class0, class1] (assuming 2 classes)
        # Or [cx, cy, w, h, conf] if 1 class?
        # Let's assume standard YOLOv8 output: (1, 4+nc, 8400)
        output_data = output_data[0].T # (8400, 6)
        
        boxes = []
        scores = []
        
        # Parse output
        # columns 0,1,2,3 are cx, cy, w, h (normalized relative to 640x640?)
        # Actually YOLOv8 export output is usually absolute pixels in 640x640 space? 
        # Or normalized? Ultralytics usually exports normalized xywh? No, usually pixels.
        # Let's check values.
        
        # Filter by confidence
        # Assuming last 2 columns are class scores. Max of them is score.
        class_scores = output_data[:, 4:]
        max_scores = np.max(class_scores, axis=1)
        mask = max_scores > CONF_THRESHOLD
        
        filtered_output = output_data[mask]
        filtered_scores = max_scores[mask]
        
        if len(filtered_output) == 0:
            print("No detections found.")
            continue
            
        # Convert to [x1, y1, x2, y2] for NMS
        # output is [cx, cy, w, h] in 640x640 scale
        
        nms_boxes = []
        final_detections = []
        
        for box, score in zip(filtered_output, filtered_scores):
            cx, cy, w, h = box[:4]
            
            # Convert to x1, y1, x2, y2
            x1 = cx - w / 2
            y1 = cy - h / 2
            x2 = cx + w / 2
            y2 = cy + h / 2
            
            nms_boxes.append([x1, y1, w, h]) # NMS expects xywh (top-left, w, h)
            scores.append(float(score))
            
        indices = nms(nms_boxes, scores)
        
        print(f"Found {len(indices)} stones after NMS.")
        
        # Draw on original image
        pil_img = Image.fromarray(original_image)
        draw = ImageDraw.Draw(pil_img)
        
        # Scale factors
        scale_x = orig_w / 640.0
        scale_y = orig_h / 640.0
        
        stone_sizes = []
        
        for idx in indices:
            box = nms_boxes[idx] # x1, y1, w, h in 640 space
            score = scores[idx]
            
            x1_640, y1_640, w_640, h_640 = box
            
            # Scale to original image
            x1 = x1_640 * scale_x
            y1 = y1_640 * scale_y
            w_px = w_640 * scale_x
            h_px = h_640 * scale_y
            x2 = x1 + w_px
            y2 = y1 + h_px
            
            # Size calculation logic from detect_model.py
            diameter_px = float(max(w_px, h_px))
            
            # Normalized size (0-1) relative to original image
            norm_w = w_px / orig_w
            norm_h = h_px / orig_h
            norm_size = float(max(norm_w, norm_h))
            
            if diameter_px < SMALL_DIAMETER_PX:
                size_mm = norm_size * MM_SCALE + MM_OFFSET
            else:
                size_mm = diameter_px * PIXEL_SPACING_MM
                
            stone_sizes.append(size_mm)
            
            # Draw box
            draw.rectangle([x1, y1, x2, y2], outline="red", width=3)
            
            # Draw label
            label = f"{size_mm:.1f}mm ({score:.2f})"
            draw.text((x1, y1 - 10), label, fill="red")
            
        # Save result
        output_filename = f"result_{i}.png"
        output_path = os.path.join(OUTPUT_DIR, output_filename)
        pil_img.save(output_path)
        print(f"Saved result to {output_path}")
        
        # Stats
        largest_stone = max(stone_sizes) if stone_sizes else 0.0
        print(f"Stone Count: {len(stone_sizes)}")
        print(f"Largest Stone: {largest_stone:.2f} mm")

if __name__ == "__main__":
    run_inference()
