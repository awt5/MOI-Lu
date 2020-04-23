# Dockerfile
FROM lucerodocker/ubuntu-plus:ub18.04-opjdk8-py3

# Copy data
WORKDIR /home/MOI/

COPY build/libs/*.jar .

CMD ["java", "-jar", "*.jar"]