/**
 * CCopyright (c) 2025 John Wellesz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the “Software”), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/*
This is written in a single file only so that the whole code can be copy/pasted into an online editor such as:
- https://scastie.scala-lang.org/ (there, you need to disable worksheet mode before running)
- https://www.programiz.com/scala/online-compiler/
 */

import scala.util.Random

/**
 * In Scala, this is called a companion object, here it's used to declare a static object that stays
 * the same for all games (Our random generator)
 */
object MontyHallGame {
  // The pseudo-random number generator to be used by the Game and the host (Monty)
  // (the same generator will be used for all the games)
  val gameRandomGen: Random = new Random()
}

/**
 * Our MontyHallGame instantiated object
 *
 * The winning door is chosen randomly when the object is constructed
 *
 * @param doorNum the number of door used in this game
 * @param candidate1stChoice the candidate 1st choice
 */
case class MontyHallGame(doorNum: Int, candidate1stChoice: Int) {

  import MontyHallGame.gameRandomGen

  // Sanity checks
  require(
    doorNum >= 3,
    "The game makes no sense below 3 doors"
  )

  require(
    candidate1stChoice >= 0 && candidate1stChoice < doorNum,
    s"Invalid 1st choice (doors are numbered from 0 until $doorNum"
  )

  // the winning door is chosen randomly when the game is created
  private val winningDoor: Int = gameRandomGen.between(0, doorNum) // ( 0 >= winningDoor < doorNum)

  // lazy just means that the value is computed/created the first time it is used, not before (just for memory optimization in this case)
  private lazy val allTheDoors = 0 until doorNum

  /**
   * In the second phase of the game, Monty reveals all the goats he can without touching the winning door or the candidate first choice
   *
   * If the candidate made the right choice, Monty just has to leave one goat-door closed if not, he is forced to reveal the winning door by leaving it closed
   *
   * @return all the goat-doors Monty chose to reveal to the candidate
   */
  def montyRevealedGoats: List[Int] = {
    if (candidate1stChoice == winningDoor) {
      // if Monty knows the candidate has made the right 1st choice, he can reveal all the goats but one

      // all the doors but the one already chosen by the candidate
      val allGoatDoors = allTheDoors.filterNot(_ == candidate1stChoice)

      // among those goat-doors, randomly choose one to leave closed
      val goatToLeaveClosed = allGoatDoors(gameRandomGen.between(0, allGoatDoors.size))

      // return all those goat-doors except the one randomly chosen to remain closed
      allGoatDoors.filterNot(_ == goatToLeaveClosed)
    } else { // the candidate 1st choice is a goat
      // if the candidate made the wrong choice, then Monty is forced to open all the other goat-doors
      // and leave the winning door closed thus "revealing" it

      // all the doors but the one already chosen by the candidate
      val allRemainingDoors = allTheDoors.filterNot(_ == candidate1stChoice)

      // Monty is forced to only keep the winning door closed
      val allGoatDoors = allRemainingDoors.filterNot(_ == winningDoor)

      allGoatDoors // return ALL the doors with a goat
    }

  }.toList

  /**
   * Play the first round of the game
   *
   * @return the remaining door the candidate can choose to open instead of the one they have already chosen after
   *         Monty revealed all the goats but one (*cough* I mean left a door closed)
   */
  lazy val playRoundOne: Int = {
    val remainingDoors = allTheDoors // all the doors
      .filterNot(_ == candidate1stChoice).toVector // minus the one already chosen by the candidate
      .diff(montyRevealedGoats) // minus all the goats revealed by Monty

    require(remainingDoors.size == 1, "This universe is using different logic laws, only one door should remain, please make sure you're running this code in Universe#42")

    remainingDoors.head // just return the only door left closed by Monty
  }

  /**
   * Play the second round with an optional different choice
   *
   * @param o_newChoice the optional new choice to use
   * @return
   */
  def playRoundTwo(o_newChoice: Option[Int]): Boolean = {
    // sanity check
    require(o_newChoice.forall(_ == playRoundOne), s"Invalid 2nd choice: ${o_newChoice.get} while there is only door #$playRoundOne remaining.")

    o_newChoice
      .map(_ == winningDoor) // when there is a 2nd choice, transform newChoice into true if it matches the winning door, false otherwise
      .getOrElse(candidate1stChoice == winningDoor) // get and return the above result if there was a 2nd choice or (if no 2nd choice) return true if the first choice is the winning door, false otherwise
  }

}  
  
@main
def main(): Unit = {

  // Not necessary but we use a different number generator for the player because we are simulating two distinct systems
  // interacting, the constructor is seeding them with a different value according to Java's documentation but using
  // MontyHallGame.gameRandomGen instead would yield the exact same results
  val playerRandomGen = new Random()

  // constants of the simulation
  /**
   * The base door number used in the Monty Hall game simulations
   */
  val BASE_DOOR_NUM = 3

  /**
   * The number of games to simulate: note that given the non-optimized way this simulation is done
   * (no optimization to keep the code understandable), it will use a lot of memories
   */
  val TRIALS_NUMBER = 100_000

  /**
   * Simulate TRIALS_NUMBER games where the candidate always chooses to keep their first choice
   * @param doorNum the number of door in the game
   * @return the winning probabilities observed between 0 and 1
   */
  def winningProbabilitiesWithNoChoiceChange(doorNum: Int): Double = {
    // create TRIALS_NUMBER independent games
    val games = List.fill(TRIALS_NUMBER)(MontyHallGame(
      doorNum, // the number or doors
      playerRandomGen.between(0, doorNum) // the player randomly chooses a door
    ))
    // play each game never changing our initial choice (equivalent to playing round2 directly with no second choice)
    val gameResults = games.map(_.playRoundTwo(None))

    gameResults.count(_ == true).toDouble / TRIALS_NUMBER.toDouble
  }

  /**
   * Simulate TRIALS_NUMBER games where the candidate always chooses to change from their initial choice
   * @param doorNum the number of door in the game
   * @return the winning probabilities observed between 0 and 1
   */
  def winningProbabilitiesWithAlwaysChoiceChange(doorNum: Int): Double = {
    // create TRIALS_NUMBER independent games
    val games = List.fill(TRIALS_NUMBER)(MontyHallGame(
      doorNum, // the number of doors
      playerRandomGen.between(0, doorNum)) // the player randomly chooses a door
    )
    val gameResults = games
      // for each game, always choose the other door
      .map { g =>
      val remainingDoor = g.playRoundOne

      g.playRoundTwo(Some(remainingDoor))
    }

    gameResults.count(_ == true).toDouble / TRIALS_NUMBER.toDouble
  }

  def compareProbabilities(doorNum: Int): Unit = {
    println(s"\nComparing $doorNum-doors games (${if (doorNum == 3) "the original Monty Hall problem" else "a generalization of the Monty Hall problem"}):")
    println(f"Probability to win a $doorNum-doors game when NEVER changing from the initial choice: ${winningProbabilitiesWithNoChoiceChange(doorNum) * 100}%02.2f%% (for $TRIALS_NUMBER games)")
    println(f"Probability to win a $doorNum-doors game when ALWAYS changing the initial choice: ${winningProbabilitiesWithAlwaysChoiceChange(doorNum) * 100}%02.2f%% (for $TRIALS_NUMBER games)")
  }

  // Run the simulations with games with increasing door numbers
  compareProbabilities(BASE_DOOR_NUM)
  compareProbabilities(BASE_DOOR_NUM + 1)
  compareProbabilities(BASE_DOOR_NUM * 10)
  compareProbabilities(100)
  compareProbabilities(200)

  /*
  Sample output:

    Comparing 3-doors games (the original Monty Hall problem):
    Probability to win a 3-doors game when NEVER changing from the initial choice: 33.06% (for 100000 games)
    Probability to win a 3-doors game when ALWAYS changing the initial choice: 66.61% (for 100000 games)

    Comparing 4-doors games (a generalization of the Monty Hall problem):
    Probability to win a 4-doors game when NEVER changing from the initial choice: 25.11% (for 100000 games)
    Probability to win a 4-doors game when ALWAYS changing the initial choice: 74.89% (for 100000 games)

    Comparing 30-doors games (a generalization of the Monty Hall problem):
    Probability to win a 30-doors game when NEVER changing from the initial choice: 3.37% (for 100000 games)
    Probability to win a 30-doors game when ALWAYS changing the initial choice: 96.72% (for 100000 games)

    Comparing 100-doors games (a generalization of the Monty Hall problem):
    Probability to win a 100-doors game when NEVER changing from the initial choice: 0.99% (for 100000 games)
    Probability to win a 100-doors game when ALWAYS changing the initial choice: 99.00% (for 100000 games)

    Comparing 200-doors games (a generalization of the Monty Hall problem):
    Probability to win a 200-doors game when NEVER changing from the initial choice: 0.52% (for 100000 games)
    Probability to win a 200-doors game when ALWAYS changing the initial choice: 99.50% (for 100000 games)
   */
}