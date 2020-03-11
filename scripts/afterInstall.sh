#!/bin/bash
#Commands to run after after installation
echo "Entered after install hook"
cd /home/ubuntu/webapp
sudo chown -R ubuntu:ubuntu /home/ubuntu/webapp/*
sudo chmod +x demo-0.0.1-SNAPSHOT.jar

#Killing the application
kill -9 $(ps -ef|grep demo-0.0.1 | grep -v grep | awk '{print $2}')

# #Removing log files
# sudo rm -rf logs/*.log

source /etc/profile.d/envvariable.sh
nohup java -jar demo-0.0.1-SNAPSHOT.jar > /dev/null 2> /dev/null < /dev/null &
