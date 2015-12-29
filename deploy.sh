#! /bin/sh

KAFKA_VERSION=8

# First create a docker-machine named "aws" for the deployment
# 
# For EC2 :
#   export AWS_ACCESS_KEY_ID=<Secret> (see https://console.aws.amazon.com/iam/home#users)
#   export AWS_SECRET_ACCESS_KEY=<Super_Top_Secret> (see https://console.aws.amazon.com/iam/home#users)
#   export AWS_VPC_ID=<YourVPC> (see https://console.aws.amazon.com/vpc/home)
#   export AWS_REGION=<your region> (ex: us-west-2 , see url at https://console.aws.amazon.com/vpc/home)
# 
#   The AWS account need at least those actions : "execute-api:invoke" and "ec2:*" (see https://console.aws.amazon.com/iam/home#policies)
# 
#   docker-machine create --driver amazonec2 --amazonec2-access-key $AWS_ACCESS_KEY_ID --amazonec2-secret-key $AWS_SECRET_ACCESS_KEY --amazonec2-vpc-id $AWS_VPC_ID --amazonec2-region $AWS_REGION --amazonec2-instance-type t2.small aws
#   eval (docker-machine env aws)
# 
# Open http (80) port on your docker-machine : Inbound of security group in https://console.aws.amazon.com/ec2/v2/home


# for development
# export MODE=DEV

######################################
# Now redeploy to the docker-machine
######################################

#Public IP
AWS_IP=`echo $DOCKER_HOST | sed -E "s/.*\/([0-9\.]+):.*/\1/"`


echo "Cleaning existing container"
#docker rm -f 
docker rm -f kafka
docker network rm cff_realtime





echo "Now deploy new infrastructure"
docker network create -d bridge cff_realtime

docker run -d -p 2181:2181 -p 9092:9092 --name kafka -h kafka --net=cff_realtime --env KAFKA_HEAP_OPTS="-Xmx256M -Xms128M" --env ADVERTISED_HOST=kafka --env ADVERTISED_PORT=9092 spotify/kafka
#docker run -d -p 2181:2181 -p 9092:9092 --name kafka --net=cff_realtime --env KAFKA_HEAP_OPTS="-Xmx256M -Xms128M" --env ADVERTISED_HOST=`docker-machine ip \`docker-machine active\`` --env ADVERTISED_PORT=9092 spotify/kafka
#docker exec -it kafka bash

# Test Kafka :
# docker run --rm=true -it --net=cff_realtime -w /opt/kafka_2.11-0.8.2.1/bin/ spotify/kafka bash
# ./kafka-console-consumer.sh --zookeeper kafka:2181 --topic cff_train_position

# docker run --rm=true -it --net=cff_realtime -w /opt/kafka_2.11-0.8.2.1/bin/ spotify/kafka bash
# ./kafka-console-producer.sh --broker-list kafka:9092 --topic test

#build
docker build -t alexmass/cff_sniff .
docker run   --env KAFKA_HOST=kafka --env MODE=$MODE --net=cff_realtime -h cff_sniff -d --name cff_sniff alexmass/cff_sniff

echo "Deployed successfully"
echo "Connect on : http://$AWS_IP"