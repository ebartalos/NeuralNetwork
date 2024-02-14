package eater

import ai.Network
import kotlin.random.Random

class Eater(private val network: Network) {

    var steps = 0
    private val maxSteps = 30

    var isAlive = true

    var positionX: Int = 0
    var positionY: Int = 0

    fun setRandomPosition(sideLength: Int) {
        positionX = Random.nextInt(1, sideLength - 1)
        positionY = Random.nextInt(1, sideLength - 1)
    }

    /**
     * Calculate next move.
     **
     * @return direction to move
     */
    private fun evaluateMove(distanceToApple: ArrayList<Int>, distanceToDeath: ArrayList<Int>): Direction {
        val inputs = arrayListOf(
            distanceToApple[0].toDouble(),
            distanceToApple[1].toDouble(),
            distanceToApple[2].toDouble(),
            distanceToApple[3].toDouble(),
            distanceToDeath[0].toDouble(),
            distanceToDeath[1].toDouble(),
            distanceToDeath[2].toDouble(),
            distanceToDeath[3].toDouble(),
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
     * Evaluate next move and move.
     *
     * @param distanceToApple distance to the apple (reward)
     * @param distanceToDeath distance to the death
     */
    fun move(distanceToApple: ArrayList<Int>, distanceToDeath: ArrayList<Int>) {
        when (evaluateMove(distanceToApple, distanceToDeath)) {
            Direction.LEFT -> positionX -= 1
            Direction.RIGHT -> positionX += 1
            Direction.DOWN -> positionY += 1
            Direction.UP -> positionY -= 1
        }
    }

    /**
     * Detect wall collision.
     *
     * @return true if eater crashed
     *         false if not
     */
    fun crashedToWall(boardState: Array<Array<Int>>, wallMark: Int): Boolean {
        return boardState[positionX][positionY] == wallMark
    }

    /**
     * Friendly fire
     */
    fun crashedToEater(otherEaters: ArrayList<Eater>): Boolean {
        for (otherEater in otherEaters) {
            if (otherEater == this) continue

            if ((this.positionX == otherEater.positionX) && this.positionY == otherEater.positionY) {
                return true
            }
        }
        return false
    }

    /**
     * Out of steps
     */
    fun isExhausted(): Boolean {
        return steps > maxSteps
    }
}