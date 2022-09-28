import ai.Network
import kotlin.math.abs
import kotlin.random.Random

class Tictactoe {
    private var board: HashMap<Int, Int> = HashMap()

    init {
        resetBoard()
    }

    /**
     * @param network neural network
     * @param isPlayerSecond if true, player (human or random) moves second after AI
     * @param isInputRandom if true, inputs are entered randomly
     *                      if false, human controls inputs
     */
    fun play(network: Network, isPlayerSecond: Boolean = true, isInputRandom: Boolean = false): Int {
        val players = arrayListOf(1, 2)
        var playerIndex = 0

        if (isPlayerSecond) playerIndex = 1

        resetBoard()

        while (determineWinner() == 3) {
            if (playerIndex == 0) {
                prettyPrint()
                println("Enter your choice")
                if (isInputRandom) {
                    while (fill((Random.nextInt() % 9) + 1, players[playerIndex]).not()) {
                    }
                } else {
                    while (fill(readLine()!!.toInt(), players[playerIndex]).not()) {
                    }
                }
            } else {
                network.setInputs(boardState())
                network.evaluate()

                val result = hashMapOf<Double, Int>()
                var it = 1
                for (output in network.output()) {
                    result[output] = it
                    it += 1
                }
                val sortedResult = result.toSortedMap(compareByDescending { it })
                println(sortedResult)

                for (index in sortedResult.values) {
                    if (fill(index, 2).not()) {
                        continue
                    } else {
                        break
                    }
                }
            }

            playerIndex = abs(playerIndex - 1)
        }
        return determineWinner()
    }

    fun playAI(network1: Network, network2: Network): Int {
        val players = arrayListOf(1, 2)
        var playerIndex = 0
        var isGameEnded: Int

        resetBoard()

        do {
            val network = if (playerIndex == 0) network1 else network2

            val adjustedBoardState = ArrayList<Int>()
            for (element in boardState()) {
                if (element == 2) {
                    adjustedBoardState.add(-1)
                } else {
                    adjustedBoardState.add(element)
                }
            }

            network.setInputs(adjustedBoardState)
            network.evaluate()

            val result = hashMapOf<Double, Int>()
            var iterator = 1
            for (output in network.output()) {
                result[output] = iterator
                iterator += 1
            }
            val sortedResult = result.toSortedMap(compareByDescending { it })

            for (index in sortedResult.values) {
                if (fill(index, players[playerIndex]).not()) {
                    continue
                } else {
                    break
                }
            }

            playerIndex = abs(playerIndex - 1)
            isGameEnded = determineWinner(false)
        } while (isGameEnded == 3)
        return isGameEnded
    }

    private fun boardState(): ArrayList<Int> {
        return ArrayList(board.values)
    }

    private fun resetBoard() {
        for (index in 1..9) {
            board[index] = 0
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
            false
        }
    }

    private fun determineWinner(printMessages: Boolean = true): Int {
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
                if (printMessages) {
                    prettyPrint()
                    println("Player 1 won")
                }
                return 1
            } else if (combinationSum == 6) {
                if (printMessages) {
                    prettyPrint()
                    println("Player 2 won")
                }
                return 2
            }
        }

        if (board.values.contains(0).not()) {
            if (printMessages) {
                prettyPrint()
                println("Draw")
            }
            return 0
        }
        return 3
    }
}