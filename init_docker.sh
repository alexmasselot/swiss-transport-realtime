#! /bin/bash

# First create a docker-machine named "aws" for the deployment
# 
# For EC2 :
#   export AWS_ACCESS_KEY_ID=<Secret> (see https://console.aws.amazon.com/iam/home#users)
#   export AWS_SECRET_ACCESS_KEY=<Super_Top_Secret> (see https://console.aws.amazon.com/iam/home#users)
#   export AWS_VPC_ID=<YourVPC> (see https://console.aws.amazon.com/vpc/home)
#   export AWS_REGION=<your region> (ex: us-west-2 , see url at https://console.aws.amazon.com/vpc/home)
#   The AWS account need at least those actions : "execute-api:invoke" and "ec2:*" (see https://console.aws.amazon.com/iam/home#policies)
# 
#   docker-machine create --driver amazonec2 --amazonec2-access-key $AWS_ACCESS_KEY_ID --amazonec2-secret-key $AWS_SECRET_ACCESS_KEY --amazonec2-vpc-id $AWS_VPC_ID --amazonec2-region $AWS_REGION --amazonec2-instance-type t2.small aws
#   eval (docker-machine env aws)
# 
# Open http (80) port on your docker-machine : Inbound of security group in https://console.aws.amazon.com/ec2/v2/home


echo "Cleaning existing containers if exist"
docker rm -f kafka kafka_data cff_sniff logstash elasticsearch
docker network rm cff_realtime


echo "Now create new infrastructure"
docker network create -d bridge cff_realtime

# Sauvegarde des données Kafka
docker build -t octoch/kafka components/kafka
docker run --name kafka_data -h kafka_data octoch/kafka echo "Data for kafka"

# Sauvegarde des données alesticsearch
docker build -t octoch/elasticsearch components/elasticsearch
docker run  -h elasticsearch_data --name elasticsearch_data  octoch/elasticsearch echo "Data for ES"
#docker run  -h elasticsearch_data --name elasticsearch_data  --volumes-from elasticsearch octoch/elasticsearch echo "Data for ES"
#backup 
#docker run --rm --volumes-from elasticsearch_data -v /Users/pkernevez/Downloads/:/backup debian tar czvf /backup/backup.tar /usr/share/elasticsearch/