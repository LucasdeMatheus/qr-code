FROM openjdk:21-jdk

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR gerado para o container
COPY target/*.jar app.jar

# Comando para executar o JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
