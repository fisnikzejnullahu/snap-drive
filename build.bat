call mvn clean package -DskipTests -f snap-drive-api/pom.xml
call mvn clean package -DskipTests -f snapdrive-local-api/pom.xml
call docker-compose up --build