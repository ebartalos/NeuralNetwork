package eater.gui

import java.awt.*
import javax.swing.ImageIcon
import javax.swing.JPanel


class Board(private var boardState: Array<Array<Int>>) : JPanel() {
    private val dotSize = 25
    private val sideLength = boardState.size

    private val boardWidth = sideLength * dotSize
    private val boardHeight = sideLength * dotSize

    private var appleIcon: Image? = null
    private var eaterIcon: Image? = null
    private var wallIcon: Image? = null

    init {
        background = Color.black
        isFocusable = true

        preferredSize = Dimension(boardWidth, boardHeight)

        loadImages()
    }

    private fun loadImages() {
        eaterIcon = ImageIcon("src/main/resources/head.png")
            .image
            .getScaledInstance(dotSize, dotSize, Image.SCALE_SMOOTH)
        appleIcon = ImageIcon("src/main/resources/apple.png")
            .image
            .getScaledInstance(dotSize, dotSize, Image.SCALE_SMOOTH)
        wallIcon = ImageIcon("src/main/resources/wall.png")
            .image
            .getScaledInstance(dotSize, dotSize, Image.SCALE_SMOOTH)
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val iconsMap = HashMap<Int, Image?>()
        iconsMap[0] = null
        iconsMap[1] = wallIcon
        iconsMap[2] = eaterIcon
        iconsMap[3] = appleIcon

        for (x in 0 until sideLength) {
            for (y in 0 until sideLength) {
                iconsMap[boardState[x][y]]?.let {
                    g.drawImage(it, x * dotSize, y * dotSize, this)
                }
            }
        }

        Toolkit.getDefaultToolkit().sync()
    }

    fun update(boardState: Array<Array<Int>>) {
        this.boardState = boardState
        repaint()
    }
}