#! /bin/bash

#Public IP
export AWS_IP=`echo $DOCKER_HOST | sed -E "s/.*\/([0-9\.]+):.*/\1/"`


wait_for_server() {
	name=$1
	ip=$2
	port=$3
	echo $ip $port
	while ! nc -z $ip $port; do
		echo "wait for $name"
		sleep 1
	done
}


echo "Cleaning existing container"
docker stop kafka elasticsearch1 elasticsearch2 elasticsearch3 cff_sniff logstash_stop logstash_position_es logstash_position_archive
docker rm  kafka elasticsearch1 elasticsearch2 elasticsearch3 cff_sniff logstash_stop logstash_position_es logstash_position_archive

echo "Now deploy new infrastructure"
### KAFKA : listen sur adresse interne et adresse publique (si le port est ouvert, ce qui n'est pas le cas chez Amazon)
docker build --force-rm=true -t octoch/kafka components/kafka

docker run --restart=always -d -p 2181:2181 -p 9092:9092 --name kafka -h kafka --net=cff_realtime --env KAFKA_HEAP_OPTS="-Xmx256M -Xms128M" --env ADVERTISED_HOST=$AWS_IP --env ADVERTISED_PORT=9092 --volumes-from kafka_data octoch/kafka



echo "\nShow mount points: KAFKA"

docker inspect --format='{{json .Mounts}}' kafka_data kafka

wait_for_server "Kafka/ZooKeeper" $AWS_IP 2181
wait_for_server "Kafka" $AWS_IP 9092
#docker exec -it kafka bash

# Test Kafka :
# docker run --rm=true -it --net=cff_realtime -w /opt/kafka_2.11-0.8.2.1/bin/ spotify/kafka ./kafka-console-consumer.sh --zookeeper kafka:2181 --topic cff_train_position

# docker run --rm=true -it --net=cff_realtime -w /opt/kafka_2.11-0.8.2.1/bin/ spotify/kafka bash
# ./kafka-console-producer.sh --broker-list kafka:9092 --topic test

#elastic search
docker build --force-rm=true -t octoch/elasticsearch components/elasticsearch
docker run  -h elasticsearch1 --net=cff_realtime  -p 9200:9200 -p 9300:9300  -d --name elasticsearch1 --volumes-from elasticsearch_data octoch/elasticsearch elasticsearch \
     -Des.cluster.name="CFF_DataLake" -Des.node.name="ES_1"	
echo -e "\nShow mount points: Elasticsearch"
docker inspect --format='{{json .Mounts}}' elasticsearch_data elasticsearch1

#logstash
docker build --force-rm=true -t octoch/logstash_data components/logstash_data

docker build --force-rm=true -t octoch/logstash_position_es components/logstash_position_es
docker run --restart=always  -h logstash_position_es --net=cff_realtime  -d --name logstash_position_es octoch/logstash_position_es -f /config-dir/logstash.conf

docker build --force-rm=true -t octoch/logstash_position_archive components/logstash_position_archive
docker run --restart=always -h logstash_position_archive --net=cff_realtime  -d --name logstash_position_archive --volumes-from logstash_data octoch/logstash_position_archive -f /config-dir/logstash.conf

docker build --force-rm=true -t octoch/logstash_stop components/logstash_stop
docker run --restart=always -h logstash_stop --net=cff_realtime  -d --name logstash_stop --volumes-from logstash_data octoch/logstash_stop -f /config-dir/logstash.conf

#sniffer
docker build --force-rm=true  -t octoch/cff_sniff components/node_cff_sniff
docker run --restart=always --env KAFKA_HOST=kafka --env MODE="$MODE" --net=cff_realtime -h cff_sniff -d --name cff_sniff octoch/cff_sniff

echo "Deployed successfully"
echo "Connect on : http://$AWS_IP"

