# Description
## Purpose
A simple tagging of youtube videos, based on the captions attached to them. 

## Technical view
The downloading is performed concurrently thanks to Akka, whereas Akka Streams provide concise and effective way to compose the computations.

# Usage
## Input format
The tags should be provided by a file with ids, each line consisting of exactly one id.

## Output format
A json file consisting of separated objects for each video id. Each of them has its atributes (id, raw captions, parsed captions and list of wikipedia articles for found tags). Each article has three atributes (link to the article, raw content and parsed content). Example input in `src/main/resources/exampleIds.csv`)
## Commands
1. `sbt` to load the project
2. Having loaded sbt, `run sourcePath destPath` to run the tagging. 
or `test` to invoke the tests

# Code overview
The clients for `http://video.google.com/timedtext` and `https://en.wikipedia.org/w/api.php` are implemented separately with coresponding ADT and Akka Stream Flows. The composision of the above is in `MainFlows.scala` with auxiliary ADT in `MainModel.scala`. The composision of the above, reading from and wrinting to a file is in `Main.scala`. Tags from the captions are simple unigram entities.
