Simple head-only no-growing snake game to showcase neural network and genetic algorithms.

## Prerequisites

You need to have Java 17+ with Kotlin installed.
You also need to have libraries

`org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2`

`org.jetbrains.kotlin:kotlin-reflect:1.8.20`

## Features

### Branch `master`

- Neural network implementation from scratch that adheres to the OOP principles
- Several neurons types - Rectified Linear, Sigmoid and Hypertangens
- Genetic algorithms - breeding and mutation
- Training function automatically saves best network to the file and will resume back when program is run again
- Simple GUI eater game with already trained neural network

#### How to run master
Run main in MainEater. You can select activity in MainEater -
Test (showcases already trained network solving eater game) or Train (training network).

### Branch `xor`

- Backpropagation demonstrated in XOR
- Run main in MainXor.

## Bug reporting

If you find a bug, open issue here at Github.