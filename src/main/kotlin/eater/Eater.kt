package eater

import ai.Network
import eater.gui.GUI
import java.io.File
import kotlin.math.abs
import kotlin.random.Random

class Eater {

    // board size (square) - 15x15
    private val sideLength = 15
    private val board = Array(sideLength) { Array(sideLength) { 0 } }

    private val emptyMark = 0
    private val wallMark = 1
    private val eaterMark = 2
    private val appleMark = 3

    private var eaterLocationX: Int = Random.nextInt(1, sideLength - 1)
    private var eaterLocationY: Int = Random.nextInt(1, sideLength - 1)
    private var appleLocationX: Int = Random.nextInt(1, sideLength - 1)
    private var appleLocationY: Int = Random.nextInt(1, sideLength - 1)

    private val stepsIncrement = 50
    private var maxSteps = stepsIncrement


    init {
        // draw walls
        for (index in 0 until sideLength) {
            board[0][index] = wallMark
            board[sideLength - 1][index] = wallMark
            board[index][0] = wallMark
            board[index][sideLength - 1] = wallMark
        }

        // set eater and apple position
        while ((eaterLocationX == appleLocationX) && (eaterLocationY == appleLocationY)) {
            appleLocationX = Random.nextInt(1, sideLength - 1)
        }

        updateBoard()
    }

    /**
     * Play 1 game, until eater crashes or runs out of steps.
     *
     * @param network neural network playing the game
     * @param maxFitness limit when should game end (prevents infinite game)
     * @param saveToFile should board be saved into the file
     * @param useGUI should be shown in GUI
     *
     * @return fitness reached
     */
    fun play(
        network: Network,
        maxFitness: Int,
        saveToFile: Boolean = false,
        useGUI: Boolean = false
    ): Int {
        val file = File("bestEaterTest.txt")
        if (saveToFile) {
            file.writeText("")
        }

        lateinit var gui: GUI
        if (useGUI) {
            gui = GUI(sideLength)
            gui.isVisible = true
        }

        var score = 0
        var steps = 0

        while (steps < maxSteps) {
            if (saveToFile) saveBoardStatusToFile(file)
            if (useGUI) {
                gui.update(arrayListOf(eaterLocationX, eaterLocationY, appleLocationX, appleLocationY))
                Thread.sleep(50)
            }

            move(evaluateMove(network))
            steps += 1

            if (isEaterDead()) {
                break
            }

            if (isAppleEaten()) {
                score += 1
                maxSteps += stepsIncrement
                setRandomApplePosition()
            }
            if (scoreFormula(score, steps) >= maxFitness) {
                break
            }
        }

        if (useGUI) gui.quit()
        return scoreFormula(score, steps)
    }

    /**
     * Formula for calculating score (fitness).
     */
    private fun scoreFormula(score: Int, steps: Int): Int {
        return (score * 1000) + steps
    }

    /**
     * Networks calculates next move.
     *
     * @param network neural network
     *
     * @return direction to move
     */
    private fun evaluateMove(network: Network): Direction {
        val distanceToApple = distanceToApple()
        val distanceToWalls = distanceToWalls()

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
     * Save current board status to file.
     */
    private fun saveBoardStatusToFile(file: File) {
        val translator = HashMap<Int, String>()
        translator[0] = " "
        translator[1] = "*"
        translator[2] = "X"
        translator[3] = "O"

        for (row in 0 until sideLength) {
            for (column in 0 until sideLength) {
                translator[board[column][row]]?.let { file.appendText(it) }
            }
            file.appendText("\n")
        }
    }

    /**
     * Calculate distance to the apple.
     *
     * @return array of distances - 4 directions
     */
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

    /**
     * Calculate distance to walls.
     *
     * @return array of distances - 4 directions
     */
    private fun distanceToWalls(): ArrayList<Int> {
        val distance = arrayListOf<Int>()
        distance.add(abs(eaterLocationX))
        distance.add(abs((sideLength - 1) - eaterLocationX))
        distance.add(abs(eaterLocationY))
        distance.add(abs((sideLength - 1) - eaterLocationY))
        return distance
    }

    private enum class Direction {
        LEFT, RIGHT, UP, DOWN
    }

    /**
     * Move eater on the board.
     */
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

    /**
     * Detect wall collision.
     *
     * @return true if eater crashed
     *         false if not
     */
    private fun isEaterDead(): Boolean {
        return (eaterLocationX < 1)
                || (eaterLocationY < 1)
                || (eaterLocationX >= sideLength - 1)
                || (eaterLocationY >= sideLength - 1)
    }

    /**
     * Detect apple collision.
     *
     * @return true if apple is eaten
     *         false if not
     */
    private fun isAppleEaten(): Boolean {
        return ((eaterLocationX == appleLocationX) && (eaterLocationY == appleLocationY))
    }

    /**
     * Set random position for apple - O - that is inside of board.
     */
    private fun setRandomApplePosition() {
        board[appleLocationX][appleLocationY] = emptyMark
        while ((eaterLocationX == appleLocationX) && (eaterLocationY == appleLocationY)) {
            appleLocationX = Random.nextInt(1, sideLength - 1)
            appleLocationY = Random.nextInt(1, sideLength - 1)
        }
        updateBoard()
    }

    /**
     * Update position of eater and apple on the board.
     */
    private fun updateBoard() {
        board[eaterLocationX][eaterLocationY] = eaterMark
        board[appleLocationX][appleLocationY] = appleMark
    }
}