# WildFly gRPC

Feature pack to bring gRPC support to WildFly.

## Get Started

To build the feature pack, simply clone this repository, and on your command line go to the checkout folder and run

```shell
mvn install
```

This will build everything, and run the testsuite. An WildFly server with the gRPC subsystem will be created in
the `build/target` directory.

## Deploy Example

The `example` folder contains the 'Hello World' example from
the [gRPC Java examples](https://github.com/grpc/grpc-java/tree/master/examples) repository.

Start the server by running

```shell
./build/target/wildfly-<wildfly-version>-grpc-0.0.1/bin/standalone.sh
```

In another terminal window run:

```shell
mvn package wildfly:deploy -pl example
```

and see the application gets deployed.

You can use tools like [BloomRPC](https://github.com/uw-labs/bloomrpc)
or [gRPCurl](https://github.com/fullstorydev/grpcurl) to invoke the deployed 'Hello World' gRPC service:

```shell
grpcurl \
  -proto example/src/main/proto/helloworld.proto \
  -plaintext -d '{"name":"Bob"}' \
  localhost:9555 helloworld.Greeter/SayHello
```
