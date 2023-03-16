package snake

import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.ImageIcon
import javax.swing.JPanel
import javax.swing.Timer


class Board : JPanel(), ActionListener {

    private val boardWidth = 300
    private val boardHeight = 300
    private val dotSize = 10
    private val allDots = 900
    private val randPos = 29
    private val delay = 100

    val headPositionX = IntArray(allDots) // ai input
    val headPositionY = IntArray(allDots) // ai input

    var snakeBodyLength: Int = 0 // ai input
    var applePositionX: Int = 0 // ai inpuy
    var applePositionY: Int = 0 // ai input

    private var leftDirection = false
    private var rightDirection = true
    private var upDirection = false
    private var downDirection = false
    private var inGame = true

    private var timer: Timer? = null
    private var ball: Image? = null
    private var apple: Image? = null
    private var head: Image? = null

    init {
//        addKeyListener(TAdapter())
        background = Color.black
        isFocusable = true

        preferredSize = Dimension(boardWidth, boardHeight)
        loadImages()
        initGame()
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
            headPositionX[z] = 50 - z * 10
            headPositionY[z] = 50
        }

        locateApple()

        timer = Timer(delay, this)
        timer!!.start()
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
                    graphics.drawImage(head, headPositionX[z], headPositionY[z], this)
                } else {
                    graphics.drawImage(ball, headPositionX[z], headPositionY[z], this)
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
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )

        rh[RenderingHints.KEY_RENDERING] = RenderingHints.VALUE_RENDER_QUALITY

        (graphics as Graphics2D).setRenderingHints(rh)

        graphics.color = Color.white
        graphics.font = small
        graphics.drawString(
            message, (boardWidth - fontMetrics.stringWidth(message)) / 2,
            boardHeight / 2
        )
    }

    private fun checkApple() {
        if (headPositionX[0] == applePositionX && headPositionY[0] == applePositionY) {
            snakeBodyLength++
            locateApple()
        }
    }

    private fun move() {
        for (z in snakeBodyLength downTo 1) {
            headPositionX[z] = headPositionX[z - 1]
            headPositionY[z] = headPositionY[z - 1]
        }

        if (leftDirection) {
            headPositionX[0] -= dotSize
        }

        if (rightDirection) {
            headPositionX[0] += dotSize
        }

        if (upDirection) {
            headPositionY[0] -= dotSize
        }

        if (downDirection) {
            headPositionY[0] += dotSize
        }
    }

    private fun checkCollision() {
        for (z in snakeBodyLength downTo 1) {
            if (z > 4 && headPositionX[0] == headPositionX[z] && headPositionY[0] == headPositionY[z]) {
                inGame = false
            }
        }

        if (headPositionY[0] >= boardHeight) {
            inGame = false
        }

        if (headPositionY[0] < 0) {
            inGame = false
        }

        if (headPositionX[0] >= boardWidth) {
            inGame = false
        }

        if (headPositionX[0] < 0) {
            inGame = false
        }

        if (!inGame) {
            timer!!.stop()
        }
    }

    private fun locateApple() {
        var r = (Math.random() * randPos).toInt()
        applePositionX = r * dotSize

        r = (Math.random() * randPos).toInt()
        applePositionY = r * dotSize
    }

    override fun actionPerformed(actionEvent: ActionEvent) {
        if (inGame) {
            checkApple()
            checkCollision()
            move() // todo here do some AI input
        }

        repaint()
    }

    enum class Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
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

//    private inner class TAdapter : KeyAdapter() {
//        override fun keyPressed(keyEvent: KeyEvent?) {
//            val key = keyEvent!!.keyCode
//
//            if (key == KeyEvent.VK_LEFT && !rightDirection) {
//                leftDirection = true
//                upDirection = false
//                downDirection = false
//            }
//
//            if (key == KeyEvent.VK_RIGHT && !leftDirection) {
//                rightDirection = true
//                upDirection = false
//                downDirection = false
//            }
//
//            if (key == KeyEvent.VK_UP && !downDirection) {
//                upDirection = true
//                rightDirection = false
//                leftDirection = false
//            }
//
//            if (key == KeyEvent.VK_DOWN && !upDirection) {
//                downDirection = true
//                rightDirection = false
//                leftDirection = false
//            }
//        }
//    }
}