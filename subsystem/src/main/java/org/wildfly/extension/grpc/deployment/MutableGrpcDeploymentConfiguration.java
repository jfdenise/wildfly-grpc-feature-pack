/*
 *  Copyright 2022 Red Hat, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wildfly.extension.grpc.deployment;

public class MutableGrpcDeploymentConfiguration implements GrpcDeploymentConfiguration {

    private String host;
    private int port;
    private String keyManager;

    void setHost(final String host) {
        this.host = host;
    }

    @Override
    public String getHost() {
        return host;
    }

    void setPort(final int port) {
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }

    void setKeyManager(final String keyManager) {
        this.keyManager = keyManager;
    }

    @Override
    public String getKeyManager() {
        return keyManager;
    }
}
