FROM logstash:2.1
RUN cd /opt/logstash && bin/plugin install --version 2.0.3 logstash-input-kafka


RUN set -x && \
   mkdir /opt/logstash/data && \
   chmod o+w /opt/logstash/data 
VOLUME /opt/logstash/data