package snake

import ai.Network
import java.awt.Insets
import java.awt.Toolkit
import javax.swing.JFrame
import kotlin.math.abs


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

    fun fitness(): Int {
        return (board.score * 10000) + board.moveTimer
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
        val distanceToApple = board.distanceToApple()
        val distanceToWalls = board.distanceToWalls()

        val inputs = arrayListOf(
            abs(distanceToApple[0].toDouble()) / board.height,
            abs(distanceToApple[1].toDouble()) / board.height,
            abs(distanceToApple[2].toDouble()) / board.height,
            abs(distanceToApple[3].toDouble()) / board.height,
            abs(distanceToWalls[0].toDouble()) / board.height,
            abs(distanceToWalls[1].toDouble()) / board.height,
            abs(distanceToWalls[2].toDouble()) / board.height,
            abs(distanceToWalls[3].toDouble()) / board.height,
        )

        network.setInputs(inputs)
        network.evaluate()
        val softmaxOutput = network.softmaxOutput()

        val evaluationMatrix = mutableMapOf<Board.Direction, Double>()
        evaluationMatrix[Board.Direction.LEFT] = softmaxOutput[0]
        evaluationMatrix[Board.Direction.RIGHT] = softmaxOutput[1]
        evaluationMatrix[Board.Direction.UP] = softmaxOutput[2]
        evaluationMatrix[Board.Direction.DOWN] = softmaxOutput[3]

        val sortedResult = evaluationMatrix.toList().sortedBy { (_, value) -> value }
        val result = sortedResult.last().first

        board.changeDirection(result)
        board.oneStep()
    }
}