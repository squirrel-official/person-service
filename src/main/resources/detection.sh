# This will clear all logs and then restart service
echo "" > /usr/local/squirrel-ai/logs/service.log
echo "" > /usr/local/person-service/logs/application.log
echo "" > /var/log/motion/motion.log
sudo -u pi /usr/bin/python3  /usr/local/squirrel-ai/service/videoDetection.py
