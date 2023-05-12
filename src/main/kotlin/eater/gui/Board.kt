package eater.gui

import eater.Eater
import java.awt.*
import javax.swing.ImageIcon
import javax.swing.JPanel


class Board(val sideLength: Int) : JPanel() {
    private val dotSize = 25

    private val boardWidth = sideLength * dotSize
    private val boardHeight = sideLength * dotSize

    private var eatersPositions = arrayListOf<Pair<Int, Int>>()

    private var appleX: Int = 0
    private var appleY: Int = 0

    private var appleIcon: Image? = null
    private var eaterIcon: Image? = null

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
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        for (eater in eatersPositions) {
            g.drawImage(eaterIcon, eater.first, eater.second, this)
        }

        g.drawImage(appleIcon, appleX, appleY, this)

        Toolkit.getDefaultToolkit().sync()
    }

    fun update(eaters: ArrayList<Eater>, applesPositions: ArrayList<Int>) {
        eatersPositions = arrayListOf()

        for (eater in eaters) {
            eatersPositions.add(Pair(eater.positionX * dotSize, eater.positionY * dotSize))
        }

        appleX = applesPositions[0] * dotSize
        appleY = applesPositions[1] * dotSize

        repaint()
    }
}