package eater

import ai.Network
import kotlin.random.Random

class Eater(private val network: Network) {

    var steps = 0
    val maxSteps = 24

    var isAlive = true

    var positionX: Int = 0
    var positionY: Int = 0

    /**
     * Play 1 game, until eater crashes or runs out of steps.
     *
     * @param network neural network playing the game
     * @param maxFitness limit when should game end (prevents infinite game)
     * @param useGUI should be shown in GUI
     *
     * @return fitness reached
     */
    fun play(network: Network, maxFitness: Int, useGUI: Boolean = false): Int {
        return 1
    }
//        lateinit var gui: GUI
//        if (useGUI) {
//            gui = GUI(sideLength)
//            gui.isVisible = true
//        }
//
//        var score = 0
//        var steps = 0
//
//        while (steps < maxSteps) {
//            if (useGUI) {
////                gui.update(arrayListOf(eaterLocationX, eaterLocationY, appleLocationX, appleLocationY))
//                Thread.sleep(delay)
//            }
//
//            move(evaluateMove(network))
//            steps += 1
//
//            if (isEaterDead()) {
//                break
//            }
//
//            if (isAppleEaten()) {
//                score += 1
//                steps = 0
//                setRandomApplePosition()
//            }
//            if (scoreFormula(score, steps) >= maxFitness) {
//                break
//            }
//        }
//
//        if (useGUI) gui.quit()
//        return scoreFormula(score, steps)
//    }

    fun setPosition(sideLength: Int) {
        positionX = Random.nextInt(1, sideLength - 1)
        positionY = Random.nextInt(1, sideLength - 1)
    }

    /**
     * Networks calculates next move.
     *
     * @param network neural network
     *
     * @return direction to move
     */
    private fun evaluateMove(distanceToApple: ArrayList<Int>, distanceToWalls: ArrayList<Int>): Direction {
        val inputs = arrayListOf(
            distanceToApple[0].toDouble(),
            distanceToApple[1].toDouble(),
            distanceToApple[2].toDouble(),
            distanceToApple[3].toDouble(),
            distanceToWalls[0].toDouble(),
            distanceToWalls[1].toDouble(),
            distanceToWalls[2].toDouble(),
            distanceToWalls[3].toDouble(),
        )

        network.setInputs(inputs)
        network.propagate()
        val softmaxOutput = network.softmaxOutput()

        val evaluationMatrix = mutableMapOf<Direction, Double>()
        evaluationMatrix[Direction.LEFT] = softmaxOutput[0]
        evaluationMatrix[Direction.RIGHT] = softmaxOutput[1]
        evaluationMatrix[Direction.UP] = softmaxOutput[2]
        evaluationMatrix[Direction.DOWN] = softmaxOutput[3]

        val sortedResult = evaluationMatrix.toList().sortedBy { (_, value) -> value }
        return sortedResult.last().first
    }

    private enum class Direction {
        LEFT, RIGHT, UP, DOWN
    }

    /**
     * Move eater on the board.
     */
    private fun move(direction: Direction) {
        when (direction) {
            Direction.LEFT -> positionX -= 1
            Direction.RIGHT -> positionX += 1
            Direction.DOWN -> positionY += 1
            Direction.UP -> positionY -= 1
        }
    }

    fun think(distanceToApple: ArrayList<Int>, distanceToWalls: ArrayList<Int>) {
        move(evaluateMove(distanceToApple, distanceToWalls))
    }

    /**
     * Detect wall collision.
     *
     * @return true if eater crashed
     *         false if not
     */
    fun crashedToWall(sideLength: Int): Boolean {
        return (positionX < 1)
                || (positionY < 1)
                || (positionX >= sideLength - 1)
                || (positionY >= sideLength - 1)
    }

    fun crashedToEater(otherEaters: ArrayList<Eater>): Boolean {
        for (otherEater in otherEaters) {
            if (otherEater == this) continue

            if ((this.positionX == otherEater.positionX) && this.positionY == otherEater.positionY) {
                return true
            }
        }
        return false
    }

    fun isExhausted(): Boolean {
        return steps > maxSteps
    }
}