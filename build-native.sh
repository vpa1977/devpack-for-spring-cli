export JAVA_HOME=/snap/graalvm-jdk/current/graalvm-ce/
./gradlew --no-daemon build \
          nativeCompile \
          -PcliNative=true \
          -x test