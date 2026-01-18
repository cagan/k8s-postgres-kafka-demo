#!/bin/bash

echo "=== Building Docker Image ==="
docker build -t demo-app:latest .

echo "=== Deploying PostgreSQL ==="
kubectl apply -f k8s/postgres/

echo "=== Waiting for PostgreSQL to be ready ==="
kubectl wait --for=condition=ready pod -l app=postgres --timeout=120s

echo "=== Deploying Zookeeper ==="
kubectl apply -f k8s/kafka/zookeeper-deployment.yaml
kubectl apply -f k8s/kafka/zookeeper-service.yaml

echo "=== Waiting for Zookeeper to be ready ==="
kubectl wait --for=condition=ready pod -l app=zookeeper --timeout=120s

echo "=== Deploying Kafka ==="
kubectl apply -f k8s/kafka/kafka-deployment.yaml
kubectl apply -f k8s/kafka/kafka-service.yaml

echo "=== Waiting for Kafka to be ready ==="
kubectl wait --for=condition=ready pod -l app=kafka --timeout=180s

echo "=== Deploying Application ==="
kubectl apply -f k8s/app/

echo "=== Waiting for Application to be ready ==="
kubectl wait --for=condition=ready pod -l app=demo-app --timeout=180s

echo "=== Deployment Complete ==="
echo "Application URL: http://localhost:30080"
echo ""
echo "Test endpoints:"
echo "  GET  http://localhost:30080/api/messages"
echo "  POST http://localhost:30080/api/messages"
echo "  POST http://localhost:30080/api/messages/kafka"
echo "  GET  http://localhost:30080/actuator/health"
