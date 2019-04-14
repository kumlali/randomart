# randomart
Random art image generator based on Ilkka Kokkarinen's [Random Art Generator](http://www.scs.ryerson.ca/~ikokkari/RandomArt/RandomArt.html) project.

It basically generates 800x800 random jpeg image.

## Build
    mvn package

Note: Requires JDK 1.8 or later.

## Usage

### Generating random art image by CLI
    java -jar randomart-0.0.1-SNAPSHOT.jar /tmp/myfile.jpg

or

    java -cp randomart-0.0.1-SNAPSHOT.jar kokkarinen.ilkka.RandomArtCli /tmp/myfile.jpg

### Opening GUI
    java -cp randomart-0.0.1-SNAPSHOT.jar  kokkarinen.ilkka.RandomArtPanel