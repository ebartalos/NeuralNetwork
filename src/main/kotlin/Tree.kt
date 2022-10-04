class Tree {

    private lateinit var root: TreeNode
    lateinit var currentNode: TreeNode
    private var goRandom: Boolean = false

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

    fun nextRandomPosition(emptySquares: ArrayList<Int>): Int {
        if (goRandom) {
            return emptySquares.random()
        }
        if (currentNode.children.isEmpty()) {
            removeCurrentNode()
            goRandom = true
            return emptySquares.random()
        }
        currentNode = currentNode.children.random()
        return currentNode.position
    }

    fun movePointer(position: Int) {
        if (currentNode.children.isEmpty()) return
        for (child in currentNode.children) {
            if (child.position == position) currentNode = child
        }
    }

    fun removeCurrentNode(): Boolean {
        if (currentNode.parent == null) {
            // we are at the root
            return false
        }
        val murderedChild = currentNode
        currentNode = currentNode.parent!!
        currentNode.children.remove(murderedChild)
        return true
    }

    fun resetCurrentNode() {
        currentNode = root
        goRandom = false
    }
}