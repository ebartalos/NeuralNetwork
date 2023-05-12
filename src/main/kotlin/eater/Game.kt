package eater

import eater.gui.GUI
import kotlin.math.abs
import kotlin.random.Random

class Game {
    private val sideLength = 15
    private val board = Array(sideLength) { Array(sideLength) { 0 } }
    private val delay: Long
        get() = 50L / howManyEatersAreAlive()

    private var eaters = arrayListOf<Eater>()

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

        updateBoard()
    }

    fun play(eaters: ArrayList<Eater>, maxFitness: Int, useGUI: Boolean = false): Int {
        this.eaters = eaters

        for (eater in eaters) {
            eater.positionX = Random.nextInt(1, sideLength - 1)
            eater.positionY = Random.nextInt(1, sideLength - 1)
        }

        lateinit var gui: GUI
        if (useGUI) {
            gui = GUI(sideLength)
            gui.isVisible = true
        }

        var score = 0

        while (allDead().not()) {
            for (eater in eaters) {
                if (eater.steps < eater.maxSteps) {
                    if (useGUI) {
                        gui.update(
                            eaters,
                            arrayListOf(appleLocationX, appleLocationY)
                        )
                        Thread.sleep(delay)
                    }

                    eater.move(distanceToApple(eater), distanceToWalls(eater))
                    eater.steps += 1

                    if (eater.isCrashed()) {
                        eater.isDead = true
                        break
                    }

                    if (isAppleEaten(eater)) {
                        score += 1
                        eater.steps = 0
                        setRandomApplePosition()
                    }
//                if (scoreFormula(score, eater.steps) >= maxFitness) {
//                    break
//                }
                } else {
                    eater.isDead = true
                }
// todo proper fitness
//            return scoreFormula(score, eater.steps)
            }
        }

        if (useGUI) gui.quit()
        return 0
    }

    private fun allDead(): Boolean {
        for (eater in eaters) {
            if (eater.isDead.not()) return false
        }
        return true
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
            if (eater.isDead.not()) count += 1
        }
        return count
    }
}