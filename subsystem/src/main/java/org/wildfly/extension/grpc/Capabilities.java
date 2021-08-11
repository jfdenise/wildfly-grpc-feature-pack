package org.wildfly.extension.grpc;

public interface Capabilities {

    // gRPC capabilities
    String CAPABILITY_GRPC = "org.wildfly.grpc";
    String CAPABILITY_SERVER = "org.wildfly.grpc.server";

    // references to other capabilities
    String REF_SOCKET_BINDING = "org.wildfly.network.socket-binding";
    String REF_WELD = "org.wildfly.weld";
}
