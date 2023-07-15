package eater

import eater.gui.GUI
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

class Game(private val eaters: ArrayList<Eater>, private val sideLength: Int) {

    private val boardState = Array(sideLength) { Array(sideLength) { 0 } }
    private val delay: Long
        get() = 50L / howManyEatersAreAlive()

    private var appleLocationX: Int = Random.nextInt(1, sideLength - 1)
    private var appleLocationY: Int = Random.nextInt(1, sideLength - 1)

    private val emptyMark = 0
    private val wallMark = 1
    private val eaterMark = 2
    private val appleMark = 3

    init {
        drawWalls()

        eaters.forEach { eater -> eater.setRandomPosition(sideLength) }

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

        while (eaters.isNotEmpty()) {
            for (eater in eaters) {
                updateBoard()
                if (useGUI) {
                    gui.update(boardState)
                    Thread.sleep(delay)
                }

                eater.move(distanceToApple(eater), distanceToDeath(eater, eaters))
                eater.steps += 1

                if (eater.crashedToEater(eaters) || eater.crashedToWall(boardState, wallMark) || eater.isExhausted()) {
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

    private fun drawWalls() {
        for (index in 0 until sideLength) {
            boardState[0][index] = wallMark
            boardState[sideLength - 1][index] = wallMark
            boardState[index][0] = wallMark
            boardState[index][sideLength - 1] = wallMark
        }
        boardState[sideLength / 2][sideLength / 2] = wallMark
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
        appleLocationX = Random.nextInt(1, sideLength - 1)
        appleLocationY = Random.nextInt(1, sideLength - 1)

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

        boardState[sideLength / 2][sideLength / 2] = wallMark

        for (eater in eaters) {
            boardState[eater.positionX][eater.positionY] = eaterMark
        }
        boardState[appleLocationX][appleLocationY] = appleMark
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
     * Calculate distance to walls or nearest eater - collision with either kills
     *
     * @param eater current eater
     * @param eaters all eaters
     *
     * @return array of distances - 4 directions
     */
    private fun distanceToDeath(eater: Eater, eaters: ArrayList<Eater>): ArrayList<Int> {
        val distance = distanceToWalls(eater)

        // todo remove
        val distanceCorrect = arrayListOf<Int>()
        // left wall
        distanceCorrect.add(abs(eater.positionX))

        // right wall
        distanceCorrect.add(abs((sideLength - 1) - eater.positionX))

        // top wall
        distanceCorrect.add(abs(eater.positionY))

        // bottom wall
        distanceCorrect.add(abs((sideLength - 1) - eater.positionY))

        for (otherEater in eaters) {
            if (eater == otherEater) continue

            if (eater.positionX == otherEater.positionX) {
                if (eater.positionY > otherEater.positionY) {
                    distance[2] = min(distance[2], abs(eater.positionY - otherEater.positionY))
                } else {
                    distance[3] = min(distance[3], abs(eater.positionY - otherEater.positionY))
                }
            }

            if (eater.positionY == otherEater.positionY) {
                if (eater.positionX > otherEater.positionX) {
                    distance[0] = min(distance[0], abs(eater.positionX - otherEater.positionX))
                } else {
                    distance[1] = min(distance[1], abs(eater.positionX - otherEater.positionX))
                }
            }
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

    private fun howManyEatersAreAlive(): Int {
        var count = 0
        for (eater in eaters) {
            if (eater.isAlive) count += 1
        }
        return count
    }

    private fun killEater(eater: Eater) {
        eater.isAlive = false
        boardState[eater.positionX][eater.positionY] = emptyMark
        eaters.remove(eater)
    }
}