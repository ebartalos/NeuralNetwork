package eater

import ai.Network
import java.io.File
import kotlin.math.abs
import kotlin.random.Random

class ConsoleEater {
    // 15x15
    private val size = 15
    private val board = Array(size) { Array(size) { 0 } }

    private val emptyMark = 0
    private val wallMark = 1
    private val eaterMark = 2
    private val appleMark = 3

    private var eaterLocationX: Int = Random.nextInt(1, size - 2)
    private var eaterLocationY: Int = Random.nextInt(1, size - 2)
    private var appleLocationX: Int = Random.nextInt(2, size - 3)
    private var appleLocationY: Int = Random.nextInt(2, size - 3)

    private var score = 0
    private var maxSteps = 100


    init {
        // draw walls
        for (index in 0 until size) {
            board[0][index] = wallMark
            board[size - 1][index] = wallMark
            board[index][0] = wallMark
            board[index][size - 1] = wallMark
        }

        // set eater and apple position
        while ((eaterLocationX == appleLocationX) && (eaterLocationY == appleLocationY)) {
            appleLocationX = Random.nextInt(2, size - 3)
        }

        updateBoard()
    }

    /**
     * Play 1 game, until eater crashes or runs out of steps
     *
     * @param network neural network playing the game
     * @param maxFitness limit when should game end (prevents infinite game)
     * @param printBoard should board be printed into the console
     * @param saveToFile should board be saved into the file
     *
     * @return fitness reached
     */
    fun play(network: Network, maxFitness: Int, printBoard: Boolean = false, saveToFile: Boolean = false): Int {
        val file = File("bestSnakeTest.txt")
        if (saveToFile) {
            file.writeText("")
        }

        var steps = 0
        while (steps < maxSteps) {
            if (printBoard) printBoard()
            if (saveToFile) saveBoardStatusToFile(file)

            move(evaluateMove(network))
            steps += 1

            if (isEaterDead()) {
                break
            }

            if (isAppleEaten()) {
                score += 1
                maxSteps += 100
                setRandomApplePosition()
            }
            if ((score * 1000) + steps >= maxFitness) {
                break
            }
        }
        return (score * 1000) + steps
    }

    private fun evaluateMove(network: Network): Direction {
        val distanceToApple = distanceToApple()
        val distanceToWalls = distanceToWalls()

        val inputs = arrayListOf(
            (distanceToApple[0].toDouble()),
            (distanceToApple[1].toDouble()),
            (distanceToApple[2].toDouble()),
            (distanceToApple[3].toDouble()),
            (distanceToWalls[0].toDouble()),
            (distanceToWalls[1].toDouble()),
            (distanceToWalls[2].toDouble()),
            (distanceToWalls[3].toDouble()),
        )

        network.setInputs(inputs)
        network.evaluate()
        val softmaxOutput = network.softmaxOutput()

        val evaluationMatrix = mutableMapOf<Direction, Double>()
        evaluationMatrix[Direction.LEFT] = softmaxOutput[0]
        evaluationMatrix[Direction.RIGHT] = softmaxOutput[1]
        evaluationMatrix[Direction.UP] = softmaxOutput[2]
        evaluationMatrix[Direction.DOWN] = softmaxOutput[3]

        val sortedResult = evaluationMatrix.toList().sortedBy { (_, value) -> value }
        return sortedResult.last().first
    }

    /**
     * Print current board status to console
     */
    private fun printBoard() {
        val translator = HashMap<Int, String>()
        translator[0] = " "
        translator[1] = "*"
        translator[2] = "X"
        translator[3] = "O"

        for (row in 0 until size) {
            for (column in 0 until size) {
                print(translator[board[column][row]])
            }
            println()
        }
    }

    /**
     * Save current board status to file
     */
    private fun saveBoardStatusToFile(file: File) {
        val translator = HashMap<Int, String>()
        translator[0] = " "
        translator[1] = "*"
        translator[2] = "X"
        translator[3] = "O"

        for (row in 0 until size) {
            for (column in 0 until size) {
                translator[board[column][row]]?.let { file.appendText(it) }
            }
            file.appendText("\n")
        }
    }

    private fun distanceToApple(): ArrayList<Int> {
        val distance = arrayListOf<Int>()
        val distanceX = eaterLocationX - appleLocationX
        if (distanceX < 0) {
            distance.add(0)
            distance.add(abs(distanceX))
        } else {
            distance.add(abs(distanceX))
            distance.add(0)
        }

        val distanceY = eaterLocationY - appleLocationY
        if (distanceY < 0) {
            distance.add(0)
            distance.add(abs(distanceY))
        } else {
            distance.add(abs(distanceY))
            distance.add(0)
        }
        return distance
    }

    private fun distanceToWalls(): ArrayList<Int> {
        val distance = arrayListOf<Int>()
        distance.add(abs(eaterLocationX))
        distance.add(abs((size - 1) - eaterLocationX))
        distance.add(abs(eaterLocationY))
        distance.add(abs((size - 1) - eaterLocationY))
        return distance
    }

    private enum class Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private fun move(direction: Direction) {
        board[eaterLocationX][eaterLocationY] = emptyMark
        if (direction == Direction.LEFT) {
            eaterLocationX -= 1
        } else if (direction == Direction.RIGHT) {
            eaterLocationX += 1
        } else if (direction == Direction.UP) {
            eaterLocationY -= 1
        } else if (direction == Direction.DOWN) {
            eaterLocationY += 1
        }
        updateBoard()
    }

    private fun isEaterDead(): Boolean {
        return (eaterLocationX < 1)
                || (eaterLocationY < 1)
                || (eaterLocationX >= size - 1)
                || (eaterLocationY >= size - 1)
    }

    private fun isAppleEaten(): Boolean {
        return ((eaterLocationX == appleLocationX) && (eaterLocationY == appleLocationY))
    }

    private fun setRandomApplePosition() {
        board[appleLocationX][appleLocationY] = emptyMark
        while ((eaterLocationX == appleLocationX) && (eaterLocationY == appleLocationY)) {
            appleLocationX = Random.nextInt(2, size - 3)
            appleLocationY = Random.nextInt(2, size - 3)
        }
        updateBoard()
    }

    private fun updateBoard() {
        board[eaterLocationX][eaterLocationY] = eaterMark
        board[appleLocationX][appleLocationY] = appleMark
    }
}