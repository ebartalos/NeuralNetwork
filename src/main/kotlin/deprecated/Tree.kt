package deprecated

@Deprecated("Not feasible", ReplaceWith("Generator"))
class Tree {

    private lateinit var root: TreeNode
    lateinit var currentNode: TreeNode
    var goRandom: Boolean = false

    fun isRootInitialized(): Boolean {
        return ::root.isInitialized
    }

    fun createRoot(position: Int, value: Int) {
        root = TreeNode(position, value, null)

        val possibleChildren = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        possibleChildren.remove(position)

        val childrenValue = if (value == 1) 2 else 1

        fillTreeWithAllValues(root, possibleChildren, childrenValue)

        currentNode = root
    }

    private fun fillTreeWithAllValues(node: TreeNode, possiblePositions: MutableList<Int>, value: Int) {
        if (possiblePositions.isEmpty()) return

        for (possiblePosition in possiblePositions) {

            val child = TreeNode(possiblePosition, value, node)
            node.children.add(child)

            val anotherPlayer = if (value == 1) 2 else 1

            fillTreeWithAllValues(
                child, possiblePositions.toMutableList().also { it.remove(possiblePosition) }, anotherPlayer
            )
        }
    }

    // enter 1
    // exit 2
    fun nextRandomPosition(emptySquares: ArrayList<Int>): Int {
        if (currentNode.value == 2) {
            return 0
        }
        if (goRandom) {
            return emptySquares.random()
        }
        if (currentNode.children.isEmpty()) {
            goRandom = true
            removeParentNode()
            return emptySquares.random()
        }
        currentNode = currentNode.children.random()
        if (currentNode.value == 2) return currentNode.position
        else throw Exception("Fuck you")
    }

    // enter 2
    // exit 1
    fun movePointer(position: Int) {
        if (currentNode.value == 1) {
            return
        }

        if (goRandom) {
            return
        }

        currentNode.children.removeAll { it.position != position }
        currentNode = currentNode.children[0]
        return
    }

    fun isTreeDrained(): Boolean {
        return root.children.isEmpty()
    }

    fun removeCurrentNode(value: Int): Boolean {
        if (value == currentNode.value) {
            val murderedChild = currentNode
            currentNode = currentNode.parent!!
            currentNode.children.remove(murderedChild)
            return true
        } else {
            return false
        }
    }

    fun removeParentNode() {
        if (isTreeDrained()) {
            return
        }

        if (currentNode.parent == null) {
            return
        }
        val murderedChild = currentNode.parent!!

        if (murderedChild.value != 2) {
            return
        }

        currentNode = murderedChild.parent!!
        currentNode.children.remove(murderedChild)
    }

    fun resetCurrentNode() {
        currentNode = root
        goRandom = false
    }
}