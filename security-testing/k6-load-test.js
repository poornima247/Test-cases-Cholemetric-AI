import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

const BASE_URL = __ENV.BASE_URL || 'http://localhost/gb_stone_api';

export const options = {
  scenarios: {
    baseline: {
      executor: 'constant-vus',
      vus: 100,
      duration: '1m',
      tags: { test_type: 'baseline' },
    },
    // Uncomment these to run other test types
    /*
    stress: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '2m', target: 200 },
        { duration: '2m', target: 500 },
        { duration: '1m', target: 0 },
      ],
      tags: { test_type: 'stress' },
    },
    spike: {
      executor: 'ramping-vus',
      startVUs: 50,
      stages: [
        { duration: '10s', target: 500 },
        { duration: '1m', target: 500 },
        { duration: '10s', target: 50 },
      ],
      tags: { test_type: 'spike' },
    },
    endurance: {
      executor: 'constant-vus',
      vus: 100,
      duration: '30m',
      tags: { test_type: 'endurance' },
    }
    */
  },
  thresholds: {
    'http_req_duration': ['p(95)<800'],
    'errors': ['rate<0.01'], // < 1% error rate
  },
};

export default function () {
  // Test 1: Legal endpoint (GET)
  let res = http.get(`${BASE_URL}/legal.php`);
  let success = check(res, {
    'status is 200 (legal)': (r) => r.status === 200,
  });
  errorRate.add(!success);
  
  sleep(1);

  // Test 2: Login attempt (POST)
  const loginPayload = JSON.stringify({
    email: 'test@example.com',
    password: 'password123'
  });
  
  res = http.post(`${BASE_URL}/login.php`, loginPayload, {
    headers: { 'Content-Type': 'application/json' },
  });
  success = check(res, {
    'status is 200 (login)': (r) => r.status === 200,
  });
  errorRate.add(!success);

  sleep(1);

  // Test 3: Get Profile (Simulating generic load)
  const profilePayload = JSON.stringify({
    doctor_id: 1
  });
  res = http.post(`${BASE_URL}/get_profile.php`, profilePayload, {
    headers: { 'Content-Type': 'application/json' },
  });
  success = check(res, {
    'status is 200 (profile)': (r) => r.status === 200,
  });
  errorRate.add(!success);
  
  sleep(1);
}
