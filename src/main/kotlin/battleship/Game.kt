package battleship

import ai.Network
import battleship.gui.GUI

class Game(private val agent: Network, private val sideLength: Int) {

    private val boardState = Array(sideLength) { Array(sideLength) { emptyMark } }
    private val shipPositions = arrayListOf<Pair<Int, Int>>()

    private val delay: Long
        get() = 50L

    private val emptyMark = 0
    private val shipMark = 1
    private val nonHitMark = 2


    init {
        setShipPositions()
    }

    fun play(useGUI: Boolean = false): Int {
        lateinit var gui: GUI
        if (useGUI) {
            gui = GUI(boardState)
            gui.isVisible = true
        }

        var score = 10000

        while (shipPositions.isNotEmpty()) {
            val result = transform(evaluateMove())
            fire(result)
            score -= 1

            if (useGUI) {
                gui.update(boardState)
                Thread.sleep(delay)
            }
        }


        if (useGUI) gui.quit()
        return score
    }

    /**
     * Calculate next move.
     **
     * @return direction to move
     */
    private fun evaluateMove(): Int {
        val inputs = arrayListOf<Double>()

        for (x in 0 until sideLength) {
            for (y in 0 until sideLength) {
                inputs.add(boardState[x][y].toDouble() + 1)
            }
        }

        agent.setInputs(inputs)
        agent.propagate()
        val softmaxOutput = agent.softmaxOutput()

        val evaluationMatrix = mutableMapOf<Int, Double>()

        for (i in 0 until 144) {
            evaluationMatrix[i] = softmaxOutput[i]
        }

        val sortedResult = evaluationMatrix.toList().sortedBy { (_, value) -> value }.reversed()

        for (result in sortedResult) {
            if (boardState[transform(result.first).first][transform(result.first).second] == emptyMark) {
                return result.first
            }
        }
        throw Exception("v pici")
    }

    private fun transform(position: Int): Pair<Int, Int> {
        return Pair(position / 12, position % 12)
    }

    private fun setShipPositions() {
        shipPositions.add(Pair(5, 5))
        shipPositions.add(Pair(5, 6))
    }

    private fun fire(spot: Pair<Int, Int>) {
        for (ship in shipPositions) {
            if (spot.first == ship.first && spot.second == ship.second) {
                boardState[spot.first][spot.second] = shipMark
                shipPositions.remove(ship)
                return
            }
        }
        boardState[spot.first][spot.second] = nonHitMark
    }
}