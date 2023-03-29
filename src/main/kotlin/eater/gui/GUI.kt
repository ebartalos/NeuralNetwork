package eater.gui

import javax.swing.JFrame

class GUI(private val sideLength: Int) : JFrame() {

    private lateinit var board: Board

    init {
        initUI()
    }

    private fun initUI() {
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
}