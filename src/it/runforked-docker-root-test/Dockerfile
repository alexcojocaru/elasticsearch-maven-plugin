# Dockerfile for testing autoHandleRootUser feature when running as root
# This test simulates a CI environment where the build runs as root in a container

FROM maven:3.9.5-eclipse-temurin-8

# Install required tools
RUN apt-get update && \
    apt-get install -y sudo procps && \
    rm -rf /var/lib/apt/lists/*

# Copy the entire project
COPY . /plugin

# Set working directory
WORKDIR /plugin/src/it/runforked-docker-root-test

# Run the test as root user
# The plugin should automatically create esuser and run ES as that user
CMD ["mvn", "clean", "verify", "-Delasticsearch.maven.plugin.version=6.30-SNAPSHOT", "-Dmaven.version=3.9.5", "-X"]
