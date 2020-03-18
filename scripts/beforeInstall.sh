#!/bin/bash

cd /home/ubuntu

sudo systemctl start amazon-cloudwatch-agent.service

dir="webapp"

if [ -d $dir ] ; then
    sudo rm -Rf $dir
    sudo mkdir webapp
    cd ..
fi