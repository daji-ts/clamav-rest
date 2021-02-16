Simple [ClamAV](http://www.clamav.net/) REST proxy. Builds on top of [clamav-java](https://github.com/solita/clamav-java) which is a minimal Java client for ClamAV.

[![Build Status](https://travis-ci.org/solita/clamav-rest.svg?branch=master)](https://travis-ci.org/solita/clamav-rest)

# What is it?

## The big picture

This is an example for the deployment. You could omit the log server, it's completely optional.

![Deployment example](img/virusscanner-deployment.png)

For more general information, see also [our blog post](http://dev.solita.fi/2015/06/02/rest-virusscan.html).

## The technical details

This is a REST proxy server with support for basic INSTREAM scanning and PING command. 

Clamd protocol is explained here:
http://linux.die.net/man/8/clamd

Clamd protocol contains command such as shutdown so exposing clamd directly to external services is not a feasible option. Accessing clamd directly is fine if you are running single application and it's on the localhost. 

# Usage

`clamav-rest`, being just a proxy, relies on a running instance of [mkdockx/docker-clamav](https://hub.docker.com/r/mkodockx/docker-clamav) - a self-updating dockerized ClamAV scanner.
To run `docker-clamav` as `clamav-server`:
```
  docker run -d --name clamav-server -p 3310:3310 -v clamav:/var/lib/clamav mkodockx/docker-clamav
```
> :warning: `docker-clamav` takes up a lot of mem to download virus definitions from the [CVD](https://www.clamav.net/documents/clamav-virus-database-faq) at startup. Your memory limit on Docker Desktop should be set to at least 3GB so it runs successfully.

To run a docker image of `clamav-rest`, you can use [eu.gcr.io/tradeshift-base/clamav-rest](https://console.cloud.google.com/gcr/images/tradeshift-base/EU/clamav-rest):
```
  docker run -d --name clamav-rest -e 'CLAMD_HOST=clamav-server' -p 8080:8080 --link clamav-server:clamav-server -t -i eu.gcr.io/tradeshift-base/clamav-rest
```

Or you can build the JAR. This creates a stand-alone JAR with embedded [Jetty serlet container](http://www.eclipse.org/jetty/).

```
  mvn package
```

Starting the REST service is quite straightforward.

```
  java -jar clamav-rest-1.0.2.jar --server.port=8765 --clamd.host=myprecious.clamd.serv.er --clamd.port=3310
```

## Setting up local clamd virtual server

By default clamd is assumed to respond in a local virtual machine. Setting it up is explained in
[ClamAV client](https://github.com/solita/clamav-java) repository. Or you can use a [clamd Docker image](https://hub.docker.com/r/mkodockx/docker-clamav).

# Testing the REST service

You will need to run `docker-clamav` (aka `clamav-server`) and a local `clamav-rest` to run the maven tests.
To run both instances:
```
  docker-compose up
```

To run the tests:
```
  mvn test
```

You can also use [curl](http://curl.haxx.se/) as it's REST.
Here's an example test session using an [EICAR file](http://www.eicar.org/86-0-Intended-use.html) - a test file recognized as a virus by scanners even though it's not really a virus:

```
  curl --header "Content-Type:application/octet-stream" --data-binary @<path/to/eicar/file> localhost:8080/file
Everything ok : false  # i.e. virus found
```

# License

Copyright © 2014 [Solita](http://www.solita.fi)

Distributed under the GNU Lesser General Public License, either version 2.1 of the License, or 
(at your option) any later version.

