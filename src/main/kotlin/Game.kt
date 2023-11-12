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
//PONTO DE MUTABILIDADE  (SEMPRE QUE HÁ ALGUMA AÇÃO, CRIA UM NOVO "GAME")
data class Game(
    val man: Man,
    val floor: List<Cell>,
    val stairs: List<Cell>,
    val eggs: List<Cell>,
    val food: List<Cell>,
)

/**
 * Loads a game from a file.
 * @param fileName the name of the file with the game information.
 * @return the game loaded.
 */
fun loadGame(fileName: String) :Game {
    val cells: List<CellContent> = loadLevel(fileName)
    return Game(
        man = createMan( cells.first { it.type==CellType.MAN }.cell ),
        floor = cells.ofType(CellType.FLOOR),
        stairs = cells.ofType(CellType.STAIR),
        eggs = cells.ofType(CellType.EGG),
        food = cells.ofType(CellType.FOOD),
    )
}

/**
 * Performs an action to the game.
 * If the action is null, returns current game.
 * @param action the action to perform.
 * @receiver the current game.
 * @return the game after the action performed.
 */
fun Game.doAction(action: Action?): Game {
    return when (action) {
        //The function isStopped() is used to prevent man getting a new speed while moving, preventing it from misalignment of the grid (Used in all movements).
        Action.WALK_LEFT -> if (man.speed.isZero() && !man.copy(Point(man.pos.x+CELL_WIDTH,man.pos.y)).DetectIfisInsideFloor(floor)) newStateMove(Direction.LEFT, man) else this

        Action.WALK_RIGHT -> if(man.speed.isZero()&& !man.copy(Point(man.pos.x+CELL_WIDTH,man.pos.y)).DetectIfisInsideFloor(floor))  newStateMove(Direction.RIGHT, man) else this

        Action.UP_STAIRS ->if(man.DetectIfisStairs(stairs)) newStateMove(Direction.UP, man) else this

        Action.DOWN_STAIRS ->if(man.DetectIfisStairs(stairs) && !man.DetectIfisFloor(floor)) newStateMove(Direction.DOWN, man) else this

        Action.JUMP -> {
            if ( man.speed.isZero() && man.stateJump==false)
                return newStateJump(man.faced,man)
            this
        }
        else -> this
    }
}

fun Game.newStateMove(direction: Direction, man: Man): Game {

    val updatedMan = when (direction) {
        Direction.RIGHT-> man.copy(speed=Speed(MOVE_SPEED,0))
        Direction.LEFT -> man.copy(speed=Speed(-MOVE_SPEED,0))
        Direction.UP -> man.copy(speed=Speed(0,-CLIMBING_SPEED))
        Direction.DOWN -> man.copy(speed=Speed(0,CLIMBING_SPEED))
    }
    val game = this.copy(man = updatedMan.copy(faced = direction))
    println(game.man)
    return game
}

fun Game.newStateJump(direction: Direction, man: Man): Game {
    val updatedMan = when (direction) {
        Direction.RIGHT -> {
            man.copy( stateJump = true, speed = Speed( MOVE_SPEED, CELL_HEIGHT/2), jumpCycle = 16)
        }
        Direction.LEFT -> {
            man.copy(stateJump = true, speed = Speed( -MOVE_SPEED,CELL_HEIGHT/2 ), jumpCycle = 16)
        }
        else -> man
    }
    return this.copy(man = updatedMan.copy(faced = direction))
}
/**
 * Computes the next game state.
 * If the man is stopped, returns current game.
 * @receiver the current game.
 * @return the game after the next frame.
 */

//TESTE COMMIT

//test COMMIT 2

fun Game.stepFrame(): Game {
    println(man)
    when{
        (man.jumpCycle > 0 && man.isonFood(food)) ->
            return Game(man.copy(jumpCycle = man.jumpCycle - 1).jump(), floor, stairs, eggs, man.removefood(food))

        (man.jumpCycle > 0 && man.isonEggs(eggs)) ->
            return Game(man.copy(jumpCycle = man.jumpCycle - 1).jump(), floor, stairs, man.removeEggs(eggs), food)

        (man.jumpCycle > 0) ->
            return Game(man.copy(jumpCycle = man.jumpCycle - 1).jump(), floor, stairs, eggs, food)

        (man.DetectIfisStairs(stairs) && man.speed.isZero() && man.stateJump==false) ->
            return Game(man.moveUpDown(), floor, stairs, eggs, food)

        (!man.DetectIfisStairs(stairs) && !man.DetectIfisFloor(floor)) ->
            return Game(man.gravity(), floor, stairs, eggs, food)

        (man.stateJump && !man.DetectIfisStairs(stairs)) ->
            return Game(man.copy(stateJump = false, speed = Speed(0, 0)), floor, stairs, eggs, food)

        (man.isonEggs(eggs)) ->
            return Game (man.move(), floor, stairs , man.removeEggs(eggs), food)

        (man.isonFood(food)) ->
            return Game (man.move(), floor, stairs , eggs, man.removefood(food))

        else ->
            return Game(man.move(), floor, stairs, eggs, food)
    }
}





