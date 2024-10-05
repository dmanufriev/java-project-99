
.PHONY: build
build:
	./app/gradlew clean
	./app/gradlew build
	./app/gradlew checkstyleMain
	./app/gradlew test

report:
	./app/gradlew jacocoTestReport