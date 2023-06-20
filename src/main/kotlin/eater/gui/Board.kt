package eater.gui

import java.awt.*
import javax.swing.ImageIcon
import javax.swing.JPanel


class Board(val sideLength: Int) : JPanel() {
    private val dotSize = 25

    private val boardWidth = sideLength * dotSize
    private val boardHeight = sideLength * dotSize

    private var eaterX: Int = 0
    private var eaterY: Int = 0
    private var appleX: Int = 0
    private var appleY: Int = 0

    private var apple: Image? = null
    private var eater: Image? = null
    private var wall: Image? = null

    init {
        background = Color.black
        isFocusable = true

        preferredSize = Dimension(boardWidth, boardHeight)
        loadImages()
    }

    private fun loadImages() {
        eater = ImageIcon("src/main/resources/head.png")
            .image
            .getScaledInstance(dotSize, dotSize, Image.SCALE_SMOOTH)
        apple = ImageIcon("src/main/resources/apple.png")
            .image
            .getScaledInstance(dotSize, dotSize, Image.SCALE_SMOOTH)
        wall = ImageIcon("src/main/resources/wall.png")
            .image
            .getScaledInstance(dotSize, dotSize, Image.SCALE_SMOOTH)
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        g.drawImage(apple, appleX, appleY, this)
        g.drawImage(eater, eaterX, eaterY, this)

        for (x in 0..boardWidth step dotSize) {
            for (y in 0..boardHeight step dotSize) {
                if ((x == 0) || (y == 0) || (x == boardHeight - dotSize) || (y == boardWidth - dotSize)) {
                    g.drawImage(wall, x, y, this)
                }
            }
        }

        Toolkit.getDefaultToolkit().sync()
    }

    fun update(positions: ArrayList<Int>) {
        eaterX = positions[0] * dotSize
        eaterY = positions[1] * dotSize

        appleX = positions[2] * dotSize
        appleY = positions[3] * dotSize

        repaint()
    }
}