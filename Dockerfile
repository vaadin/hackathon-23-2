### Stage 1: Image used for building the app
FROM openjdk:17-jdk-slim as build

ARG SEC=${SEC}
ARG LIC=${LIC}
# Test that a valid license is passed from host 
RUN (test -n "${LIC}" && mkdir -p ~/.vaadin && echo "${LIC}" > ~/.vaadin/proKey)

# Copy sources to the image used for building
COPY . /tmp/build
WORKDIR /tmp/build

# Build the app for production
RUN (test -n "${SEC}" && echo "com.vaadin.example.sightseeing.auth.secret=${SEC}" >> src/main/resources/application.properties)
RUN (bash mvnw -ntp package -DskipTests -Pproduction)

# Save and rename the app so as it can be taken in next stage
RUN ls -l /tmp/build/target/
RUN cp /tmp/build/target/*.jar /tmp/app.jar

### Stage 2: Image used for deploying and running the app
FROM openjdk:17-jdk-slim

# Copy app from the build container
COPY --from=build /tmp/app.jar /tmp/

# Add a non-root user for running the app
RUN useradd -m myuser
WORKDIR /tmp/app
RUN (chown myuser:myuser /tmp/app && chmod -R 777 /tmp)
USER myuser

# Expose the app port
EXPOSE 8080

# Execute the app
ENTRYPOINT ["java", "-jar", "/tmp/app.jar"]
