import pt.isel.canvas.Canvas
import pt.isel.canvas.GREEN
import pt.isel.canvas.RED
import pt.isel.canvas.erase

/**
 * Represents the game action.
 */
enum class Action { WALK_LEFT, WALK_RIGHT, UP_STAIRS, DOWN_STAIRS, JUMP }

/**
 * Represents all game information.
 * @property man information about man
 * @property floor positions of floor cells
 * @property stairs positions of stairs cells
 */
//Mutability point, every time there is an action, it creates a new "game"
data class Game(
    val man: Man,
    val floor: List<Cell>,
    val stairs: List<Cell>,
    val eggs: List<Cell>,
    val food: List<Cell>,
    val score: Int,
    val scoreAdded: Boolean,
    val time: Int
)

/**
 * Loads a game from a file.
 * @param fileName the name of the file with the game information.
 * @return the game loaded.
 */
fun loadGame(fileName: String): Game {
    val cells: List<CellContent> = loadLevel(fileName)
    return Game(
        man = createMan(cells.first { it.type == CellType.MAN }.cell),
        floor = cells.ofType(CellType.FLOOR),
        stairs = cells.ofType(CellType.STAIR),
        eggs = cells.ofType(CellType.EGG),
        food = cells.ofType(CellType.FOOD),
        score = 0,
        time = 2666,
        scoreAdded = false
    )
}

/**
 * Performs an action to the game.
 * If the action is null, returns current game.
 * @param action the action to perform.
 * @receiver the current game.
 * @return the game after the action performed.
 */

//When the key is clicked, the coordinates of the man are updated using the newStateMove and newStateJump functions.
fun Game.doAction(action: Action?): Game {
    return when (action) {
        //The function isStopped() is used to prevent man getting a new speed while moving, preventing it from misalignment of the grid (Used in all movements).
        Action.WALK_LEFT -> if (man.pos.x == 0) this
        else if (man.speed.isZero() && !man.copy(Point(man.pos.x - CELL_WIDTH, man.pos.y)).detectIfisInsideFloor(floor) && man.detectIfisFloororstrair(floor, stairs))
            newStateMove(Direction.LEFT, man) else this

        Action.WALK_RIGHT -> if (man.pos.x == MAX_X) this
        else if (man.speed.isZero() && !man.copy(Point(man.pos.x + CELL_WIDTH, man.pos.y)).detectIfisInsideFloor(floor) && man.detectIfisFloororstrair(floor, stairs))
            newStateMove(Direction.RIGHT, man) else this

        Action.UP_STAIRS -> if (man.detectIfisStairs(stairs) && man.speed.isZero() && man.copy(Point(man.pos.x, man.pos.y - CELL_HEIGHT * 2)).detectIfisStairs(stairs))
            newStateMove(Direction.UP, man) else this

        Action.DOWN_STAIRS -> if (man.detectIfisStairs(stairs) && !man.detectIfisFloor(floor) && man.speed.isZero())
            newStateMove(Direction.DOWN, man) else this

        Action.JUMP -> {
            if (man.speed.isZero() && !man.stateJump && man.detectIfisFloororstrair(floor, stairs))
                return newStateJump(man.faced, man)
            this
        }

        else -> this
    }

}

//Function for when the game ends
fun Game.isOver(): Boolean = time == 0 || this.eggs.isEmpty() && this.food.isEmpty()

//Create a new state of motion
fun Game.newStateMove(direction: Direction, man: Man): Game {
    val updatedMan = when (direction) {
        Direction.RIGHT -> man.copy(speed = Speed(MOVE_SPEED, 0), animationCicle = 6)
        Direction.LEFT -> man.copy(speed = Speed(-MOVE_SPEED, 0), animationCicle = 6)
        Direction.UP -> man.copy(speed = Speed(0, -CLIMBING_SPEED), animationCicle = 4)
        Direction.DOWN -> man.copy(speed = Speed(0, CLIMBING_SPEED), animationCicle = 4)
    }
    return this.copy(man = updatedMan.copy(faced = direction))
}

//Create a new state of jump
fun Game.newStateJump(direction: Direction, man: Man): Game {
    val updatedMan = when (direction) {
        Direction.RIGHT -> man.copy(stateJump = true, speed = Speed(MOVE_SPEED, CELL_HEIGHT / 2), jumpCycle = 16)
        Direction.LEFT -> man.copy(stateJump = true, speed = Speed(-MOVE_SPEED, CELL_HEIGHT / 2), jumpCycle = 16)
        else -> man
    }
    return this.copy(man = updatedMan.copy(faced = direction))
}

/**
 * Computes the next game state.
 * If the man is stopped, returns current game.
 * @receiver the current game.
 * @return the game after the next frame.
 * Every 30ms the game is updated according to the conditions
 */

fun Game.stepFrame(): Game {
    if (!isOver()) {
        return when {
            //Man jumping
            (man.stateJump && (man.jumpCycle == 16 || !man.detectIfisFloor(floor))) -> {
                println("Entrei aqui primeiro")
                this.copy(man.copy(jumpCycle = man.jumpCycle - 1).jump(), time = time - 1, scoreAdded = false) }

            //Man standing on stairs
            (man.animationCicle > 0 && !man.stateJump && man.detectIfisStairs(stairs)) -> {
                this.copy(man.copy(animationCicle = man.animationCicle - 1).moveUpDown(), time = time - 1, scoreAdded = false) }

            //Man falling
            (!man.detectIfisStairs(stairs) && !man.detectIfisFloor(floor)) -> {
                this.copy(man.gravity(), time = time - 1, scoreAdded = false) }

            //Man jumping without standing in stairs
            (man.stateJump) -> {
                this.copy(man.copy(pos = Point(man.pos.x, man.pos.y).toCell().toPoint(), stateJump = false, speed = Speed(0, 0), animationCicle = 2))
            }
            else -> {
                return when{
                    man.food(food) ->
                        this.copy(man.copy(animationCicle = man.animationCicle - 1),food =man.removeFood(food), score = score + 50, time = time - 1, scoreAdded = false)

                    man.eggs(eggs)->
                        this.copy(man.copy(animationCicle = man.animationCicle - 1).move(), eggs = man.removeEggs(eggs), score = score + 100, time = time - 1, scoreAdded = false)

                    else ->{
                        this.copy(man.copy(animationCicle = man.animationCicle - 1).move(), time = time - 1, scoreAdded = false)
                        }

                }
            }
        }
    }
    // When all the food and eggs are eaten the game ends and the remaing time is added to score
    return if(!scoreAdded){
        this.copy(score = score+time, scoreAdded = true)
    }
    else this

}