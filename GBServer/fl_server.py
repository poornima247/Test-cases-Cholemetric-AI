"""
Simple Flask server for federated learning data collection

Install: pip install flask

Run: python3 fl_server.py
"""

from flask import Flask, request, jsonify
from datetime import datetime
import os
import json

app = Flask(__name__)

# Store training data
TRAINING_DATA_FILE = "fl_training_data.json"

def load_training_data():
    if os.path.exists(TRAINING_DATA_FILE):
        with open(TRAINING_DATA_FILE, 'r') as f:
            return json.load(f)
    return []

def save_training_data(data):
    with open(TRAINING_DATA_FILE, 'w') as f:
        json.dump(data, f, indent=2)

@app.route('/federated_learning/collect', methods=['POST'])
def collect_training_data():
    try:
        payload = request.json
        
        device_id = payload.get('device_id')
        training_samples = payload.get('training_samples', [])
        model_version = payload.get('model_version')
        timestamp = payload.get('timestamp')
        
        if not training_samples:
            return jsonify({
                'success': False,
                'error': 'No training samples provided'
            }), 400
        
        # Load existing data
        all_data = load_training_data()
        
        # Add new contribution
        contribution = {
            'device_id': device_id,
            'model_version': model_version,
            'timestamp': timestamp,
            'sample_count': len(training_samples),
            'samples': training_samples
        }
        
        all_data.append(contribution)
        
        # Save updated data
        save_training_data(all_data)
        
        print(f"✅ Received {len(training_samples)} samples from device {device_id}")
        print(f"   Total contributions: {len(all_data)}")
        
        return jsonify({
            'success': True,
            'message': f'Received {len(training_samples)} training samples',
            'total_contributions': len(all_data),
            'total_samples': sum(c['sample_count'] for c in all_data)
       })
        
    except Exception as e:
        return jsonify({
            'success': False,
            'error': str(e)
        }), 500

@app.route('/federated_learning/stats', methods=['GET'])
def get_stats():
    """Get statistics about collected data"""
    all_data = load_training_data()
    
    total_contributions = len(all_data)
    total_samples = sum(c['sample_count'] for c in all_data)
    unique_devices = len(set(c['device_id'] for c in all_data))
    
    return jsonify({
        'total_contributions': total_contributions,
        'total_samples': total_samples,
        'unique_devices': unique_devices,
        'latest_timestamp': all_data[-1]['timestamp'] if all_data else None
    })

if __name__ == '__main__':
    print("🚀 Federated Learning Server Starting...")
    print("📊 Training data will be saved to:", TRAINING_DATA_FILE)
    print("🌐 Server running on http://localhost:8000")
    app.run(host='0.0.0.0', port=8000, debug=True)
