FROM {{services.alpine.image}}

RUN apk add nginx
ADD bb /usr/bin/bb
WORKDIR /balancer

RUN mkdir /balancer/logs
ADD nginx_tmpl /balancer
ADD run /balancer

EXPOSE {{globals.BALANCER_PORT}}

CMD [ "/balancer/run" ]


