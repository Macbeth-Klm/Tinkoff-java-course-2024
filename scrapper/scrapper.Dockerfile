FROM openjdk:21
WORKDIR /app
COPY . /app
ENV DB_USERNAME=${DB_USERNAME} DB_PASSWORD=${DB_PASSWORD}
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.datasource.username=$DB_USERNAME", "-Dspring.datasource.password=$DB_PASSWORD", "/scrapper.jar"]
