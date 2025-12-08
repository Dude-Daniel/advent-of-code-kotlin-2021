package de.devdudes.aoc.helpers

data class Point3D(val x: Int, val y: Int, val z: Int) {
    companion object {
        val MIN = Point3D(x = Int.MIN_VALUE, y = Int.MIN_VALUE, z = Int.MIN_VALUE)
        val MAX = Point3D(x = Int.MAX_VALUE, y = Int.MAX_VALUE, z = Int.MAX_VALUE)
    }
}

fun Point3D.distanceSquaredTo(other: Point3D): Long {
    val deltaX = other.x - this.x.toLong()
    val deltaY = other.y - this.y.toLong()
    val deltaZ = other.z - this.z.toLong()
    return (deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ)
}
