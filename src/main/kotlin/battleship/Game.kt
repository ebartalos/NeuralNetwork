package battleship

import ai.Network
import battleship.gui.GUI
import kotlin.random.Random

class Game(private val agent: Network, private val sideLength: Int) {

    private val visibleBoardState = Array(sideLength) { Array(sideLength) { emptyMark } }
    private val shipPositions = arrayListOf<Pair<Int, Int>>()

    private val delay: Long
        get() = 50L


    private val emptyMark = 0
    private val shipMark = 1
    private val nonHitMark = 2


    init {
        setShipPositions()
    }

    fun play(useGUI: Boolean = false){
        lateinit var gui: GUI
        if (useGUI) {
            gui = GUI(visibleBoardState)
            gui.isVisible = true
        }

        var score = 0

        while (shipPositions.isNotEmpty()) {
            fire(Pair(Random.nextInt(12), Random.nextInt(12)))
            gui.update(visibleBoardState)
            Thread.sleep(delay)
        }


        if (useGUI) gui.quit()
    }

    private fun setShipPositions() {
        shipPositions.add(Pair(5, 5))
    }

    private fun fire(spot: Pair<Int, Int>) {
        for (ship in shipPositions) {
            if (spot.first == ship.first && spot.second == ship.second) {
                visibleBoardState[spot.first][spot.second] = shipMark
                shipPositions.remove(ship)
                return
            }
        }
        visibleBoardState[spot.first][spot.second] = nonHitMark
    }
}