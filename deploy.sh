#! /bin/bash

#Public IP
export AWS_IP=`echo $DOCKER_HOST | sed -E "s/.*\/([0-9\.]+):.*/\1/"`


echo "Cleaning existing container"
docker rm -f kafka cff_sniff logstash elasticsearch


echo "Now deploy new infrastructure"

### KAFKA : listen sur adresse interne et adresse publique (si le port est ouvert, ce qui n'est pas le cas chez Amazon)
docker build -t octoch/kafka components/kafka
docker run -d -p 2181:2181 -p 9092:9092 --name kafka -h kafka --net=cff_realtime --env KAFKA_HEAP_OPTS="-Xmx256M -Xms128M" --env ADVERTISED_HOST=$AWS_IP --env ADVERTISED_PORT=9092 --volumes-from kafka_data octoch/kafka
echo "\nShow mount points: KAFKA"
docker inspect --format='{{json .Mounts}}' kafka_data kafka
#docker exec -it kafka bash

# Test Kafka :
# docker run --rm=true -it --net=cff_realtime -w /opt/kafka_2.11-0.8.2.1/bin/ spotify/kafka ./kafka-console-consumer.sh --zookeeper kafka:2181 --topic cff_train_position

# docker run --rm=true -it --net=cff_realtime -w /opt/kafka_2.11-0.8.2.1/bin/ spotify/kafka bash
# ./kafka-console-producer.sh --broker-list kafka:9092 --topic test

#elastic search
docker build -t octoch/elasticsearch components/elasticsearch
docker run  -h elasticsearch --net=cff_realtime  -p 9200:9200 -p 9300:9300  -d --name elasticsearch --volumes-from elasticsearch_data octoch/elasticsearch elasticsearch -Des.node.name="CFF_DataLake"
echo "\nShow mount points: Elasticsearch"
docker inspect --format='{{json .Mounts}}' elasticsearch_data elasticsearch

#logstash
docker build -t octoch/logstash components/logstash
docker run  -h logstash --net=cff_realtime  -d --name logstash octoch/logstash -f /config-dir/logstash.conf

#sniffer
docker build -t octoch/cff_sniff components/node_cff_realtime_sniffer
docker run --env KAFKA_HOST=kafka --env MODE="$MODE" --net=cff_realtime -h cff_sniff -d --name cff_sniff octoch/cff_sniff


echo "Deployed successfully"
echo "Connect on : http://$AWS_IP"
