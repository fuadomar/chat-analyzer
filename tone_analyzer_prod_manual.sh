#!/bin/bash

# COMMAND LINE VARIABLES

env=$1
projectName=$2 #spring-boot


mvn clean package

#### CONFIGURABLE VARIABLES ######
#destination absolute path. It must be pre created or you can

destAbsPath=$HOME/$projectName/$env
configFolder=src/main/resources

#jar file

destFile=target/$projectName.jar

mkdir -p $HOME/deploy/$env

mkdir -p $HOME/logs/$env

echo $destFile

cp $destFile $HOME/deploy/$env
rm -f $projectName.conf
touch $projectName.conf
echo "LOG_FOLDER=/home/mozammal/logs/$env" >> $projectName.conf
echo 'JAVA_OPTS="-Xmx1024M -Dspring.profiles.active='$env'"' >> $projectName.conf
cp $HOME/tone-analyzer/$projectName.conf $HOME/deploy/$env


#./tone_analyzer_prod_manual.sh prod tone-analyzer-0.0.1-SNAPSHOT

#journalctl  -u toneanalyzer-prod.service | tail -n 100
