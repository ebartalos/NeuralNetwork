package snake

import ai.Network
import javax.swing.JFrame

class Snake(private val network: Network) : JFrame() {
    lateinit var board: Board

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
        val inputs = arrayListOf(
            board.allJointsX[0],
            board.allJointsY[0],
            board.applePositionX,
            board.applePositionY,
            board.snakeBodyLength
        )
        network.setInputs(inputs)
        network.evaluate()

        val evaluationMatrix = mutableMapOf<Board.Direction, Double>()
        evaluationMatrix[Board.Direction.LEFT] = network.output()[0]
        evaluationMatrix[Board.Direction.RIGHT] = network.output()[1]
        evaluationMatrix[Board.Direction.UP] = network.output()[2]
        evaluationMatrix[Board.Direction.DOWN] = network.output()[3]

        val sortedResult = evaluationMatrix.toList().sortedBy { (_, value) -> value }
            .toMap()
        val result = sortedResult.keys.reversed()[3]

        board.changeDirection(result)
    }
}