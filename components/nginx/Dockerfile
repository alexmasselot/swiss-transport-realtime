FROM nginx
MAINTAINER Alexandre Masselot <alexandre.masselot@gmail.com>

COPY config/docker-fontend.json /usr/share/nginx/html/config.json
COPY config/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
