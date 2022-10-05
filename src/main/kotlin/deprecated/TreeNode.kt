package deprecated

@Deprecated("Not feasible")
class TreeNode(
    var position: Int,
    var value: Int,
    var parent: TreeNode?,
    var children: ArrayList<TreeNode> = ArrayList()
)