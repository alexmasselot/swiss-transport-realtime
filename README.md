#CFF Big Data POC

##Dev

### Install

#### pre requisites

 * Docker toolbox (1.10) - the terminal version is enough
 * Java 8
 * nodeJS (nvm)
 
 
### Launching a  mock feed (for offline evelopement)

To avoid launching the full stack as described below, or being connected to the amazon kafka, one can cycle through a few historical data. A node js script read those files a send event to a local kafka, as if they where coming from the real cff sniffer. The pace is multiplied a factor (10 is default) to have a more compfortable dev environnment.

A docker compose is ready to be fired

    cd devtools/cff-mock-feeder
	docker-compose build
	docker-compose up

*But what happens when some container fails?*
That is typically the case when your dev laptop goes out of network for a while. The code could certainy be more robust, but for the moment:

    docker-compose  restart
	docker logs --tail=100 -f cffmockfeeder_cff-mock-feeder_1
	
And if you're stunned unde LeaderNotAvailable jibber jabber, just ^C and redo...


### Developing the real time viz

Once the mock feeder is up, we must start two components

#### The streaming app
The scala/akka/kafka/play app that reads the position/station board stream, aggregate, decorate them and expose them via REST API.

    cd components/play-realtime
	./activator ~run
	

Check it out via a couple of calls to http://localhost:9000/position/snapshot (let a few seconds to warm up)


#### The web frontend

a nodejs + express + react/redux

    cd components/web-realtime-viewer
	nvm use 4.2 #to use a recent nodejs
	npm start
	
And head to http://localhost:3000

 
### Launch the full stack (well, without the real time viz yet)

    eval $(docker-machine env default)
	#create at once all the docker containers
	./init_docker.sh 
	
And launch the full machines
   
    ./deploy.sh
	
Check what's up?

    docker ps -a
	
And you should see

	CONTAINER ID        IMAGE                              COMMAND                  CREATED             STATUS                     PORTS                                            NAMES
	f31ab47ecec5        octoch/cff_sniff                   "npm start"              11 seconds ago      Up 11 seconds                                                               cff_sniff
	ca5246f84b16        octoch/logstash_stop               "/docker-entrypoint.s"   2 minutes ago       Up 2 minutes                                                                logstash_stop
	a65f020241b4        octoch/logstash_position_archive   "/docker-entrypoint.s"   2 minutes ago       Up 2 minutes                                                                logstash_position_archive
	c87700158398        octoch/logstash_position_es        "/docker-entrypoint.s"   2 minutes ago       Up 2 minutes                                                                logstash_position_es
	39dce32267c2        octoch/elasticsearch               "/docker-entrypoint.s"   2 minutes ago       Up 2 minutes               0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp   elasticsearch1
	182c32d29d38        octoch/kafka                       "supervisord -n"         2 minutes ago       Up 2 minutes               0.0.0.0:2181->2181/tcp, 0.0.0.0:9092->9092/tcp   kafka
	0f51f449871a        octoch/logstash_data               "echo 'Data for logst"   4 minutes ago       Exited (0) 4 minutes ago                                                    logstash_data
	a1cac8d9b7bc        octoch/elasticsearch               "echo 'Data for ES'"     5 minutes ago       Exited (0) 5 minutes ago                                                    elasticsearch_data
	a53080435858        octoch/kafka                       "echo 'Data for kafka"   6 minutes ago       Exited (0) 6 minutes ago                                                    kafka_data
	

#### Docker dev troubleshooting

Check the logs to see if a container is running

    docker logs cff_sniff

If you change network, DNS/resolv might die

	docker-machine ssh default
	echo "nameserver 8.8.8.8" > /etc/resolv.conf && sudo /etc/init.d/docker restart

#amazon
If you have a local docker 1.10 and some complaint about and client versions incompatibilities. You can stil fix it with 

    export DOCKER_API_VERSION=1.21

Then rock with docker

    eval $(docker-machine env aws3)
    docker stats `docker ps | awk '{print $NF}' | grep -v NAMES`
	

#Play with data

##Copying back data files
You wanna play with data on your laptop. Let's get them back in a barabarian way, for one day (Ok, I don't know how to have wildcards with `docker cp`)

    for i in 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23; do
	  echo $i
	  docker cp logstash_position_archive:/opt/logstash/data/stop/cff-stop-2016-02-14_$i.txt.gz ~/tmp/logstash_position_archive-stops/
	done


