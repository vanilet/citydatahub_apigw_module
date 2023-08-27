#!/bin/bash
mkdir -p logs

java    \
        -server \
        -jar apigw-1.1.0.jar > /dev/null 2>&1 &

sleep 2

tail -f ./logs/apigw.log
