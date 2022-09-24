import kotlin.math.abs

class Tictactoe {
    private var board: HashMap<Int, Int> = HashMap()

    init {
        for (index in 1..9) {
            board[index] = 0
        }
    }

    fun boardState(): MutableCollection<Int> {
        return board.values
    }

    fun play() {
        val players = arrayListOf(1, 2)
        var playerIndex = 0
        while (determineWinner() == 0) {
            prettyPrint()
            while (fill(readLine()!!.toInt(), players[playerIndex]).not()) {
            }
            playerIndex = abs(playerIndex - 1)
        }
    }

    private fun prettyPrint() {
        for ((index, value) in board.values.withIndex()) {
            print("$value ")
            if ((index + 1) % 3 == 0) print("\n")
        }
    }

    private fun fill(index: Int, value: Int): Boolean {
        return if (board[index] == 0) {
            board[index] = value
            true
        } else {
            println("$index is already filled")
            false
        }
    }

    private fun determineWinner(): Int {
        val winningCombos = arrayListOf(
            arrayListOf(1, 2, 3),
            arrayListOf(4, 5, 6),
            arrayListOf(7, 8, 9),
            arrayListOf(1, 4, 7),
            arrayListOf(2, 5, 8),
            arrayListOf(3, 6, 9),
            arrayListOf(1, 5, 9),
            arrayListOf(3, 5, 7)
        )

        combinations@ for (combination in winningCombos) {
            var combinationSum = 0
            for (index in combination) {
                if (board[index]!! == 0) continue@combinations
                combinationSum += board[index]!!
            }

            if (combinationSum == 3) {
                println("Player 1 won")
                return 1
            } else if (combinationSum == 6) {
                println("Player 2 won")
                return 2
            }
        }
        return 0
    }
}