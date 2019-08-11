#!/usr/bin/env bash

curl -H "Accept: application/json" \
-H "Content-Type: application/json" \
--data "@register-request.json" \
-X POST "http://localhost:8080/register/event"
echo
