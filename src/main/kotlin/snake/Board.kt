package snake

import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.ImageIcon
import javax.swing.JPanel


class Board : JPanel(), ActionListener {

    private val boardWidth = 200
    private val boardHeight = 200
    private val dotSize = 10
    private val allDots = 900
    private val randPos = 29

    private val allJointsX = IntArray(allDots)
    private val allJointsY = IntArray(allDots)

    var snakeBodyLength: Int = 0
    var applePositionX: Int = 30
    private var applePositionY: Int = 80

    private var leftDirection = false
    private var rightDirection = true
    private var upDirection = false
    private var downDirection = false

    private var inGame = true
    var isGameOver = false

    var moveTimer = 1000

    private var ball: Image? = null
    private var apple: Image? = null
    private var head: Image? = null

    init {
        background = Color.black
        isFocusable = true

        preferredSize = Dimension(boardWidth, boardHeight)
        loadImages()
        initGame()
    }

    fun distanceToApple(): ArrayList<Int> {
        val distance = arrayListOf<Int>()
        distance.add(allJointsX[0] - applePositionX)
        distance.add(allJointsY[0] - applePositionY)

        return distance
    }

    fun distanceToWalls(): ArrayList<Int> {
        val distance = arrayListOf<Int>()
        distance.add(allJointsX[0])
        distance.add(allJointsX[0] - boardHeight)
        distance.add(allJointsY[0])
        distance.add(allJointsY[0] - boardWidth)

        return distance
    }

    fun directionMatrix(): ArrayList<Int> {
        val directionMatrix = arrayListOf<Int>()
        directionMatrix.add(if (leftDirection) 0 else 1)
        directionMatrix.add(if (rightDirection) 0 else 1)
        directionMatrix.add(if (upDirection) 0 else 1)
        directionMatrix.add(if (downDirection) 0 else 1)

        return directionMatrix
    }

    private fun loadImages() {
        val dot = ImageIcon("src/main/resources/dot.png")
        ball = dot.image

        val apple = ImageIcon("src/main/resources/apple.png")
        this.apple = apple.image

        val head = ImageIcon("src/main/resources/head.png")
        this.head = head.image
    }

    private fun initGame() {
        snakeBodyLength = 3

        for (z in 0 until snakeBodyLength) {
            allJointsX[z] = (Math.random() * boardWidth).toInt() - z * 10
            allJointsY[z] = (Math.random() * boardHeight).toInt()
        }

        setRandomPositionForApple()
    }

    public override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)
        doDrawing(graphics)
    }

    private fun doDrawing(graphics: Graphics) {
        if (inGame) {
            graphics.drawImage(apple, applePositionX, applePositionY, this)

            for (z in 0 until snakeBodyLength) {
                if (z == 0) {
                    graphics.drawImage(head, allJointsX[z], allJointsY[z], this)
                } else {
                    graphics.drawImage(ball, allJointsX[z], allJointsY[z], this)
                }
            }

            Toolkit.getDefaultToolkit().sync()

        } else {
            gameOver(graphics)
        }
    }

    private fun gameOver(graphics: Graphics) {
        val message = "Game Over"
        val small = Font("Helvetica", Font.BOLD, 14)
        val fontMetrics = getFontMetrics(small)

        val rh = RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
        )

        rh[RenderingHints.KEY_RENDERING] = RenderingHints.VALUE_RENDER_QUALITY

        (graphics as Graphics2D).setRenderingHints(rh)

        graphics.color = Color.white
        graphics.font = small
        graphics.drawString(
            message, (boardWidth - fontMetrics.stringWidth(message)) / 2, boardHeight / 2
        )
        isGameOver = true
    }

    private fun checkAppleCollision() {
        if (allJointsX[0] == applePositionX && allJointsY[0] == applePositionY) {
            snakeBodyLength++
            setRandomPositionForApple()
            println("Podarilo")
        }
    }

    private fun move() {
        for (z in snakeBodyLength downTo 1) {
            allJointsX[z] = allJointsX[z - 1]
            allJointsY[z] = allJointsY[z - 1]
        }

        if (leftDirection) {
            allJointsX[0] -= dotSize
        } else if (rightDirection) {
            allJointsX[0] += dotSize
        } else if (upDirection) {
            allJointsY[0] -= dotSize
        } else if (downDirection) {
            allJointsY[0] += dotSize
        }

        moveTimer -= 1
    }

    private fun checkWallCollision() {
        for (z in snakeBodyLength downTo 1) {
            if (z > 4 && allJointsX[0] == allJointsX[z] && allJointsY[0] == allJointsY[z]) {
                inGame = false
            }
        }

        if (allJointsY[0] >= boardHeight) {
            inGame = false
        }

        if (allJointsY[0] < 0) {
            inGame = false
        }

        if (allJointsX[0] >= boardWidth) {
            inGame = false
        }

        if (allJointsX[0] < 0) {
            inGame = false
        }
    }

    private fun setRandomPositionForApple() {
        var r = (Math.random() * randPos).toInt()
        applePositionX = r * dotSize

        r = (Math.random() * randPos).toInt()
        applePositionY = r * dotSize
    }

    override fun actionPerformed(actionEvent: ActionEvent) {}

    fun oneStep() {
        if (inGame) {
            checkAppleCollision()
            checkWallCollision()
            move()

            if (moveTimer <= 0) {
                inGame = false
            }
        }

        repaint()
    }

    enum class Direction {
        LEFT, RIGHT, UP, DOWN
    }

    fun changeDirection(direction: Direction) {
        if (direction == Direction.LEFT && !rightDirection) {
            leftDirection = true
            upDirection = false
            downDirection = false
        } else if (direction == Direction.RIGHT && !leftDirection) {
            rightDirection = true
            upDirection = false
            downDirection = false
        } else if (direction == Direction.UP && !downDirection) {
            upDirection = true
            rightDirection = false
            leftDirection = false
        } else if (direction == Direction.DOWN && !upDirection) {
            downDirection = true
            rightDirection = false
            leftDirection = false
        }
    }
}
