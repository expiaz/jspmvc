cd ..
echo "Building jar"
javac -d ./command/build ./command/*.java
cd ./command/build
jar cfm symfonee.jar MANIFEST.MF *
# jar cmvf ./command/META-INF/MANIFEST.MF ./command/symfonee.jar ./command/build/*
echo "Running jar"
java -jar ./symfonee.jar