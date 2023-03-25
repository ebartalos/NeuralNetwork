package eater

import Constants
import ai.Network
import java.io.File
import kotlin.math.abs
import kotlin.random.Random

class ConsoleEater {
    /***
     * 0 - empty space
     * 1 - wall
     * 2 - snake
     * 3 - apple
     */
    private val size = 15
    private val board = Array(size) { Array(size) { 0 } }

    private var snakeLocationX: Int = Random.nextInt(1, size - 2)
    private var snakeLocationY: Int = Random.nextInt(1, size - 2)
    private var appleLocationX: Int = Random.nextInt(2, size - 3)
    private var appleLocationY: Int = Random.nextInt(2, size - 3)

    private var score = 0
    private var maxSteps = 100


    init {
        // draw walls
        for (index in 0 until size) {
            board[0][index] = 1
            board[size - 1][index] = 1
            board[index][0] = 1
            board[index][size - 1] = 1
        }

        while ((snakeLocationX == appleLocationX) && (snakeLocationY == appleLocationY)) {
            appleLocationX = Random.nextInt(2, size - 3)
        }
        board[snakeLocationX][snakeLocationY] = 2
        board[appleLocationX][appleLocationY] = 3
    }

    fun play(network: Network, printBoard: Boolean = false, saveToFile: Boolean = false): Int {
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

            if (isSnakeDead()) {
                break
            }

            if (isAppleEaten()) {
                score += 1
                maxSteps += 100
                setRandomApplePosition()
            }
            if ((score * 1000) + steps >= Constants.MAX_FITNESS) {
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
        val distanceX = snakeLocationX - appleLocationX
        if (distanceX < 0) {
            distance.add(0)
            distance.add(abs(distanceX))
        } else {
            distance.add(abs(distanceX))
            distance.add(0)
        }

        val distanceY = snakeLocationY - appleLocationY
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
        distance.add(abs(snakeLocationX))
        distance.add(abs((size - 1) - snakeLocationX))
        distance.add(abs(snakeLocationY))
        distance.add(abs((size - 1) - snakeLocationY))
        return distance
    }

    enum class Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private fun move(direction: Direction) {
        board[snakeLocationX][snakeLocationY] = 0
        if (direction == Direction.LEFT) {
            snakeLocationX -= 1
        } else if (direction == Direction.RIGHT) {
            snakeLocationX += 1
        } else if (direction == Direction.UP) {
            snakeLocationY -= 1
        } else if (direction == Direction.DOWN) {
            snakeLocationY += 1
        }
        updateBoard()
    }

    private fun isAppleEaten(): Boolean {
        return ((snakeLocationX == appleLocationX) && (snakeLocationY == appleLocationY))
    }

    private fun setRandomApplePosition() {
        board[appleLocationX][appleLocationY] = 0
        while ((snakeLocationX == appleLocationX) && (snakeLocationY == appleLocationY)) {
            appleLocationX = Random.nextInt(2, size - 3)
            appleLocationY = Random.nextInt(2, size - 3)
        }
        updateBoard()
    }

    private fun isSnakeDead(): Boolean {
        return (snakeLocationX < 1)
                || (snakeLocationY < 1)
                || (snakeLocationX >= size - 1)
                || (snakeLocationY >= size - 1)
    }

    private fun updateBoard() {
        board[snakeLocationX][snakeLocationY] = 2
        board[appleLocationX][appleLocationY] = 3
    }
}