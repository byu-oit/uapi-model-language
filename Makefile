
build: kt_build
clean: schema_clean kt_clean
compile: kt_compile
test: schema_test kt_test
deploy: kt_deploy

kt_build:
	cd kotlin && ./mvnw install

kt_clean:
	cd kotlin && ./mvnw clean

kt_compile:
	cd kotlin && ./mvnw compile test-compile

kt_test: kt_compile
	cd kotlin && ./mvnw test integration-test

kt_deploy: test build
	cd kotlin && ./mvnw deploy

schema_clean:
	rm -rf tmp

tmp:
	mkdir -p tmp

schema_test: examples_yml
	npx ajv-cli compile -s json-schema.json
	npx ajv-cli validate -s json-schema.json -d "examples/*.json"

ymlfiles := $(wildcard examples/*.yml)

destfiles := $(patsubst examples/%.yml,examples/%.yml.json,$(ymlfiles))

examples_yml: $(destfiles)

examples/%.yml.json: examples/%.yml
	npx js-yaml $< > $@


