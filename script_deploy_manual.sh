#!/usr/bin/env bash

sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
echo "deb http://repo.mongodb.org/apt/ubuntu "$(lsb_release -sc)"/mongodb-org/3.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.2.list
sudo apt-get update
sudo apt-get install -y mongodb-org
sudo apt-get update
sudo apt-get upgrade
wget http://packages.erlang-solutions.com/ubuntu/erlang_solutions.asc
sudo apt-key add erlang_solutions.asc
sudo apt-get update


wget https://packages.erlang-solutions.com/erlang-solutions_1.0_all.deb
sudo dpkg -i erlang-solutions_1.0_all.deb
sudo apt-get update
sudo apt-get install erlang erlang-nox
echo 'deb http://www.rabbitmq.com/debian/ testing main' | sudo tee /etc/apt/sources.list.d/rabbitmq.list
wget -O- https://www.rabbitmq.com/rabbitmq-release-signing-key.asc | sudo apt-key add -
sudo apt-get update
sudo apt-get install rabbitmq-server
sudo service rabbitmq-server start

sudo rabbitmq-plugins enable rabbitmq_management
sudo rabbitmq-plugins enable rabbitmq_web_stomp
sudo apt install redis-server
sudo apt-get install default-jre
sudo apt-get install default-jdk

aws s3 cp tone-analyzer-0.0.1-SNAPSHOT.jar s3://mozammal-bucket/tone-analyzer-0.0.1-SNAPSHOT.jar
fuser -n tcp -k 8080
rm -f toneanalyzer.log
rm -f  tone-analyzer-0.0.1-SNAPSHOT.jar
aws s3 cp s3://mozammal-bucket/tone-analyzer-0.0.1-SNAPSHOT.jar /home/ubuntu/tone-analyzer-0.0.1-SNAPSHOT.jar
sudo nohup java -jar tone-analyzer-0.0.1-SNAPSHOT.jar > toneanalyzer.log&
