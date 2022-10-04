import ai.Network
import java.io.File
import kotlin.math.abs

class Tictactoe {
    // index, value
    private var board: HashMap<Int, Int> = HashMap()

    // intelligent random
    private val allPossibleOptionsTree = Tree()

    init {
        resetBoard()
    }

    enum class PlayerInputs {
        HUMAN,
        RANDOM,
        INTELLIGENT_RANDOM
    }

    /**
     * @param network neural network
     * @param playerInput TODO
     * @param file file to print logs into
     */
    fun play(network: Network, playerInput: PlayerInputs, file: File? = null): Int {
        var iterativePlayerIndex = if (Constants.AI_PLAYER_INDEX == 1) 1 else 0
        var isGameEnded: Int

        resetBoard()

        do {
            printBoardStateToFile(file)

            if (iterativePlayerIndex == 0) {
                when (playerInput) {
                    PlayerInputs.RANDOM ->
                        while (fill(availableBoardSquares().random(), Constants.OPPONENT_PLAYER_INDEX).not()) {
                        }

                    PlayerInputs.HUMAN ->
                        while (fill(readLine()!!.toInt(), Constants.OPPONENT_PLAYER_INDEX).not()) {
                        }

                    PlayerInputs.INTELLIGENT_RANDOM ->
                        println("todo")
                }
            } else {
                network.setInputs(adjustedBoardState())
                network.evaluate()

                // map output neurons to tic-tac-toe fields
                val result = hashMapOf<Double, Int>()
                var it = 1
                for (output in network.output()) {
                    result[output] = it
                    it += 1
                }
                val sortedResult = result.toSortedMap(compareByDescending { it })

                sortedResult.forEach { file?.appendText("$it \n") }

                for (index in sortedResult.values) {
                    if (fill(index, Constants.AI_PLAYER_INDEX).not()) {
                        continue
                    } else {
                        if (playerInput == PlayerInputs.INTELLIGENT_RANDOM && !allPossibleOptionsTree.isRootInitialized()) {
                            allPossibleOptionsTree.createRoot(index, Constants.AI_PLAYER_INDEX)
                        }
                        break
                    }
                }
            }

            iterativePlayerIndex = abs(iterativePlayerIndex - 1)
            isGameEnded = determineWinner(file)
        } while (isGameEnded == 3)
        printBoardStateToFile(file)

        return isGameEnded
    }

    /**
     * Used to train networks between themselves
     */
    fun playAI(network1: Network, network2: Network): Int {
        val players = arrayListOf(1, 2)
        var playerIndex = 0
        var isGameEnded: Int

        resetBoard()

        do {
            val network = if (playerIndex == 0) network1 else network2

            network.setInputs(adjustedBoardState())
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
            isGameEnded = determineWinner()
        } while (isGameEnded == 3)
        return isGameEnded
    }

    /**
     * Board state with 0, 1, 2 (easier to read in console)
     */
    private fun boardState(): ArrayList<Int> {
        return ArrayList(board.values)
    }

    /**
     * Array with indexes of empty board spaces (used for random)
     */
    private fun availableBoardSquares(): ArrayList<Int> {
        val emptySpots = ArrayList<Int>()
        for ((index, value) in board) {
            if (value == 0) {
                emptySpots.add(index)
            }
        }
        return emptySpots
    }

    /**
     * Board state with 0, 1, -1 (for NN learning)
     */
    private fun adjustedBoardState(): ArrayList<Int> {
        val adjustedBoardState = ArrayList<Int>()
        for (element in boardState()) {
            if (element == 2) {
                adjustedBoardState.add(-1)
            } else {
                adjustedBoardState.add(element)
            }
        }
        return adjustedBoardState
    }

    private fun resetBoard() {
        for (index in 1..9) {
            board[index] = 0
        }
    }

    private fun printBoardStateToFile(toFile: File? = null) {
        for ((index, value) in board.values.withIndex()) {
            toFile?.appendText("$value ")
            if ((index + 1) % 3 == 0) {
                toFile?.appendText("\n")
            }
        }
        toFile?.appendText("\n")
    }

    /**
     * @return true if value was successfully entered
     *         false if not
     */
    private fun fill(index: Int, value: Int): Boolean {
        return if (board[index] == 0) {
            board[index] = value
            true
        } else {
            false
        }
    }

    private fun determineWinner(file: File? = null): Int {
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
                file?.appendText("Player 1 won \n")
                return 1
            } else if (combinationSum == 6) {
                file?.appendText("Player 2 won \n")
                return 2
            }
        }

        if (board.values.contains(0).not()) {
            file?.appendText("Draw \n")
            return 0
        }
        return 3
    }
}