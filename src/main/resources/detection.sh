# This will clear all logs and then restart service
echo "" > /usr/local/squirrel-ai/logs/service.log
echo "" > /usr/local/squirrel-ai/logs/motion.log
echo "" > /usr/local/person-service/logs/application.log
sudo -u pi /usr/bin/python3  /usr/local/squirrel-ai/service/videoDetection.py
