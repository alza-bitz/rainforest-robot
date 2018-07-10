# rainforest-robot

## Problem

Rainforest LTD is an online distribution company that specialises in selling bags of sugar-free gummy bears.  They have a large warehouse in Milton Keynes where crates of the bags are stored.
A new remote-controlled robot is being tested at the warehouse.  The robot can move around the warehouse floor, reach into crates to pick up bags of gummy bears, and drop the bags off at a conveyor-belt feeder. The robot is only able to pick up one bag at a time, but can carry multiple bags at once.  However when dropping off the bags the robot drops all the bags in its possession at once.

The robot is controlled with the following instructions:

**N, S, E, W** - the robot moves one unit of distance in the direction specified (N increases the y coordinate value, E increases the x coordinate value).

**P** - pick up one bag of sugar-free gummy bears from a crate.

**D** - drop the bags of sugar-free gummy bears that the robot currently has in its possession onto the conveyor-belt feeder.

A couple of issues with the robot have been found:
- If the robot tries to retrieve a bag from a position where a crate doesn’t reside, it falls over and short-circuits.  From this point onwards it no longer responds to instructions.
- If the robot tries to drop bags off at a position that is any place other than the conveyor-belt feeder, the bags get caught in its wheels and it short-circuits.  In this instance it also no longer responds to instructions.

Your task is to develop an application that takes in the following lines of input:

  1. The x, y coordinates of the position of the conveyor-belt feeder
  1. The x, y coordinates of the start position of the robot
  1. Comma separated descriptions of the crates.  Each crate has an x coord, y coord and quantity.
  1. A set of instructions for the robot to perform.

The application should respond with the total number of bags dropped on the conveyor-belt feeder, and the final position and health of the robot (either OK or BROKEN)

For example:
```
INPUT
0 2                // Conveyor-belt feeder is at coord 0,2
0 0                // Robot is at coord 0,0
0 1 10, -1 -2 5        // A crate at 0,1 with 10 bags and at -1,-2 with 5 bags
NPPPND            // Move North, pick up a bag (x3), move north, drop bags

OUTPUT
3                // 3 bags have been dropped on the conveyor-belt feeder
0 2 OK            // The robot is at 0,2 and still functioning
```

### Sample Test Cases
```
INPUT
1 1
0 1
0 0 10
PNNEE

OUTPUT
0
0 1 BROKEN
```
---
```
INPUT
0 5
0 1
0 1 3, 1 3 3
PPPPENNPPWNNDSS

OUTPUT
5
0 3 OK
```
---
```
INPUT
-2 -2
0 0
-1 -1 2
SWPSWDNDN

OUTPUT
1
-2 -1 BROKEN
```

## Solution

This is my solution to the Rainforest Robot problem.

I provide a command-line application that takes a formatted text file containing warehouse setup and robot instructions, and after setting up the warehouse and performing the instructions will print a report of the 
warehouse to stdout.

### Requirements

- [Leiningen][leiningen] (to run the tests, check test coverage, execute the app, or build an executable jar artifact)
- A Java runtime (to execute the jar artifact)
- A correctly formatted text file containing warehouse setup and robot instructions

### Usage

There are two options:

#### Execute the App using Leiningen

	lein run test-resources/instructions.txt

#### Build an Executable Jar Artifact and Execute using Java

	lein uberjar
	
	java -jar target/rainforest-robot-0.1.0-SNAPSHOT-standalone.jar test-resources/instructions.txt

### Tests

The provided test suite covers the sample test cases shown above (see `sample-tests` in `core_test.clj`).

#### Running the Tests

	lein test

#### Checking the Test Coverage

1. Add `[lein-cloverage "1.0.6"]` to `:plugins` in `~/.lein/profiles.clj`
1. Execute `lein cloverage`

### Assumptions and Limitations for My Solution

1. No limit on warehouse size (other than limits of underlying data types)
1. A maximum of one robot and belt can be placed
1. Not possible to place more than one crate or belt in same position
1. No limit on the number of bags that can be carried by the robot, in a crate or dropped on the belt (other than limits of underlying data types)

### Notes on My Solution Design

1. State changes in the warehouse are either (robot+crate) or (robot+belt). So the atomicity/consistency is designed around what's common to both of these cases - the warehouse. The function calls in the warehouse are designed for use as value succession functions (with warehouse as first argument), so that they will work with an atom ref if one were to be created for the warehouse. (In the solution I have not actually used an atom ref since it wasn't warranted in this case, but the basic support is there in the design).
1. A 'perform' multimethod was used for the robot instructions, in order to simplify the code when the robot is broken (a common data structure for both the instruction argument and return value).

### Roadmap or Possible Improvements

In no particular order:

1. Use [clojure.spec][clojure.spec] in combination with property-based testing ([test.check][test.check]), instead of ad-hoc unit testing around a small fixed number of cases (and edge-cases)
1. Provide a RESTful HTTP endpoint in addition to the command line endpoint

## License

Copyright © 2016 Alex Coyle

Distributed under the MIT License.

[leiningen]: http://leiningen.org
[clojure.spec]: http://clojure.org/about/spec
[test.check]: http://github.com/clojure/test.check
