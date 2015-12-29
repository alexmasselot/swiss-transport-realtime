#! /bin/sh

#Public IP
AWS_IP=`echo $DOCKER_HOST | sed -E "s/.*\/([0-9\.]+):.*/\1/"`


echo "Cleaning existing container"
docker rm -f kafka cff_sniff flume


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

#flume
# cd components/flume-dump
docker build -t octoch/flume components/flume-dump
docker run -e FLUME_AGENT_NAME=agent_kafka_dump -e FLUME_CONF_FILE=/var/tmp/flume.conf -h flume --net=cff_realtime  -d --name flume octoch/flume

# SNIFFER
docker build -t octoch/cff_sniff components/node_cff_realtime_sniffer
docker run --env KAFKA_HOST=kafka --env MODE=$MODE --net=cff_realtime -h cff_sniff -d --name cff_sniff octoch/cff_sniff

echo "Deployed successfully"
echo "Connect on : http://$AWS_IP"
