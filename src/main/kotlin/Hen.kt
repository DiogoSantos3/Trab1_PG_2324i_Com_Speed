
data class Hen (val pos: Point, val faced: Direction, val stateJump: Boolean, val speed: Speed, val jumpCycle: Int,val animationCicle:Int)


fun createHen(cell: Cell) = Hen(
    pos = cell.toPoint(),
    faced = Direction.LEFT,
    false,
    speed = Speed(0, 0),
    jumpCycle = 0,
    animationCicle = 0
)


fun Hen.move(): Hen {
     while (true){
        return Hen(pos.plus(speed).limitToArea(MAX_X, MAX_Y), faced, stateJump, speed.stopIfInCell(pos.plus(speed)), 0, this.animationCicle)
    }

}