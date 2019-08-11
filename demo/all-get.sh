#!/usr/bin/env bash

curl -H "Accept: application/json" \
-H "Content-Type: application/json" \
-o "all-get-response.json" \
-X POST "http://localhost:8080/views/all?n=30"
