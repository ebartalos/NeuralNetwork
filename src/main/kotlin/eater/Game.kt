package eater

import eater.gui.GUI
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class Game(private val eater: Eater, private val sideLength: Int) {

    private val boardState = Array(sideLength) { Array(sideLength) { 0 } }
    private val delay: Long
        get() = 50L

    private var appleLocationX: Int = 4
    private var appleLocationY: Int = 4

    private val emptyMark = 0
    private val wallMark = 1
    private val eaterMark = 2
    private val appleMark = 3

    private val criticalPositions = Stack<Pair<Int, Int>>()

    init {
        drawWalls()
        eater.setRandomPosition(sideLength)
        updateBoard()
    }

    fun play(maxFitness: Int, useGUI: Boolean = false): Int {
        lateinit var gui: GUI
        if (useGUI) {
            gui = GUI(boardState)
            gui.isVisible = true
        }

        var score = 0
        val eatersFitness = mutableMapOf<Eater, Int>()

        while (eater.isAlive) {
            updateBoard()
            if (useGUI) {
                gui.update(boardState)
                Thread.sleep(delay)
            }

            eater.move(distanceToApple(eater), distanceToWalls(eater))
            eater.energy -= 1

            if (eater.crashedToWall(boardState, wallMark) || eater.isExhausted()) {
                eatersFitness[eater] = score
                killEater(eater)
                break
            }

            if (isAppleEaten(eater)) {
                score += 1000
                score += eater.energy
                eater.replenishEnergy()
                setRandomApplePosition()
            }

            if (score >= maxFitness) {
                eatersFitness[eater] = score
                return score
            }
        }
        if (useGUI) gui.quit()

        val bestEater = eatersFitness.maxByOrNull { it.value }
        return bestEater!!.value
    }

    private fun drawWalls() {
        for (index in 0 until sideLength) {
            boardState[0][index] = wallMark
            boardState[sideLength - 1][index] = wallMark
            boardState[index][0] = wallMark
            boardState[index][sideLength - 1] = wallMark
        }
    }

    /**
     * Detect apple collision.
     *
     * @return true if apple is eaten
     *         false if not
     */
    private fun isAppleEaten(eater: Eater): Boolean {
        return ((eater.positionX == appleLocationX) && (eater.positionY == appleLocationY))
    }

    /**
     * Set random position for apple - O - that is inside of board.
     */
    private fun setRandomApplePosition() {
        boardState[appleLocationX][appleLocationY] = emptyMark

        if (criticalPositions.isNotEmpty()) {
            val position = criticalPositions.pop()
            appleLocationX = position.first
            appleLocationY = position.second
        } else {
            appleLocationX = Random.nextInt(1, sideLength - 1)
            appleLocationY = Random.nextInt(1, sideLength - 1)
        }

        updateBoard()
    }

    /**
     * Update position of eater and apple on the board.
     */
    private fun updateBoard() {
        drawWalls()

        for (x in 1 until sideLength - 1) {
            for (y in 1 until sideLength - 1) {
                if (boardState[x][y] != wallMark) {
                    boardState[x][y] = 0
                }
            }
        }

        boardState[eater.positionX][eater.positionY] = eaterMark

        boardState[appleLocationX][appleLocationY] = appleMark
    }

    /**
     * Calculate distance to the apple.
     *
     * @return array of distances - 4 directions
     */
    private fun distanceToApple(eater: Eater): ArrayList<Int> {
        val distance = arrayListOf<Int>()
        val distanceX = eater.positionX - appleLocationX
        if (distanceX < 0) {
            distance.add(0)
            distance.add(abs(distanceX))
        } else {
            distance.add(abs(distanceX))
            distance.add(0)
        }

        val distanceY = eater.positionY - appleLocationY
        if (distanceY < 0) {
            distance.add(0)
            distance.add(abs(distanceY))
        } else {
            distance.add(abs(distanceY))
            distance.add(0)
        }
        return distance
    }

    private fun distanceToWalls(eater: Eater): ArrayList<Int> {
        val distances = arrayListOf<Int>()

        var leftWallDistance = sideLength
        for (x in 0 until eater.positionX) {
            if (boardState[x][eater.positionY] == wallMark) {
                leftWallDistance = abs(x - eater.positionX)
            }
        }
        distances.add(leftWallDistance)

        var rightWallDistance = sideLength
        for (x in eater.positionX until sideLength) {
            if (boardState[x][eater.positionY] == wallMark) {
                rightWallDistance = abs(x - eater.positionX)
                break
            }
        }
        distances.add(rightWallDistance)

        var topWallDistance = sideLength
        for (y in 0 until eater.positionY) {
            if (boardState[eater.positionX][y] == wallMark) {
                topWallDistance = abs(y - eater.positionY)
            }
        }
        distances.add(topWallDistance)

        var bottomWallDistance = sideLength
        for (y in eater.positionY until sideLength) {
            if (boardState[eater.positionX][y] == wallMark) {
                bottomWallDistance = abs(y - eater.positionY)
                break
            }
        }
        distances.add(bottomWallDistance)

        return distances
    }

    private fun killEater(eater: Eater) {
        eater.isAlive = false
        boardState[eater.positionX][eater.positionY] = emptyMark
    }
}