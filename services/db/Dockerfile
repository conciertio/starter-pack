FROM {{services.alpine.image}}

RUN apk add redis
ADD runner /runner
CMD [ "/runner" ]