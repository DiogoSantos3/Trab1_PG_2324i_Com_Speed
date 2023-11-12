
import kotlin.math.floor

// Speed of man in pixels per frame, in horizontal and vertical directions
const val MOVE_SPEED = CELL_WIDTH / 6
const val CLIMBING_SPEED = CELL_HEIGHT / 4
const val AC_JUMP = CELL_HEIGHT /SPRITE_HEIGHT

// Limits of the board in pixels
// Point(MAX_X, MAX_Y) == Cell(GRID_HEIGHT-1, GRID_WIDTH-1).toPoint()
const val MAX_X = (GRID_WIDTH - 1) * CELL_WIDTH
const val MAX_Y = (GRID_HEIGHT - 1) * CELL_HEIGHT

/**
 * Represents the Man in the game.
 * @property pos is the position in the board.
 * @property faced the direction the man is facing
 */
data class Man(val pos: Point, val faced: Direction, val stateJump:Boolean, val speed: Speed, val jumpCycle:Int)
/**
 * Creates the Man in the cell
 */
fun createMan(cell: Cell) = Man(
    pos = cell.toPoint(),
    faced = Direction.LEFT,
    false,
    speed = Speed(0,0),
    jumpCycle = 0)


fun Man.move(): Man{
    return Man(pos.plus(speed).limitToArea(MAX_X,MAX_Y), faced,this.stateJump, speed.stopIfInCell(pos.plus(speed)),0)
}

fun Man.moveUpDown(): Man {
    return Man(pos.plus(speed).limitToArea(MAX_X,MAX_Y), faced,this.stateJump, speed.stopIfInCell(pos.plus(speed)),0)
}

fun Man.gravity(): Man {
    return Man(pos.plus(speed= Speed(this.speed.dx,CLIMBING_SPEED)).limitToArea(MAX_X,MAX_Y), faced,this.stateJump,speed.stopIfInCell(pos.plus(speed)),0)
}

fun Man.jump(): Man {
    val newDyTemp = this.speed.dy - AC_JUMP
    val newYTemp = this.pos.y - this.speed.dy
    val newXTemp = when (faced) {
        Direction.RIGHT -> this.pos.x + speed.dx
        Direction.LEFT -> this.pos.x + speed.dx
        else -> this.pos.x
    }
         return Man(Point(newXTemp, newYTemp).limitToArea(MAX_X, MAX_Y), this.faced, true,speed=Speed(this.speed.dx,newDyTemp),this.jumpCycle)
    }

fun Man.DetectIfisStairs(stairs:List<Cell>):Boolean{
    return stairs.any { pos.toCell().col == it.col && pos.toCell().row == it.row}
}

//This function DetectIfisFloor ensures man is above a cell type Floor.
fun Man.DetectIfisFloor(floor:List<Cell>):Boolean {
    return when {
        floor.any { pos.toCell().col == it.col && pos.toCell().row == it.row - 1 } -> true
        else -> false
    }
}
//This function DetectIfisFloor ensures man is on a cell type Floor.
fun Man.DetectIfisInsideFloor(floor:List<Cell>):Boolean {
    return when {
        floor.any { pos.toCell().col == it.col && pos.toCell().row == it.row } -> true
        else -> false
    }
}


fun Man.isonFood(food:List<Cell>):Boolean{
    return food.any { pos.toCell().col == it.col && pos.toCell().row == it.row}
}

fun Man.removefood(food:List<Cell>): List<Cell> {
    val manPos = this.pos.toCell()
    val foodList = food
    return foodList - manPos
}

fun Man.isonEggs(eggs:List<Cell>):Boolean{
    return eggs.any { pos.toCell().col == it.col && pos.toCell().row == it.row}
}

fun Man.removeEggs(eggs:List<Cell>): List<Cell> {
    val manPos = this.pos.toCell()
    val eggsList = eggs
    return eggsList - manPos
}