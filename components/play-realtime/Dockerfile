FROM java:8
MAINTAINER amasselot@octo.com

#RUN ls -l .
#RUN  ./activator docker:stage

WORKDIR /opt/docker
ADD target/docker/stage/opt /opt
ADD target/docker/stage/opt/docker/conf/application-docker.conf /opt/docker/conf/application.conf

RUN ["chown", "-R", "daemon:daemon", "."]
EXPOSE 9000
USER daemon
ENTRYPOINT ["bin/cff-poc-streaming-backend"]
