// Speed of man in pixels per frame, in horizontal and vertical directions
const val MOVE_SPEED = CELL_WIDTH / 6
const val CLIMBING_SPEED = CELL_HEIGHT / 4
const val AC_JUMP = CELL_HEIGHT / SPRITE_HEIGHT

// Limits of the board in pixels
// Point(MAX_X, MAX_Y) == Cell(GRID_HEIGHT-1, GRID_WIDTH-1).toPoint()
const val MAX_X = (GRID_WIDTH - 1) * CELL_WIDTH
const val MAX_Y = (GRID_HEIGHT - 1) * CELL_HEIGHT

/**
 * Represents the Man in the game.
 * @property pos is the position in the board.
 * @property faced the direction the man is facing
 */
data class Man(val pos: Point, val faced: Direction, val stateJump: Boolean, val speed: Speed, val jumpCycle: Int,val animationCicle:Int)

/**
 * Creates the Man in the cell
 */
fun createMan(cell: Cell) = Man(
    pos = cell.toPoint(),
    faced = Direction.LEFT,
    false,
    speed = Speed(0, 0),
    jumpCycle = 0,
    animationCicle = 0
)


//Update the X-coordinate by adding an acceleration on the X-axis
fun Man.move(): Man {
    return Man(pos.plus(speed).limitToArea(MAX_X, MAX_Y), faced, this.stateJump, speed.stopIfInCell(pos.plus(speed)), 0,this.animationCicle)
}


//Update the Y-coordinate by adding an acceleration on the Y-axis
fun Man.moveUpDown(): Man {
    return Man(pos.plus(speed).limitToArea(MAX_X, MAX_Y), faced, this.stateJump, speed.stopIfInCell(pos.plus(speed)), 0,this.animationCicle)
}

//Add an acceleration on the Y-axis that causes the man to descend
fun Man.gravity(): Man {
    return Man(pos.plus(speed = Speed(this.speed.dx, CLIMBING_SPEED)).limitToArea(MAX_X, MAX_Y), faced, this.stateJump, speed.stopIfInCell(pos.plus(speed)), 0, 0)
}

//Add speed on the Y-axis and X-axis making the man jump
fun Man.jump(): Man {
    val newDyTemp = this.speed.dy - AC_JUMP
    val newYTemp = this.pos.y - this.speed.dy
    val newXTemp = when (faced) {
        Direction.RIGHT,Direction.LEFT -> this.pos.x + speed.dx
        else -> this.pos.x
    }
    return Man(Point(newXTemp, newYTemp).limitToArea(MAX_X, MAX_Y), this.faced, true, speed = Speed(this.speed.dx, newDyTemp), this.jumpCycle, this.animationCicle)
}

//Detects if the man is on the stairs
fun Man.detectIfisStairs(stairs: List<Cell>): Boolean = stairs.any { pos.toCell().toPoint() == it.toPoint() }

//Detects if the man is on the floor
fun Man.detectIfisFloor(floor: List<Cell>): Boolean = floor.any { pos.toCell().col == it.col && pos.toCell().row == it.row - 1 }

//Detects if the man is on the floor OR on the stairs
fun Man.detectIfisFloororstrair(floor: List<Cell>, stairs: List<Cell>): Boolean = floor.any { pos.toCell().col == it.col && pos.toCell().row == it.row - 1 } || stairs.any { pos.toCell().col == it.col && pos.toCell().row == it.row }

//Ensures man is on a cell type Floor.
fun Man.detectIfisInsideFloor(floor: List<Cell>): Boolean = floor.any { pos.toCell().col == it.col && pos.toCell().row == it.row }

//Add the food to the grid
fun Man.food(food: List<Cell>): Boolean {
    return food.any { pos.toCell().col == it.col && pos.toCell().row == it.row }
}

//This function when called remove food from the grid
fun Man.removeFood(food: List<Cell>): List<Cell> {
    val manPos = this.pos.toCell()
    val foodList = food
    return foodList - manPos
}

//Add the eggs to the grid
fun Man.eggs(eggs: List<Cell>): Boolean {
    return eggs.any { pos.toCell().col == it.col && pos.toCell().row == it.row }
}

//When called remove eggs from the grid
fun Man.removeEggs(eggs: List<Cell>): List<Cell> {
    val manPos = this.pos.toCell()
    val eggsList = eggs
    return eggsList - manPos
}
