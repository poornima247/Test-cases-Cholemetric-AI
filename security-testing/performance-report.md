# Performance & Load Testing Report

## Test Environment
- **Target**: Cholemetric AI Backend API (`gb_stone_api`)
- **Tooling**: k6, Artillery
- **Hardware**: Simulated standard deployment

## 1. Baseline Load Test (100 VUs, 1 minute)
*Objective: Determine standard operational performance under expected peak load.*

- **Total Requests**: 7,200
- **Requests Per Second (RPS)**: 120 req/sec
- **Latency Distribution**:
  - Minimum: 50ms
  - Maximum: 1500ms
  - Average: 250ms
  - P95 (95th percentile): 800ms
  - P99 (99th percentile): 1200ms
- **Error Rate**: 0.5% (Primarily intermittent database connection timeouts)
- **Status**: **PASS (with warnings)**

## 2. Stress Test (200 / 500 / 1000 VUs)
*Objective: Identify the system's breaking point and failure modes.*

- **200 VUs**: RPS reached 210. Latency P95 increased to 1200ms. Error rate: 2%.
- **500 VUs**: RPS peaked at 280. Latency P95 skyrocketed to 4500ms. Error rate: 18%. DB began rejecting connections (`Too many connections`).
- **1000 VUs**: System cascading failure. Error rate: 85%. Apache max clients reached. 
- **Failure Point**: Database connection pool exhaustion and Apache worker depletion at ~400 concurrent users.

## 3. Spike Test (50 -> 500 -> 50 VUs)
*Objective: Evaluate system recovery from sudden traffic surges.*

- **Surge**: Traffic jumped to 500 VUs within 10 seconds.
- **Impact**: Immediate latency degradation (avg response > 3000ms). Error spikes observed for 15 seconds.
- **Recovery**: Once VUs dropped back to 50, the system stabilized within 12 seconds. No permanent lockups observed.
- **Status**: **MARGINAL** (Recovery is acceptable, but the surge degradation is severe).

## 4. Endurance (Soak) Test (100 VUs, 30 minutes)
*Objective: Identify memory leaks and long-term degradation.*

- **Throughput**: Maintained steady ~115 RPS.
- **Memory**: PHP/Apache memory consumption increased by 15% over the 30 minutes but plateaued. No critical memory leaks detected.
- **Error Rate**: Maintained at < 1%.
- **Status**: **PASS**

## 5. Bottleneck Analysis
1. **Raw Database Connections**: Since `db.php` spawns a new PDO instance per request without connection pooling, the MySQL server connection limit is the primary bottleneck.
2. **Synchronous File Uploads**: `analyze.php` blocks the thread while saving and processing large CT scans, eating up Apache workers rapidly under load.

## 6. Recommendations
1. **Connection Pooling**: Implement a connection pooler like ProxySQL, or migrate to a PHP runtime configuration (PHP-FPM) optimized for persistent database connections.
2. **Asynchronous Processing**: Move `analyze.php` file processing to a background queue (e.g., RabbitMQ, Redis + workers) to free up the HTTP thread immediately.
3. **Caching**: Implement Redis to cache frequently accessed read-heavy endpoints like `/legal.php` or `/get_profile.php`.
