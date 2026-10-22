FROM artifacts.developer.gov.bc.ca/docker-remote/maven:3.9.11-amazoncorretto-17-alpine AS build
WORKDIR /workspace/app

COPY api/pom.xml .
COPY api/src src
RUN mvn clean package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM artifacts.developer.gov.bc.ca/docker-remote/amazoncorretto:17.0.20-alpine3.23
RUN apk upgrade --no-cache && apk add --no-cache tzdata
RUN addgroup -S spring && adduser -S -G spring -s /bin/sh spring
RUN mkdir -p /logs
RUN chown -R spring:spring /logs
RUN chmod 755 /logs
USER spring
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-Duser.name=DIGITALID_API","-Xms312m","-Xmx312m","-XX:TieredStopAtLevel=1","-XX:+UseParallelGC","-XX:MinHeapFreeRatio=20","-XX:MaxHeapFreeRatio=40","-XX:GCTimeRatio=4","-XX:AdaptiveSizePolicyWeight=90","-XX:MaxMetaspaceSize=100m","-XX:ParallelGCThreads=2","-Djava.util.concurrent.ForkJoinPool.common.parallelism=1","-XX:CICompilerCount=2","-XX:+ExitOnOutOfMemoryError","-Djava.security.egd=file:/dev/./urandom","-Dspring.backgroundpreinitializer.ignore=true","-cp","app:app/lib/*","ca.bc.gov.educ.api.digitalid.DigitalIDApiResourceApplication"]
