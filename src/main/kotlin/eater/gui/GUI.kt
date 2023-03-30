package eater.gui

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

    fun update(positions: ArrayList<Int>) {
        board.update(positions)
    }

    fun quit() {
        this.dispose()
    }
}