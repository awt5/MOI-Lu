# Dockerfile
FROM lucerodocker/ubuntu-plus:ub18.04-opjdk8-py3

# Copy data
WORKDIR /home/MOI/
COPY ./build/libs/MOI-1.0-SNAPSHOT.jar .

CMD ["java", "-jar", "MOI-1.0-SNAPSHOT.jar"]