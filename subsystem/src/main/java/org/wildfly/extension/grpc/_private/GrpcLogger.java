/*
 * Copyright 2021 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.extension.grpc._private;

import static org.jboss.logging.Logger.Level.INFO;

import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "WFLYGRPC", length = 4)
public interface GrpcLogger extends BasicLogger {

    GrpcLogger LOGGER = Logger.getMessageLogger(GrpcLogger.class, "org.wildfly.extension.grpc");

    /**
     * Logs an informational message indicating the subsystem is being activated.
     */
    @LogMessage(level = INFO)
    @Message(id = 1, value = "Activating gRPC subsystem")
    void activatingSubsystem();

    @LogMessage(level = INFO)
    @Message(id = 3, value = "gRPC server listening on %s:%d")
    void serverListening(String address, int port);

    @LogMessage(level = INFO)
    @Message(id = 4, value = "gRPC server stopping")
    void serverStopping();

    @Message(id = 5, value = "Deployment %s requires use of the '%s' capability but it is not currently registered")
    DeploymentUnitProcessingException deploymentRequiresCapability(String deploymentName, String capabilityName);
}
