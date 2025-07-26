#!/bin/bash

# Create temporary directory
mkdir -p target/test-plugin-classes

# Compile test plugin classes
javac -cp "$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q):target/classes" \
      -d target/test-plugin-classes \
      src/test/java/com/alibaba/qlexpress4/pf4j/TestPluginInterface.java \
      src/test/java/com/alibaba/qlexpress4/pf4j/TestPluginImpl.java

# Copy plugin configuration file
cp src/test/resources/test-plugins/plugin.properties target/test-plugin-classes/

# Package into jar
jar cf src/test/resources/test-plugins/test-plugin.jar -C target/test-plugin-classes .

echo "Plugin jar created: src/test/resources/test-plugins/test-plugin.jar" 