FROM openjdk:21
WORKDIR /app
COPY . /app
ENV TELEGRAM_TOKEN=${TELEGRAM_TOKEN}
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "-Dapp.telegram-token=$TELEGRAM_TOKEN", "/bot.jar"]
