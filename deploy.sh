#! /bin/sh

#Public IP
AWS_IP=`echo $DOCKER_HOST | sed -E "s/.*\/([0-9\.]+):.*/\1/"`


echo "Cleaning existing container"
docker rm -f kafka cff_sniff logstash 


echo "Now deploy new infrastructure"

### KAFKA
docker build -t octoch/kafka components/kafka
docker run -d -p 2181:2181 -p 9092:9092 --name kafka -h kafka --net=cff_realtime --env KAFKA_HEAP_OPTS="-Xmx256M -Xms128M" --env ADVERTISED_HOST=kafka --env ADVERTISED_PORT=9092 --volumes-from kafka_data octoch/kafka
#docker exec -it kafka bash

# Test Kafka :
# docker run --rm=true -it --net=cff_realtime -w /opt/kafka_2.11-0.8.2.1/bin/ spotify/kafka bash
# ./kafka-console-consumer.sh --zookeeper kafka:2181 --topic cff_train_position

# docker run --rm=true -it --net=cff_realtime -w /opt/kafka_2.11-0.8.2.1/bin/ spotify/kafka bash
# ./kafka-console-producer.sh --broker-list kafka:9092 --topic test

#build
docker build -t alexmass/cff_sniff .
docker run   --env KAFKA_HOST=kafka --env MODE=$MODE --net=cff_realtime -h cff_sniff -d --name cff_sniff alexmass/cff_sniff

#elastic search
docker build -t elasticsearch .
docker run  -h elasticsearch --net=cff_realtime  -p 9200:9200 -p 9300:9300  -d --name elasticsearch  elasticsearch

#elasticsearch
docker build -t logstash .
docker run  -h logstash --net=cff_realtime  -d --name logstash -v "$PWD/conf":/config-dir logstash -f /config-dir/logstash.conf


echo "Deployed successfully"
echo "Connect on : http://$AWS_IP"
