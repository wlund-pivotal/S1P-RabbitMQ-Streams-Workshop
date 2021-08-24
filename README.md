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

The reason that was important was that v3 breaks the anlytics demo.  The v3 app-starters uses the new analytics sink that
I was unaware of.  In the analytics sink you specify that you want a counter but it doesn't yield the same format so the
grafana dashboard.  That led through cycles with Christian Tzolov and they said we should import the Einstein counter, which
shows 2 of the three panels but would have been good enough.

Gary provided a 3.2 version of counter, the point we're flailing now, and dataflow server throws an error when we try to deploy
tweets.  If you look at the spring-shell.log you can see everything we've tried to do. 


