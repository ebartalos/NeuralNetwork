package snake

import ai.Network
import javax.swing.JFrame

class Snake(val network: Network) : JFrame() {
    private lateinit var board: Board

    init {
        initUI()
    }

    private fun initUI() {
        board = Board()
        add(board)

        title = "Snake"
        isResizable = false
        pack()

        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    fun changeDirection() {
//        val inputs = arrayListOf<Int>(
//            board.headPositionX,
//            board
//        )
//        network.setInputs()
//        val p = 4
    }
}