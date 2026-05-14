FROM openjdk:22-jdk
ADD target/user-details.jar user-details.jar
ENTRYPOINT [ "java","-jar","/user-details.jar" ]
