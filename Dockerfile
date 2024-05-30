FROM gradle:jdk21-alpine as build

COPY . .

RUN gradle clean build --no-daemon

FROM eclipse-temurin:21-alpine as runtime

WORKDIR /app

COPY --from=build /home/gradle/build/libs/gitcord-*-all.jar bot.jar

ENTRYPOINT ["java", "-Dbot.config=config/config.json", "-Dlog4j.configurationFile=config/log4j2.xml", "-Dcjda.localisation.error.name=false", "-jar" , "bot.jar"]
