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
package org.wildfly.extension.grpc;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.ModelOnlyRemoveStepHandler;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.as.controller.registry.ManagementResourceRegistration;

import java.util.Collection;
import java.util.Collections;

import static org.wildfly.extension.grpc.GrpcExtension.WELD_CAPABILITY_NAME;

public class GrpcSubsystemDefinition extends PersistentResourceDefinition {

    private static final String GRPC_CAPABILITY_NAME = "org.wildfly.grpc";

    static final RuntimeCapability<Void> GRPC_CAPABILITY = RuntimeCapability.Builder
            .of(GRPC_CAPABILITY_NAME, false, GrpcServerService.class)
            .build();

    static final GrpcSubsystemDefinition INSTANCE = new GrpcSubsystemDefinition();

    public GrpcSubsystemDefinition() {
        super(new SimpleResourceDefinition.Parameters(
                GrpcExtension.SUBSYSTEM_PATH,
                GrpcExtension.getResourceDescriptionResolver(GrpcExtension.SUBSYSTEM_NAME))
                .setAddHandler(GrpcSubsystemAdd.INSTANCE)
                .setRemoveHandler(new ModelOnlyRemoveStepHandler())
                .setCapabilities(GRPC_CAPABILITY)
        );
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Collections.emptyList();
    }

    @Override
    public void registerAdditionalRuntimePackages(ManagementResourceRegistration resourceRegistration) {
        super.registerAdditionalRuntimePackages(resourceRegistration);
        //TODO - If your feature-pack needs any other modules, you should add those here, and remove the line above
        /*
        resourceRegistration.registerAdditionalRuntimePackages(
                // Required dependencies are always added
                RuntimePackageDependency.required("my.required.module"),
                // Optional and passive modules depend on the 'optional-packages' mode. See the Galleon
                // documentation for more details as this is an advanced feature
                RuntimePackageDependency.optional("my.optional.module"),
                RuntimePackageDependency.passive("my.passive.module")
        );*/
    }
}
