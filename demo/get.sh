#!/usr/bin/env bash

curl -H "Accept: application/json" \
-H "Content-Type: application/json" \
-o "get-response.json" \
-X GET "http://localhost:8080/views/e1?n=30"
