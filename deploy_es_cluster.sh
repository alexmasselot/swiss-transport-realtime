#! /bin/bash

docker stop elasticsearch2 elasticsearch3
docker rm elasticsearch2 elasticsearch3

docker build --force-rm=true -t octoch/elasticsearch components/elasticsearch

docker run  -h elasticsearch2 --net=cff_realtime  -p 9202:9200 -p 9302:9300  -d --name elasticsearch2 --volumes-from elasticsearch_data octoch/elasticsearch elasticsearch \
     -Des.cluster.name="CFF_DataLake" -Des.node.name="ES_2" -Ddiscovery.zen.ping.multicast.enabled="false" \
     -Ddiscovery.zen.ping.unicast.hosts="elasticsearch1"

docker run  -h elasticsearch3 --net=cff_realtime  -p 9203:9200 -p 9303:9300  -d --name elasticsearch3 --volumes-from elasticsearch_data octoch/elasticsearch elasticsearch \
     -Des.cluster.name="CFF_DataLake" -Des.node.name="ES_3" -Ddiscovery.zen.ping.multicast.enabled="false" \
     -Ddiscovery.zen.ping.unicast.hosts="elasticsearch1"

echo -e "\nShow mount points: Elasticsearch"
docker inspect --format='{{json .Mounts}}' elasticsearch_data elasticsearch1 elasticsearch2 elasticsearch3
