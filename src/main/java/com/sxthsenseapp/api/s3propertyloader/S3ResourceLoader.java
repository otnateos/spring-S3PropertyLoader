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

import lombok.extern.slf4j.Slf4j;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

@Slf4j
public class S3ResourceLoader implements ResourceLoader {

    private static final String LOCATION_PREFIX = "s3://";
    private static final String ENVIRONMENT_VARIABLE_PREFIX = "S3_CONFIG_RES_";
    
    public static class S3Path {
        public String bucket;
        public String key;
    };

    private String awsAccessKey;
    private String awsSecretKey;
    private AWSCredentials credentials;
    private S3Service s3Service;

    public S3ResourceLoader( ) {
        this.awsAccessKey = getRequiredSystemProperty( "S3_CONFIG_AWS_ACCESS_KEY_ID" );
        this.awsSecretKey = getRequiredSystemProperty( "S3_CONFIG_AWS_SECRET_KEY" );
        this.setupAWSCredentials();
    }

    public S3ResourceLoader(String awsAccessKey, String awsSecretKey){
        this.awsAccessKey = awsAccessKey;
        this.awsSecretKey = awsSecretKey;
        this.setupAWSCredentials();
    }

    private void setupAWSCredentials(){
        this.credentials = new AWSCredentials( awsAccessKey, awsSecretKey );
        try {
            this.s3Service = new RestS3Service( credentials );
        } catch( S3ServiceException e ) {
            throw new S3ResourceException( "could not initialize s3 service", e );
        }

    }

    public ClassLoader getClassLoader() {
        return this.getClassLoader();
    }

    public Resource getResource( String location ) {
        try {
            S3Path s3Path = parseS3Path( location );
            S3Object s3Object = s3Service.getObject( s3Path.bucket, s3Path.key );
            byte[] buf = readS3Object( s3Object );
            return new ByteArrayResource( buf , location );

        } catch ( Exception e ) {
            throw new S3ResourceException( "could not load resource from " + location, e );
        }
    }

    private String getRequiredSystemProperty( String propertyName ) {
        String value = System.getProperty( propertyName );
        if ( value == null || "".equals( value.trim() ) ) {
            throw new S3ResourceException( "no " + propertyName + " property found in system" );
        }
        return value;
    }

    private S3Path parseS3Path( String location ) {

        String path = getLocationPath( location );
        String bucketName = path.substring(0,path.indexOf("/"));
        String keyName = path.substring(path.indexOf("/")+1);

        log.debug("S3 Resource from bucket: "+bucketName+" and key: "+keyName);


        S3Path s3Path = new S3Path( );

        s3Path.bucket = bucketName;
        s3Path.key = keyName;
        return s3Path;
    }

    private String getLocationPath( String location ) {

        if ( location == null || "".equals( location.trim() ) ) {
            throw new S3ResourceException( "location cannot be empty or null" );
        }

        String resolvedLocation = location;

        if ( resolvedLocation.startsWith( LOCATION_PREFIX ) ) {
            return resolvedLocation.substring( LOCATION_PREFIX.length(), resolvedLocation.length() );
        }else if(resolvedLocation.startsWith(ENVIRONMENT_VARIABLE_PREFIX)){
            return getRequiredSystemProperty(resolvedLocation);
        }else{
            throw new S3ResourceException( resolvedLocation + " does not begin with " + LOCATION_PREFIX );
        }
        

        
    }
    private byte[] readS3Object( S3Object s3Object ) throws Exception {
        InputStream inputStream = s3Object.getDataInputStream();
        int size = 1024;
        byte[] buf = new byte[ size ];
        
        int readedSize = inputStream.read( buf );
        while ( readedSize == 1024 ) {
            byte[] tmpBuf = new byte[ size ];
            readedSize = inputStream.read( tmpBuf );
            byte[] newBuf = new byte[ buf.length + readedSize ];
            System.arraycopy( buf, 0, newBuf, 0, buf.length );
            System.arraycopy( tmpBuf, 0, newBuf, buf.length, readedSize );
            buf = newBuf;
        }
        return buf;
    }
}
