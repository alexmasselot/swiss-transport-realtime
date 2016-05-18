FROM nginx
COPY index.html /usr/share/nginx/html/
COPY static /usr/share/nginx/html/static
COPY docker/docker-fontend.json /usr/share/nginx/html/config.json
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
