FROM logstash

RUN cd /opt/logstash && bin/plugin install --version 2.0.3 logstash-input-kafka
ADD conf /config-dir

