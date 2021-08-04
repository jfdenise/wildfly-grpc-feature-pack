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

import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.descriptions.ResourceDescriptionResolver;
import org.jboss.as.controller.descriptions.StandardResourceDescriptionResolver;
import org.jboss.as.controller.operations.common.GenericSubsystemDescribeHandler;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

public class GrpcExtension implements Extension {

    /**
     * The name of our subsystem within the model.
     */
    public static final String SUBSYSTEM_NAME = "grpc";

    protected static final PathElement SUBSYSTEM_PATH = PathElement.pathElement(SUBSYSTEM, SUBSYSTEM_NAME);

    static final String WELD_CAPABILITY_NAME = "org.wildfly.weld";

    private static final String RESOURCE_NAME = GrpcExtension.class.getPackage().getName() + ".LocalDescriptions";

    protected static final ModelVersion VERSION_1_0_0 = ModelVersion.create(1, 0, 0);
    private static final ModelVersion CURRENT_MODEL_VERSION = VERSION_1_0_0;

    private static final SubsystemParser_1_0 CURRENT_PARSER = new SubsystemParser_1_0();

    @Override
    public void initialize(ExtensionContext extensionContext) {
        SubsystemRegistration sr = extensionContext.registerSubsystem(SUBSYSTEM_NAME, CURRENT_MODEL_VERSION);
        sr.registerXMLElementWriter(CURRENT_PARSER);
        ManagementResourceRegistration root = sr.registerSubsystemModel(GrpcSubsystemDefinition.INSTANCE);
        root.registerOperationHandler(
                GenericSubsystemDescribeHandler.DEFINITION,
                GenericSubsystemDescribeHandler.INSTANCE,
                false
        );
    }

    @Override
    public void initializeParsers(ExtensionParsingContext extensionParsingContext) {
        extensionParsingContext.setSubsystemXmlMapping(
                SUBSYSTEM_NAME,
                SubsystemParser_1_0.NAMESPACE,
                CURRENT_PARSER
        );
    }

    static ResourceDescriptionResolver getResourceDescriptionResolver(final String... keyPrefix) {
        return getResourceDescriptionResolver(true, keyPrefix);
    }

    static ResourceDescriptionResolver getResourceDescriptionResolver(final boolean useUnprefixedChildTypes,
                                                                      final String... keyPrefix) {
        StringBuilder prefix = new StringBuilder();
        for (String kp : keyPrefix) {
            if (prefix.length() > 0) {
                prefix.append('.');
            }
            prefix.append(kp);
        }
        return new StandardResourceDescriptionResolver(
                prefix.toString(),
                RESOURCE_NAME,
                GrpcExtension.class.getClassLoader(),
                true,
                useUnprefixedChildTypes
        );
    }
}
