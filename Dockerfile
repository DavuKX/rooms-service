FROM tomcat:11-jdk21
LABEL authors="davu"

RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY target/rooms-service.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
