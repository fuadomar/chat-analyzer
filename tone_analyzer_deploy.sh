#!/bin/bash

# COMMAND LINE VARIABLES

env=$1
projectName=$2 #spring-boot


mvn clean package -Dmaven.test.skip=true

#### CONFIGURABLE VARIABLES ######
#destination absolute path. It must be pre created or you can

destAbsPath=$HOME/$projectName/$env
configFolder=src/main/resources

#jar file

sourFile=$HOME/biyeta_profile_recommendation_prod/biyeta_profile_recommendation/target/$projectName.jar
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


function stopServer(){
    echo " "
    echo "Stoping process on port: $serverPort"
    fuser -n tcp -k $serverPort > redirection &
    echo " "
}



# 1 - stop server on port ...
#stopServer

# 2 - delete destinations folder content
#deleteFiles

# 3 - copy files to deploy dir
#copyFiles

#run server
#run

#run the script from command line

#./tone_analyzer_prod_manual.sh prod tone-analyzer-0.0.1-SNAPSHOT



#journalctl  -u toneanalyzer-prod.service | tail -n 100
