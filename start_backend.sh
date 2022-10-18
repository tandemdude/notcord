#!/bin/bash

pushd backend
npx tailwindcss -i ./src/main/resources/static/input.css -o ./src/main/resources/static/output.css
./mvnw spring-boot:run
