
build: kotlin/pom.xml kotlin/mvnw
	cd kotlin && ./mvnw clean install

deploy: kotlin/pom.xml kotlin/mvnw
	cd kotlin && ./mvnw deploy
