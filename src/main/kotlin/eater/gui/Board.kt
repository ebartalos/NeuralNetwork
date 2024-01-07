package eater.gui

import Constants.GUI_DOT_SIZE
import java.awt.*
import javax.swing.ImageIcon
import javax.swing.JPanel


class Board(private var boardState: Array<Array<Int>>) : JPanel() {
    private val sideLength = boardState.size

    private val boardWidth = sideLength * GUI_DOT_SIZE
    private val boardHeight = sideLength * GUI_DOT_SIZE

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
            .getScaledInstance(GUI_DOT_SIZE, GUI_DOT_SIZE, Image.SCALE_SMOOTH)
        appleIcon = ImageIcon("src/main/resources/apple.png")
            .image
            .getScaledInstance(GUI_DOT_SIZE, GUI_DOT_SIZE, Image.SCALE_SMOOTH)
        wallIcon = ImageIcon("src/main/resources/wall.png")
            .image
            .getScaledInstance(GUI_DOT_SIZE, GUI_DOT_SIZE, Image.SCALE_SMOOTH)
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
                    g.drawImage(it, x * GUI_DOT_SIZE, y * GUI_DOT_SIZE, this)
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