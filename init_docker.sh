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
docker rm -f -v kafka kafka_data \
     cff_sniff \
     logstash_data logstash_stop logstash_position_es logstash_position_archive \
     elasticsearch1 elasticsearch2 elasticsearch3 elasticsearch_data
     
docker network rm cff_realtime


echo "Now create new infrastructure"
docker network create -d bridge cff_realtime

# Sauvegarde des données Kafka
docker build --force-rm=true -t octoch/kafka components/kafka
docker run --name kafka_data -h kafka_data octoch/kafka echo "Data for kafka"

# Sauvegarde des données elasticsearch
docker build --force-rm=true -t octoch/elasticsearch components/elasticsearch
docker run  -h elasticsearch_data --name elasticsearch_data  --entrypoint='echo' octoch/elasticsearch "Data for ES"
#docker run --rm -it --volumes-from elasticsearch -w /usr/share/elasticsearch octoch/elasticsearch bash

# Sauvegarde des données brutes
docker build --force-rm=true -t octoch/logstash_data components/logstash_data
docker run  -h logstash_data --name logstash_data  --entrypoint='echo' octoch/logstash_data "Data for logstash archive"
#docker run --rm -it --volumes-from logstash_data -w /opt/logstash/data octoch/logstash_data bash

#backup 
#docker run --rm --volumes-from elasticsearch_data -v /Users/pkernevez/Downloads/:/backup debian tar czvf /backup/backup.tar /usr/share/elasticsearch/