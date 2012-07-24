openid4java-memcached-association-store
=======================================

OpenID4Java MemcachedAssociationStore implementation.

## OpenID4Java

http://code.google.com/p/openid4java/


## pom.xml

```xml
<dependency>
    <groupId>com.m3</groupId>
    <artifactId>openid4java-memcached-association-store</artifactId>
    <version>1.0</version>
</dependency>
```

## Usage

```java
List<InetSocketAddress> memcachedServers = Arrays.asList(new InetSocketAddress("localhost", 11211);
ServerAssociationStore associationStore = new MemcachedServerAssociationStore(memcachedServers);

ServerManager manager = new ServerManager();
manager.setSharedAssociations(associationStore);
manager.setPrivateAssociations(associationStore);
```

## License 

Apache License, Version 2.0

http://www.apache.org/licenses/LICENSE-2.0.html

