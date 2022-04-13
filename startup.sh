java    \
        -server \
        -jar apigw-1.0.0.jar > /dev/null 2>&1 &
tail -f ./logs/apigw.log
