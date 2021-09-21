call mvn clean package -f snap-drive-api/pom.xml
call mvn clean package -f snapdrive-local-api/pom.xml
call docker-compose up --build