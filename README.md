# S1P-RabbitMQ-Streams-Workshop

Thsi project is using the docker-compose settings [docker-compose](https://dataflow.spring.io/docs/installation/local/docker/) with
modifications to docker-compose-rabbitmq.yml to upgrade to rabbitmq-3.9.  We start docker-compose in this manner:

```bash
DATAFLOW_VERSION=2.8.1 SKIPPER_VERSION=2.7.1 \
docker-compose -f ./docker-compose.yml -f ./docker-compose-prometheus.yml \
-f docker-compose-rabbitmq.yml up
```

The dataflow sample we chose was the analytics sample
[analytics](https://docs.spring.io/spring-cloud-dataflow-samples/docs/current/reference/htmlsingle/#_analytics). The goal was to
have this running in K8S but its much faster to work through issues in docker-compose than in k8s. There were also differences
in the version of the app-starters used between the two that have been aligned during the time we've been workin on this project.
The docker-compose was importing Einstein app-starters (v2.1.1) whereas k8s was importing v3.2.  However they are both importing v3
now,

The reason that was important was that v3 breaks the anlytics demo.  The v3 app-starters uses the new analytics sink.
In the analytics sink you specify that you want a counter metrics but it doesn't yield the same format so the
grafana dashboard.

A new version for 3.2 version of counter was checked into github so that it could be registered via a globally addressable
address and registered with dataflow-server. The steps within the dataflow-shell are:

The SCDF dashboard is found at localhost:9393.
The rabbitmq dashboard is found at localhost:15672

You will see that the new tweetlang queue is in fact a stream.  

```shell
app register --name counter --type sink --uri https://raw.githubusercontent.com/wlund-pivotal/S1P-RabbitMQ-Streams-Workshop/main/jars/counter-0.0.1-SNAPSHOT.jar
stream create tweetlang  --definition ":tweets.twitter-stream > counter --counter.name=language --spring.cloud.stream.rabbit.binding.consumer.container-type=stream --counter.tag.expression.lang=#jsonPath(payload,'$..lang')" --deploy
stream destroy tweetlang
stream create tweetlang  --definition ":tweets.twitter-stream > counter --counter.name=language --spring.cloud.stream.rabbit.binding.consumer.container-type=stream --counter.tag.expression.lang=#jsonPath(payload,'$..lang') --spring.rabbitmq.stream.host=dataflow-rabbitmq" --deploy
stream list
stream undeploy tweets
stream undeploy tweetlang
```






