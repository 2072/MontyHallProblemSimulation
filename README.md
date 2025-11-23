# Monty Hall Problem – Functional Scala 3 Simulation

A crystal-clear, fully commented, tested, and mathematically correct simulation of the famous **[Monty Hall Problem][wikipedia_MHp]** (and its generalization) written in pure functional Scala 3.

The code is written in a way that should be readable even if you don't know anything about Scala, it's written using
the [functional programming paradigm][wikipedia_FP] which makes it very natural (you transform things until they
are what you want using functions whose output only depends on their inputs without any side effects... Just notice the
absence of variables)

I stumble onto this very well known problem today, which is difficult to grasp intuitively—as you can see by the
length of the [Wikipedia page][wikipedia_MHp]—and decided to create a simple and clear simulation of the game in Scala3, so I could
understand it better. Since I'm quite pleased with the result, I decided to share it (it's also a nice Scala3 sample project)

Everything happens in the [main.scala](src/main/scala/main.scala) file, it is meant to be pastable as-is in an online editor such as:
- https://scastie.scala-lang.org/ (on this one, you need to disable _worksheet_ mode before running, or you won't see any output)
- https://www.programiz.com/scala/online-compiler/

The simulation runs 100,000 trials, you can change this by editing the `TRIALS_NUMBER` definition.

After reading [main.scala](src/main/scala/main.scala) the Monty Hall problem should no longer be one!

An easy-to-understand [test suite](src/test/scala/mainTest.scala) written with [ScalaTest](https://www.scalatest.org/) is also available.

Sample output of the program:

```text
Comparing 3-doors games (the original Monty Hall problem):
Probability to win a 3-doors game when NEVER changing from the initial choice: 33.21% (for 100000 games)
Probability to win a 3-doors game when ALWAYS changing the initial choice: 66.86% (for 100000 games)

Comparing 4-doors games (a generalization of the Monty Hall problem):
Probability to win a 4-doors game when NEVER changing from the initial choice: 24.89% (for 100000 games)
Probability to win a 4-doors game when ALWAYS changing the initial choice: 75.03% (for 100000 games)

Comparing 30-doors games (a generalization of the Monty Hall problem):
Probability to win a 30-doors game when NEVER changing from the initial choice: 3.31% (for 100000 games)
Probability to win a 30-doors game when ALWAYS changing the initial choice: 96.68% (for 100000 games)

Comparing 100-doors games (a generalization of the Monty Hall problem):
Probability to win a 100-doors game when NEVER changing from the initial choice: 0.99% (for 100000 games)
Probability to win a 100-doors game when ALWAYS changing the initial choice: 99.02% (for 100000 games)

Comparing 200-doors games (a generalization of the Monty Hall problem):
Probability to win a 200-doors game when NEVER changing from the initial choice: 0.51% (for 100000 games)
Probability to win a 200-doors game when ALWAYS changing the initial choice: 99.52% (for 100000 games)
```

## How to run locally

### Prerequisites
- [Java 11 or higher (Java 21 recommended)][msJDKBuids]
- [sbt](https://www.scala-sbt.org/) (Scala Build Tool)

### Commands

```bash
# 1. Clone the repository
git clone https://github.com/2072/MontyHallProblemSimulation.git
cd MontyHallProblemSimulation

# 2. Run the simulation (shows results for 3, 4, 100, and 200 doors on 100,000 game trials)
sbt run

# 3. Run the tests
sbt test

# 4. Run tests continuously
sbt ~test

# 5. Compile only (no execution)
sbt compile
```


[wikipedia_MHp]: https://en.wikipedia.org/wiki/Monty_Hall_problem "Wikipedia's definition of the Monty Hall problem"
[wikipedia_FP]: https://en.wikipedia.org/wiki/Functional_programming "Wkipedia's definition of functional programming"
[msJDKBuids]: https://learn.microsoft.com/en-us/java/openjdk/download "Microsoft Build of OpenJDK (good builds of the JDK but others are available)"