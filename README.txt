Jelly
=====

The primary build tool for this project is Maven. 
So all you should need to do is install Maven and just type

	maven

Some common maven goals for building and testing this project are

	clean    : cleans up the build so new builds will start from fresh
	test     : just run the unit tests
	java:jar : compiles, runs unit tests and if they work build the jar
	javadoc  : creates the javadoc
	site     : build the complete documentation with reports, javadoc etc
	dist     : creates a distribution
			
For more help using Maven please go to

  http://jakarta.apache.org/turbine/maven/

Maven also supports the auto-generation of Ant build files so
you may also be able to use Ant to build the code.

Enjoy!
