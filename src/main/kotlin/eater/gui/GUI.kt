package eater.gui

import eater.Eater
import javax.swing.JFrame

class GUI(sideLength: Int) : JFrame() {

    private var board: Board

    init {
        board = Board(sideLength = sideLength)
        add(board)

        title = "Eater"
        isResizable = false

        pack()

        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    fun update(eaters: ArrayList<Eater>, applePositions: ArrayList<Int>) {
        board.update(eaters, applePositions)
    }

    fun quit() {
        this.dispose()
    }
}