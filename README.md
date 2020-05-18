
# Word War: Client

[![GitHub release](https://img.shields.io/github/v/release/prasad1120/word-war-client.svg)](https://GitHub.com/prasad1120/word-war-client/releases/)
![GitHub top language](https://img.shields.io/github/languages/top/prasad1120/word-war-client)

A multiplayer game in Java using client-server architecture and sockets in which two players compete to 
find more number of English language words in a predefined time from a separate but similar 
4x4 board of letters based on their frequency in English.

 > Check out [word-war-server](https://github.com/prasad1120/word-war-server)

<p float="middle">
  <img src="https://github.com/prasad1120/word-war-client/blob/master/gameplay.gif" />
</p>
 
## Features
- [x] Board populated on the basis of letter frequency in English language
- [x] Score of opponent displayed in realtime
- [x] All possible words are displayed after the game
- [x] Rematch with same as well as fresh opponent

## Built with
- **Gradle** - used as build system
- **Java Swing** - used for GUI

## Build and Run Process

- To run the Word War Client, in your top-level project directory, run the following command on terminal:
~~~
$ ./gradlew run
~~~
 > When running Word War Client, set host and port (on which Word War Server is running) to required values in `WordWarClient.java`.

- To build an executable jar, run:
~~~
$ ./gradlew jar
~~~
    
- To build and run Word War Client jar, run:
~~~
$ ./gradlew runExecutableJar
~~~

 > When building an executable jar, set host and port (on which Word War Server is running) to required values in `build.gradle`. 
The jar will get exported as `build/libs/word-war-client-<version>.jar`.

**Default values**:
```
host='localhost'
port=8000
```
