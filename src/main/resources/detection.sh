# This will clear all logs and then restart service
echo "" > /usr/local/squirrel-ai/logs/service.log
echo "" > /usr/local/squirrel-ai/logs/motion.log
echo "" > /usr/local/squirrel-ai/logs/detection.log
echo "" > /usr/local/squirrel-ai/logs/notification.log
export FLASK_APP=/usr/local/squirrel-ai/service/web.py
nohup flask run &
#sudo -u pi /usr/bin/python3  /usr/local/squirrel-ai/service/videoDetection.py
