package snake

import kotlin.random.Random

class ConsoleEater {
    /***
     * 0 - empty space
     * 1 - wall
     * 2 - snake
     * 3 - apple
     */
    private val size = 20
    private val board = Array(size) { Array(size) { 0 } }

    private var snakeLocationX: Int = Random.nextInt(1, size - 1)
    private var snakeLocationY: Int = Random.nextInt(1, size - 1)
    private var appleLocationX: Int = Random.nextInt(1, size - 1)
    private var appleLocationY: Int = Random.nextInt(1, size - 1)

    private var score = 0
    private var maxSteps = 1000


    init {
        // draw walls
        for (index in 0 until size) {
            board[0][index] = 1
            board[size - 1][index] = 1
            board[index][0] = 1
            board[index][size - 1] = 1
        }

        while ((snakeLocationX == appleLocationX) && (snakeLocationY == appleLocationY)) {
            appleLocationX = Random.nextInt(1, size - 1)
        }
        board[snakeLocationX][snakeLocationY] = 2
        board[appleLocationX][appleLocationY] = 3
    }

    fun play() {
        while (maxSteps > 0) {
            printBoard()
            move(Direction.values().random())
            maxSteps -= 1

            if (isSnakeDead()) {
                break
            }

            if (isAppleEaten()) {
                println("Ate!")
                score += 1
                setRandomApplePosition()
            }
        }
        println("Score $score")
    }

    private fun printBoard() {
        val translator = HashMap<Int, String>()
        translator[0] = " "
        translator[1] = "*"
        translator[2] = "X"
        translator[3] = "O"

        for (row in 0 until size) {
            for (column in 0 until size) {
                print(translator[board[row][column]])
            }
            println()
        }
    }

    enum class Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private fun move(direction: Direction) {
        board[snakeLocationX][snakeLocationY] = 0
        if (direction == Direction.LEFT) {
            snakeLocationX -= 1
        } else if (direction == Direction.RIGHT) {
            snakeLocationX += 1
        } else if (direction == Direction.UP) {
            snakeLocationY -= 1
        } else if (direction == Direction.DOWN) {
            snakeLocationY += 1
        }
       updateBoard()
    }

    private fun isAppleEaten(): Boolean {
        return ((snakeLocationX == appleLocationX) && (snakeLocationY == appleLocationY))
    }

    private fun setRandomApplePosition() {
        board[appleLocationX][appleLocationY] = 0
        while ((snakeLocationX == appleLocationX) && (snakeLocationY == appleLocationY)) {
            appleLocationX = Random.nextInt(1, size - 1)
            appleLocationY = Random.nextInt(1, size - 1)
        }
        updateBoard()
    }

    private fun isSnakeDead(): Boolean {
        return (snakeLocationX < 1)
                || (snakeLocationY < 1)
                || (snakeLocationX >= size - 1)
                || (snakeLocationY >= size - 1)
    }

    private fun updateBoard(){
        board[snakeLocationX][snakeLocationY] = 2
        board[appleLocationX][appleLocationY] = 3
    }
}