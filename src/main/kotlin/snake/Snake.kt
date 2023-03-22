package snake

import ai.Network
import java.awt.Insets
import java.awt.Toolkit
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

        setLocationToTopRight(this)
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    private fun setLocationToTopRight(frame: JFrame) {
        val config = frame.graphicsConfiguration
        val bounds = config.bounds
        val insets: Insets = Toolkit.getDefaultToolkit().getScreenInsets(config)
        val x = bounds.x + bounds.width - insets.right - frame.width
        val y = bounds.y + insets.top
        frame.setLocation(x, y)
    }


    fun changeDirection() {
        val inputs = arrayListOf(
            board.allJointsX[0],
            board.allJointsY[0],
            board.applePositionX,
            board.applePositionY,
//            board.snakeBodyLength
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
        board.oneStep()
    }
}