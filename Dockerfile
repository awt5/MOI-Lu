# Dockerfile
FROM ub_python_java:1.0

# Copy data
RUN mkdir /home/MOI/;
COPY ./build/libs/MOI-1.0-SNAPSHOT.jar /home/MOI/

WORKDIR /home/MOI/;

ENTRYPOINT ["java"]

CMD ["-jar", "MOI-1.0-SNAPSHOT.jar"]
