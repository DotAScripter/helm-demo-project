1. Requirements
* Jdk17
* Maven 3.9.5

2. Compile Java code
'make build' 

3. Setup ports and IP addresses
To set the service ports and addresses,
specify all env variables used in ClusterService.java

4. Run executable
'java -jar ./target/jpod-1.0.jar' 

5. Build docker image
In top directory of project run:
'docker build -t jpod:1.0 .'

6. Run docker image
When docker image with tag 'jpod' is built run:
'docker run -p [port]:[port] jpod'
[port] should be 'JPOD_HTTP_PORT'.