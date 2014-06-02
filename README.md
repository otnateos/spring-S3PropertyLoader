[![Build Status](https://travis-ci.org/aletheia/spring-S3PropertyLoader.svg?branch=master)](https://travis-ci.org/aletheia/jSOAM)


# README #

S3 Property Loader has the aim of allowing loading of Spring property files from S3 bucket, in order to guarantee stateless machine configuration.

Spring PropertyConfigurer replaces standard PropertyConfigurer to load property files from AWS S3 bucket. S3 path could be specified as environment variable or directly into spring beans xml file.

## Loading properties from amazon S3 buckets

A typic cloud deployment is thought for scaling. This means deployment machines should be stateless, even if we will use spring webframework to wire up the context of the application. 

Usually it involves set configuration values in application context xml, such as database credentials, dependent service endpoints. 

One way to resolve this is to either directly set values in spring context or 
use properties files within classpath. However the idea that embeds 
configuration within application itself is generally not flexible. Considered
an application may have different deployment environment such as production,
integration and development. In each environment, it is desire and sometime
even must have different configuration values.

In an non-stateless deployment, we could just load properties files from file
system or http url if this is inside secure network.
But for AWS cloud deploy, we ideally don't want to open our configuration parameters to the whole WWW or go through specific EC2 instances configurations of security settings.
The most straightforward operation would be defining a couple of IAM credentials and one or more S3 locations where property files could be found.

### How can this be used? ###
This JAR can be easily dropped into your project and used as a substitution for PropertyPlaceholderConfigurer, but our preferred way of managing is through Maven build system.

```xml
<groupId>com.sixthsenseapp.libs</groupId>
<artifactId>s3-property-loder</artifactId>
<version>1.0.0</version>
```


### How to use ###

`S3PropertyPlaceholderConfigurer` is a plumber to load properties from an
private S3 bucket in spring context. 

Assuming you have spring context to load properties from classpath like this:

```
<bean id="propertyConfigurer" class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
  <property name="locations">
    <list>
      <value>classpath:app.properties</value>
      <value>classpath:another.properties</value>
    </list>
  <property>
</bean>
```

To load same properties file from a s3 bucket `config/production/app.properties`
You need generate an IAM with aws access key id and secret key, and in 
beanstalk > {you app} > {your environment} > configuration > container, set
S3_CONFIG_AWS_ACCESS_KEY_ID to the aws access key and S3_CONFIG_AWS_SECRET_KEY to the aws secure
key. 

Then update context to be

```
<bean id="propertyConfigurer" class="org.longhorn.beanstalk.springintegration.config.S3PropertyPlaceholderConfigurer">
  <property name="locations">
    <list>
      <value>classpath:another.properties</value>
    </list>
  <property>
  <property name="s3Locations">
    <list>
      <value>s3://config/production/app.properties</value>
    </list>
  </property>
</bean>
```

Noticed that you can use load properties files as before.

You can also set up a S3ResourceLoader, in order to pass ACCESS and SECRET keys. This can be easily achieved just defining

```xml
<bean id="resourceLoader" class="com.sxthsenseapp.api.s3propertyloader.S3ResourceLoader">
        <constructor-arg name="awsAccessKey" value="<YOUR_AWS_ACCESS_KEY>"/>
        <constructor-arg name="awsSecretKey" value="<YOUR_AWS_SECRET_KEY>"/>
</bean>
```

You can even specify location of S3 document from an environment variable. To add this, you should easily define a s£Location list value with the name of the ENV property. Each ENV property must start with S3_CONFIG_RES_ prefix.

```xml
    <bean id="propertyConfigurer" class="com.sxthsenseapp.api.s3propertyloader.S3PropertyPlaceholderConfigurer">
        <constructor-arg ref="resourceLoader"/>
        <property name="s3Locations">
            <list>
                <value>S3_CONFIG_RES_MY_LOCATION</value>
            </list>
        </property>
    </bean>
```
this can be very helpful when dealing with stage/production environments.

## Future improvements
* Add support for DynamoDB property locations: using a DynamoDB table as property storage
* Add support for external REST service property location: using an external REST interface to get properties
