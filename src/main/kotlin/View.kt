import pt.isel.canvas.*
import javax.xml.crypto.Data

// Dimensions of the sprites in the images files
// Floor, Egg, Food and Stair are 1x1 ; Man is 1x2 ; Hen is 1x3 or 2x2
const val SPRITE_WIDTH = 24  // [pixels in image file]
const val SPRITE_HEIGHT = 16 // [pixels in image file]

// Dimensions of the Arena grid
const val GRID_WIDTH = 20
const val GRID_HEIGHT = 24

// Dimensions of each cell of the Arena grid
const val VIEW_FACTOR = 2 // each cell is VIEW_FACTOR x sprite
const val CELL_WIDTH = VIEW_FACTOR * SPRITE_WIDTH   // [pixels]
const val CELL_HEIGHT = VIEW_FACTOR * SPRITE_HEIGHT  // [pixels]



/**
 * Creates a canvas with the dimensions of the arena.
 */
fun createCanvas() = Canvas(GRID_WIDTH * CELL_WIDTH, GRID_HEIGHT * CELL_HEIGHT, BLACK)

/**
 * Draw horizontal and vertical lines of the grid in arena.
 */
fun Canvas.drawGridLines() {
    (0..<width step CELL_WIDTH).forEach { x -> drawLine(x, 0, x, height, WHITE, 1) }
    (0..<height step CELL_HEIGHT).forEach { y -> drawLine(0, y, width, y, WHITE, 1) }
}

/**
 * Represents a sprite in the image.
 * Example: Sprite(2,3,1,1) is the man facing left.
 * @property row the row of the sprite in the image. (in sprites)
 * @property col the column of the sprite in the image. (in sprites)
 * @property height the height of the sprite in the image. (in sprites)
 * @property width the width of the sprite in the image. (in sprites)
 */
data class Sprite(val row: Int, val col: Int, val height: Int = 1, val width: Int = 1)

/**
 * Draw a sprite in a position of the canvas.
 * @param pos the position in the canvas (top-left of base cell).
 * @param spriteRow the row of the sprite in the image.
 * @param spriteCol the column of the sprite in the image.
 * @param spriteHeight the height of the sprite in the image.
 * @param spriteWidth the width of the sprite in the image.
 */
fun Canvas.drawSprite(pos: Point, s: Sprite) {

    val x = s.col * SPRITE_WIDTH + s.col + 1  // in pixels
    val y = s.row * SPRITE_HEIGHT + s.row + s.height
    val h = s.height * SPRITE_HEIGHT
    val w = s.width * SPRITE_WIDTH
    drawImage(
        fileName = "chuckieEgg|$x,$y,$w,$h",
        xLeft = pos.x,
        yTop = pos.y - (s.height - 1) * CELL_HEIGHT,
        width = CELL_WIDTH * s.width,
        height = CELL_HEIGHT * s.height
    )
}


//Draw the score we have
fun Canvas.drawScore(num: Int) {
    val x = 50
    val y = 64
    drawText(x, y, "Score : $num", YELLOW, 40)
}

//Draw the time we have to finish the game
fun Canvas.drawTime(num: Int) {
    val x = 673
    val y = 64
    drawText(x, y, "Time : $num", YELLOW, 40)
}

//Ends the game when there is no food or eggs left or when there is no time left
fun Canvas.endGame(game: Game): Int{
    val x = 180
    val y = 400
    when {
        (game.eggs.isEmpty() && game.food.isEmpty()) -> {
            drawText(x, y, "YOU WON", GREEN, 120)
        }
        (game.time == 0) -> drawText(x, y, "YOU LOSE", RED, 120)
    }
    return 0
}


/**
 * Draw all the elements of the game.
 */
fun Canvas.drawGame(game: Game) {
    erase()
    //drawGridLines()
    game.floor.forEach { drawSprite(it.toPoint(), Sprite(0, 0)) }
    game.stairs.forEach { drawSprite(it.toPoint(), Sprite(0, 1)) }
    game.eggs.forEach { drawSprite(it.toPoint(), Sprite(1, 1)) }
    game.food.forEach { drawSprite(it.toPoint(), Sprite(1, 0)) }

    drawScore(game.score)
    drawTime(game.time)
    drawMan(game.man)
    endGame(game)
}

/**
 * Draws the man in canvas according to the direction he is facing.
 */

/*
fun Canvas.drawMan(m: Man) {
    val sprite = getManSprite(m)
    drawSprite(m.pos, sprite)
}

fun getManSprite(m: Man): Sprite {
    val xOffset = if (m.faced == Direction.LEFT) 2 else 0
    val yOffset = if (m.faced == Direction.UP) 1 else if (m.faced == Direction.DOWN) 3 else 2
    val cycle = m.animationCicle % 5

    return when (cycle) {
        1 -> Sprite(xOffset, yOffset + 2, 2)
        2 -> Sprite(xOffset, yOffset + 1, 2)
        3 -> Sprite(xOffset, yOffset, 2)
        4 -> Sprite(xOffset, yOffset + 1, 2)
        else -> Sprite(xOffset, yOffset + 2, 2)
    }
}*/


fun Canvas.drawMan(m: Man) {
    val sprite = when(m.faced) {
        //Animations for Man
        Direction.LEFT ->
            when (m.animationCicle) {
                1-> Sprite(2, 4, 2)
                2-> Sprite(2,3,2)
                3->Sprite(2,2,2)
                4->Sprite(2,3,2)
                5->Sprite(2, 4, 2)
                else-> Sprite(2,3,2)//Stopped
            }

        Direction.RIGHT ->
            when (m.animationCicle) {
                1-> Sprite(0, 4, 2)
                2-> Sprite(0,3,2)
                3->Sprite(0,2,2)
                4->Sprite(0,3,2)
                5->Sprite(0, 4, 2)
                else-> Sprite(0,3,2)//Stopped
            }

        Direction.UP->
            when (m.animationCicle) {
                1-> Sprite(4, 3, 2)
                2-> Sprite(4, 2, 2)
                3->Sprite(4, 1, 2)
                else ->Sprite(4, 0, 2)//Stopped
            }

        Direction.DOWN ->
            when (m.animationCicle) {
                1-> Sprite(4, 4, 2)
                2-> Sprite(4, 3, 2)
                3-> Sprite(4, 2, 2)
                4-> Sprite(4, 1, 2)
                else -> Sprite(4, 0, 2)//Stopped

            }
    }
    drawSprite(m.pos, sprite)
}