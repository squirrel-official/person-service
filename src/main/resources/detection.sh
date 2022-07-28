# This will clear all logs and then restart service
echo "" > /usr/local/squirrel-ai/logs/service.log
echo "" > /usr/local/squirrel-ai/logs/motion.log
echo "" > /usr/local/squirrel-ai/logs/detection.log
echo "" > /usr/local/squirrel-ai/logs/notification.log
sudo modprobe v4l2loopback devices=2
#export FLASK_APP=/usr/local/squirrel-ai/service/web.py
#sudo -u pi /usr/bin/python3  /usr/local/squirrel-ai/service/web.py
sudo -u pi /usr/bin/python3  /usr/local/squirrel-ai/service/motionDetection.py
#sudo -u pi /usr/bin/python3  /usr/local/squirrel-ai/service/videoDetection.py
