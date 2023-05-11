package eater

import eater.gui.GUI
import kotlin.random.Random

class Game {
   private val sideLength = 15
    private val board = Array(sideLength) { Array(sideLength) { 0 } }

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



        // set eater and apple position
//        while ((eaterLocationX == appleLocationX) && (eaterLocationY == appleLocationY)) {
//            appleLocationX = Random.nextInt(1, sideLength - 1)
//        }

        updateBoard()
    }

    fun play(eaters: ArrayList<Eater>, maxFitness: Int, useGUI: Boolean = false): Int {
        this.eaters = eaters

        for (eater in eaters){
            eater.eaterLocationX = Random.nextInt(1, sideLength - 1)
            eater.eaterLocationY = Random.nextInt(1, sideLength - 1)

        }

        lateinit var gui: GUI
        if (useGUI) {
            gui = GUI(sideLength)
            gui.isVisible = true
        }

        var score = 0

        for (eater in eaters) {
            while (eater.steps < eater.maxSteps) {
                if (useGUI) {
                    gui.update(
                        eaters,
                        arrayListOf(appleLocationX, appleLocationY)
                    )
                    Thread.sleep(eater.delay)
                }

                eater.move()
                eater.steps += 1

                if (eater.isEaterDead()) {
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
            }

            if (useGUI) gui.quit()
//            return scoreFormula(score, eater.steps)
        }
        return 0
    }

    private fun isAppleEaten(eater:Eater): Boolean {
        return ((eater.eaterLocationX == appleLocationX) && (eater.eaterLocationY == appleLocationY))
    }


    private fun setRandomApplePosition() {
        board[appleLocationX][appleLocationY] = emptyMark
//        while ((eaterLocationX == appleLocationX) && (eaterLocationY == appleLocationY)) {
            appleLocationX = Random.nextInt(1, sideLength - 1)
            appleLocationY = Random.nextInt(1, sideLength - 1)
//        }
        updateBoard()
    }

    private fun updateBoard() {
        for (eater in eaters){
            board[eater.eaterLocationX][eater.eaterLocationY] = eaterMark
        }
        board[appleLocationX][appleLocationY] = appleMark
    }

    private fun scoreFormula(score: Int, steps: Int): Int {
        return (score * 1000) + steps
    }

}