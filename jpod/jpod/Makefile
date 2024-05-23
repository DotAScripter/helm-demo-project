PROJECT=jpod

SRC_ROOT := src/main/java
JPOD_ROOT := $(SRC_ROOT)/com/hdp/jpod

JAVACLEAN := mvn clean

.PHONY: build-full
build-full:
	rm -rf ./target
	mvn clean install

.PHONY: build
build:
	mvn clean install -DskipTests

.PHONY: package
package:
	mvn package

.PHONY: image
image:
	docker build --build-arg PROJECT=$(PROJECT) ./../ -t $(PROJECT):1.0


.PHONY: clean
clean:
	$(JAVACLEAN)
