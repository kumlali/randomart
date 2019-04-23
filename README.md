# randomart
800x800 random art jpeg image generator based on Ilkka Kokkarinen's [Random Art Generator](http://www.scs.ryerson.ca/~ikokkari/RandomArt/RandomArt.html) project.

## Building
    mvn package

Note: Requires JDK 1.8 or later and Maven.

## Usage

### Generating multiple random art images via CLI
    java -cp randomart-0.0.1-SNAPSHOT.jar kokkarinen.ilkka.RandomArtCli outDir fileNamePrefix fileCount

For example, following command creates 25 random art jpg file (myimg00001.jpg to myimg00025.jpg) under /tmp directory: 

    java -cp randomart-0.0.1-SNAPSHOT.jar kokkarinen.ilkka.RandomArtCli /tmp/ myimg 25

### Opening GUI
    java -jar randomart-0.0.1-SNAPSHOT.jar

or

    java -cp randomart-0.0.1-SNAPSHOT.jar kokkarinen.ilkka.RandomArtPanel
 