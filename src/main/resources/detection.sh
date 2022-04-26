echo "starting the detection service"
sudo -u pi nohup python3 /usr/local/squirrel-ai/service/motion.py &
echo "finished starting the detection service"