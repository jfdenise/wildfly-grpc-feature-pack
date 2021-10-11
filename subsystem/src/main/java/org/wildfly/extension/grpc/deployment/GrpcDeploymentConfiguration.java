package org.wildfly.extension.grpc.deployment;

public interface GrpcDeploymentConfiguration {

    String getHost();

    int getPort();

    String getKeyManager();
}
