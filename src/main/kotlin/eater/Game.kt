package eater

import eater.gui.GUI
import kotlin.math.abs
import kotlin.random.Random

class Game(private val eaters: ArrayList<Eater>, private val sideLength: Int) {

    private val board = Array(sideLength) { Array(sideLength) { 0 } }
    private val delay: Long
        get() = 50L / howManyEatersAreAlive()

    private var appleLocationX: Int = Random.nextInt(1, sideLength - 1)
    private var appleLocationY: Int = Random.nextInt(1, sideLength - 1)

    private val emptyMark = 0
    private val wallMark = 1
    private val eaterMark = 2
    private val appleMark = 3

    init {
        // draw walls
        for (index in 0 until sideLength) {
            board[0][index] = wallMark
            board[sideLength - 1][index] = wallMark
            board[index][0] = wallMark
            board[index][sideLength - 1] = wallMark
        }
        eaters.forEach { eater -> eater.setPosition(sideLength) }

        updateBoard()
    }

    fun play(maxFitness: Int, useGUI: Boolean = false): Int {
        lateinit var gui: GUI
        if (useGUI) {
            gui = GUI(sideLength)
            gui.isVisible = true
        }

        var score = 0
        val eatersFitness = mutableMapOf<Eater, Int>()

        while (areAllEatersDead().not()) {
            for (eater in eaters) {
                if (useGUI) {
                    gui.update(
                        eaters,
                        arrayListOf(appleLocationX, appleLocationY)
                    )
                    Thread.sleep(delay)
                }

                eater.think(distanceToApple(eater), distanceToWalls(eater))
                eater.steps += 1

                if (eater.crashedToEater(eaters) || eater.crashedToWall(sideLength) || eater.isExhausted()) {
                    eatersFitness[eater] = scoreFormula(score, eater.steps)
                    score = 0
                    killEater(eater)
                    break
                }

                if (isAppleEaten(eater)) {
                    score += 1
                    eater.steps = 0
                    setRandomApplePosition()
                }

                if (scoreFormula(score, eater.steps) >= maxFitness) {
                    eatersFitness[eater] = scoreFormula(score, eater.steps)
                    return scoreFormula(score, eater.steps)
                }
            }
        }
        if (useGUI) gui.quit()

        val bestEater = eatersFitness.maxByOrNull { it.value }
        return bestEater!!.value
    }

    private fun areAllEatersDead(): Boolean {
        return eaters.size <= 0
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
        board[appleLocationX][appleLocationY] = emptyMark
        appleLocationX = Random.nextInt(1, sideLength - 1)
        appleLocationY = Random.nextInt(1, sideLength - 1)

        updateBoard()
    }

    /**
     * Update position of eater and apple on the board.
     */
    private fun updateBoard() {
        for (eater in eaters) {
            board[eater.positionX][eater.positionY] = eaterMark
        }
        board[appleLocationX][appleLocationY] = appleMark
    }

    /**
     * Formula for calculating score (fitness).
     */
    private fun scoreFormula(score: Int, steps: Int): Int {
        return (score * 1000) + steps
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

    /**
     * Calculate distance to walls.
     *
     * @return array of distances - 4 directions
     */
    private fun distanceToWalls(eater: Eater): ArrayList<Int> {
        val distance = arrayListOf<Int>()
        distance.add(abs(eater.positionX))
        distance.add(abs((sideLength - 1) - eater.positionX))
        distance.add(abs(eater.positionY))
        distance.add(abs((sideLength - 1) - eater.positionY))
        return distance
    }

    private fun howManyEatersAreAlive(): Int {
        var count = 0
        for (eater in eaters) {
            if (eater.isAlive) count += 1
        }
        return count
    }

    private fun killEater(eater: Eater) {
        eater.isAlive = false
        board[eater.positionX][eater.positionY] = emptyMark
        eaters.remove(eater)
    }
}