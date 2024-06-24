package eater

import ai.Network
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

object Tests {

    @OptIn(ExperimentalTime::class)
    fun pruningTest(playgroundSize: Int) {
        val network = Network()
        network.loadTrainedNetworkFromFile()

        println("Number of connections before pruning: ${network.getConnections()}")

        val runs = 10

        val timeTakenBeforePruning = measureTime {
            for (i in 1..runs) {
                Game((Eater(network)), playgroundSize).play(EaterConstants.MAX_FITNESS, useGUI = false)
            }
        }
        println("Time before pruning: ${timeTakenBeforePruning.inWholeSeconds}s")

        network.prune()

        println("Number of connections after pruning: ${network.getConnections()}")
        val timeTakenAfterPruning = measureTime {
            for (i in 1..runs) {
                Game((Eater(network)), playgroundSize).play(EaterConstants.MAX_FITNESS, useGUI = false)
            }
        }
        println("Time after pruning: ${timeTakenAfterPruning.inWholeSeconds}s")

        println("Time saved: ${(timeTakenBeforePruning.minus(timeTakenAfterPruning)).inWholeSeconds}s for $runs runs")
    }
}