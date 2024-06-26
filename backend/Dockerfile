FROM ubuntu
MAINTAINER LTTMG

RUN apt-get update
RUN apt-get install -y openjdk-17-jre-headless

ADD target/backend-0.0.1-SNAPSHOT.jar /backend/backend.jar
RUN mkdir /backend/res
RUN mkdir /backend/res/avatars
RUN mkdir /backend/res/assignment_attachment
ADD res/avatars/default.png backend/res/avatars/

EXPOSE 8081

WORKDIR /backend
CMD ["java", "-jar", "/backend/backend.jar"]
