#! /bin/bash -e

./set-env.sh
sudo mvn clean package docker:build
sudo docker-compose up -d
sudo docker logs -f tone-analyzer-web