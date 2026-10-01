**Platform Builder Gradle Plugin** Changelog

## Version 0.6
* [fix]: check for self-references and don't include them in the provenance graph.

## Version 0.5
* [fix]: support determining provenance with rich-version detection heuristic.

## Version 0.4
* [feat]: track provenance.
* [feat]: support local Android libraries as source of constraints.

## Version 0.3
* [fix]: don't emit constraints relating to `com.google.guava:guava` or `com.google.guava:listenablefuture`. (Ugly hack)

## Version 0.2
* [fix]: Support KMP libraries as sources of constraints.
* [fix]: Support down to Gradle 9.0.0.

## Version 0.1
* First release.
