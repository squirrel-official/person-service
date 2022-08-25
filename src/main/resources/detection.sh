# This will clear all logs and then restart service
echo "" > /usr/local/squirrel-ai/logs/service.log
echo "" > /usr/local/squirrel-ai/logs/motion.log
echo "" > /usr/local/squirrel-ai/logs/detection.log
echo "" > /usr/local/squirrel-ai/logs/notification.log

sudo -u pi /usr/bin/python3  /usr/local/squirrel-ai/service/motionDetection.py
