package tictactoe

import ai.Network
import java.io.File
import kotlin.math.abs

/**
 * Classic 3x3 tic-tac-toe game.
 */
class Tictactoe {
    // index, value
    private var board: HashMap<Int, Int> = HashMap()

    init {
        resetBoard()
    }

    enum class PlayerInputs {
        HUMAN,
        RANDOM,
        GENERATOR
    }

    /**
     * Plays one round of tic-tac-toe.
     *
     * @param network neural network
     * @param aiPlayerIndex is AI first(1) or second(2) player?
     * @param playerInput who is playing network - Human, Random or tictactoe.Generator
     * @param file file to print logs into
     * @param generator generator for tictactoe.Generator input
     */
    fun play(
        network: Network,
        aiPlayerIndex: Int,
        playerInput: PlayerInputs,
        file: File? = null,
        generator: Generator? = null
    ): Int {
        var iterativePlayerIndex = if (aiPlayerIndex == 1) 1 else 0
        var isGameEnded: Int

        var generatorLevel = 0
        var goRandom = false

        val opponentPlayerIndex = if (aiPlayerIndex == 1) 2 else 1

        resetBoard()

        do {
            printBoardStateToFile(file)

            if (iterativePlayerIndex == 0) {
                when (playerInput) {
                    PlayerInputs.RANDOM ->
                        while (fill(availableBoardSquares().random(), opponentPlayerIndex).not()) {
                        }

                    PlayerInputs.HUMAN -> {
                        printBoardToConsole()
                        while (fill(readLine()!!.toInt(), opponentPlayerIndex).not()) {
                        }
                    }

                    PlayerInputs.GENERATOR -> {
                        if (generatorLevel < 4 && goRandom.not() && fill(
                                generator!!.yield(generatorLevel),
                                opponentPlayerIndex
                            )
                        ) {
                            generatorLevel += 1
                        } else {
                            goRandom = true
                            while (fill(availableBoardSquares().random(), opponentPlayerIndex).not()) {
                            }
                        }
                    }
                }
            } else {
                network.setInputs(adjustedBoardState(aiPlayerIndex, opponentPlayerIndex))
                network.evaluate()

                // map output neurons to tic-tac-toe fields
                val result = hashMapOf<Int, Double>()
                var boardIndex = 1
                for (output in network.output()) {
                    result[boardIndex] = output
                    boardIndex += 1
                }
                val sortedResult = result.toList().sortedBy { (key, value) -> value }
                    .toMap()

                sortedResult.forEach { file?.appendText("$it \n") }

                for (index in sortedResult.keys.reversed()) {
                    if (fill(index, aiPlayerIndex).not()) {
                        continue
                    } else {
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
    private fun adjustedBoardState(aiPlayerIndex: Int, opponentPlayerIndex: Int): ArrayList<Double> {
        val adjustedBoardState = ArrayList<Double>()
        for (element in boardState()) {
            if (element == opponentPlayerIndex) {
                adjustedBoardState.add(-1.0)
            } else if (element == aiPlayerIndex) {
                adjustedBoardState.add(1.0)
            } else {
                adjustedBoardState.add(element.toDouble())
            }
        }
        return adjustedBoardState
    }

    /**
     * Resets board to initial state (all zeroes)
     */
    private fun resetBoard() {
        for (index in 1..9) {
            board[index] = 0
        }
    }

    /**
     * Saves current board state to the file in human-readable format
     *
     * @param file target file
     */
    private fun printBoardStateToFile(file: File? = null) {
        for ((index, value) in board.values.withIndex()) {
            file?.appendText("$value ")
            if ((index + 1) % 3 == 0) {
                file?.appendText("\n")
            }
        }
        file?.appendText("\n")
    }

    /**
     * Prints current board state to the console in human-readable format
     */
    private fun printBoardToConsole() {
        for ((index, value) in board.values.withIndex()) {
            print("$value ")
            if ((index + 1) % 3 == 0) {
                print("\n")
            }
        }
        println("\n")
    }

    /**
     * Fills board index with value
     *
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

    /**
     * Evaluates current state of the board and determines end of game.
     *
     * @param file target file for saving the result
     * @return 0 if game ended in draw
     *         1 if player 1 won
     *         2 if player 2 won
     *         3 if the game is not determined yet
     */
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