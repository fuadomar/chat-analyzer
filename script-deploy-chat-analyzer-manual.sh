#!/bin/bash

# COMMAND LINE VARIABLES

env=$1
projectName=$2 #spring-boot


mvn clean package -Dmaven.test.skip=true

#jar file

destFile=target/$projectName.jar

curDirectory=`pwd`

destFile=$curDirectory/target/$projectName.jar

deployFolderName=deploy-chat-analyzer

mkdir -p $HOME/$deployFolderName/$env

echo $destFile

yes | cp -rf $destFile $HOME/$deployFolderName/$env
rm -f $projectName.conf
touch $projectName.conf
echo "LOG_FOLDER=/var/logs/$env" >> $projectName.conf
echo 'JAVA_OPTS="-Xmx2048M -Dspring.profiles.active='$env'"' >> $projectName.conf
cp $curDirectory/$projectName.conf $HOME/$deployFolderName/$env
rm -f $curDirectory/$projectName.conf



#use the following command for app deployment from command line

#./script-deploy-chat-analyzer-manual.sh prod chat-analyzer-0.0.1-SNAPSHOT

#run as service
#sudo systemctl daemon-reload
#sudo systemctl enable chatAnalyzer
#sudo systemctl start chatAnalyzer
#sudo systemctl stop chatAnalyzer

#journalctl  -u chatAnalyzer.service | tail -n 100
