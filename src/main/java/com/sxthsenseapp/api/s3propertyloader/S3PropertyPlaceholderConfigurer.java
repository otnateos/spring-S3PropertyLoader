package com.sxthsenseapp.api.s3propertyloader;

/*
 * #%L
 * S3Property Loader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 SixthSenseApp
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

public class S3PropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {


    private S3ResourceLoader resourceLoader;
    private String[] s3Locations = new String[0];
    private Resource[] conventionalResources = new Resource[0];

    public S3PropertyPlaceholderConfigurer() {
        resourceLoader = new S3ResourceLoader();
    }

    public S3PropertyPlaceholderConfigurer(S3ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void setLocations(Resource[] locations) {
        this.conventionalResources = locations;
    }

    public void setS3Locations(String[] s3Locations) {
        this.s3Locations = new String[s3Locations.length];
        for (int i = 0; i < s3Locations.length; i++) {
            this.s3Locations[i] = parseStringValue(s3Locations[i],
                    new Properties(),
                    new HashSet());
        }

    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        injectS3Resources();
        super.postProcessBeanFactory(beanFactory);
    }

    private void injectS3Resources() {

        int total = conventionalResources.length + s3Locations.length;

        if (total > 0) {
            List<Resource> allResources = new ArrayList<Resource>();
            for (Resource conventionalResource : conventionalResources) {
                allResources.add(conventionalResource);
            }
            for (String s3Location : s3Locations) {
                allResources.add(resourceLoader.getResource(s3Location));
            }
            super.setLocations(allResources.toArray(new Resource[0]));
        }

    }
}
