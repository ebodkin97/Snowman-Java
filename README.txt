Run the jar file using command:

java -jar Snowman.jar
-------------------------------------------------------
for compilation,

Firstly change to correct directory. 
Then use the javac command to build into the correct class folder:

javac -d classes -cp classes  src/gmaths/*.java
javac -d classes -cp classes  src/snowman/*.java

-------------------------------------------------------
then run class using:
java -cp classes snowman.Snowman