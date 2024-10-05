
.PHONY: build
build:
	@echo "Current directory: $(shell pwd)"
	@echo "Listing files:"
	@ls -l
	./gradlew clean
	./gradlew build
	./gradlew checkstyleMain
	./gradlew test

report:
	./gradlew jacocoTestReport