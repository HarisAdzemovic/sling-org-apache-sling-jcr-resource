/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.jcr.resource.internal.helper.jcr;

import static org.apache.sling.api.resource.ResourceMetadata.RESOLUTION_PATH;

import java.util.Calendar;

import javax.jcr.Item;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;

public class JcrPropertyResource implements Resource {

    private final String path;

    private final Property property;

    private final String resourceType;
    
    private final ResourceMetadata metadata;

    public JcrPropertyResource(String path, Property property)
            throws RepositoryException {
        this.path = path;
        this.property = property;
        this.resourceType = JcrNodeResource.getResourceTypeForNode(property.getParent())
            + "/" + property.getName();
        this.metadata = new ResourceMetadata();
        this.metadata.put(RESOLUTION_PATH, path);
    }

    public String getPath() {
        return path;
    }

    public ResourceMetadata getResourceMetadata() {
        return metadata;
    }

    public String getResourceType() {
        return resourceType;
    }

    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {

        // the property itself
        if (type == Property.class || type == Item.class) {
            return (AdapterType) property;
        }

        // the property value
        try {
            if (type == Boolean.class) {
                return (AdapterType) new Boolean(getProperty().getBoolean());
            } else if (type == Long.class) {
                return (AdapterType) new Long(getProperty().getLong());
            } else if (type == Double.class) {
                return (AdapterType) new Double(getProperty().getDouble());
            } else if (type == Calendar.class) {
                return (AdapterType) getProperty().getDate();
            } else if (type == Value.class) {
                return (AdapterType) getProperty().getValue();
            }
        } catch (ValueFormatException vfe) {
            // TODO: log
        } catch (RepositoryException re) {
            // TODO: log
        }
        
        // no adapter here
        return null;
    }

    public Property getProperty() {
        return property;
    }
}