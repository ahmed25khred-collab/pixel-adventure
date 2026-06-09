package com.pixeladventure.game

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.random.Random

class GameView(context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var gameThread: GameThread? = null
    private var isRunning = false
    private var isPaused = false
    private var score = 0
    private var gameOver = false
    
    private val player = Player()
    private val enemies = mutableListOf<Enemy>()
    private val coins = mutableListOf<Coin>()
    private val obstacles = mutableListOf<Obstacle>()
    
    private var gameListener: GameListener? = null
    
    private var lastEnemySpawn = System.currentTimeMillis()
    private var lastCoinSpawn = System.currentTimeMillis()

    interface GameListener {
        fun onScoreChange(score: Int)
        fun onGameOver(finalScore: Int)
    }

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    fun setGameListener(listener: GameListener) {
        gameListener = listener
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread = GameThread(holder, this)
        gameThread?.start()
        isRunning = true
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isRunning = false
        var retry = true
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null && event.action == MotionEvent.ACTION_DOWN) {
            if (gameOver) {
                restartGame()
            } else {
                player.jump()
            }
        }
        return true
    }

    fun draw(canvas: Canvas) {
        if (!isRunning) return

        val gradient = LinearGradient(0f, 0f, 0f, height.toFloat(), 
            Color.parseColor("#87CEEB"), Color.parseColor("#E0F6FF"), Shader.TileMode.CLAMP)
        val paint = Paint().apply { shader = gradient }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        if (!isPaused) {
            update()
        }

        player.draw(canvas, width, height)
        coins.forEach { it.draw(canvas) }
        enemies.forEach { it.draw(canvas) }
        obstacles.forEach { it.draw(canvas) }

        drawScore(canvas)

        if (gameOver) {
            drawGameOverScreen(canvas)
        }
    }

    private fun update() {
        player.update(width, height)

        coins.removeAll { coin ->
            coin.update()
            coin.y > height
        }

        enemies.removeAll { enemy ->
            enemy.update()
            if (player.collidesWith(enemy)) {
                gameOver = true
                gameListener?.onGameOver(score)
            }
            enemy.x < -enemy.size || enemy.x > width
        }

        obstacles.removeAll { obstacle ->
            obstacle.update()
            if (player.collidesWith(obstacle)) {
                gameOver = true
                gameListener?.onGameOver(score)
            }
            obstacle.y > height
        }

        coins.forEach { coin ->
            if (player.collidesWith(coin)) {
                coins.remove(coin)
                score += 10
                gameListener?.onScoreChange(score)
            }
        }

        if (System.currentTimeMillis() - lastCoinSpawn > 1000) {
            coins.add(Coin(Random.nextInt(0, width).toFloat()))
            lastCoinSpawn = System.currentTimeMillis()
        }

        if (System.currentTimeMillis() - lastEnemySpawn > 2000) {
            enemies.add(Enemy(Random.nextInt(0, width).toFloat(), -50f))
            lastEnemySpawn = System.currentTimeMillis()
        }
    }

    private fun drawScore(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.WHITE
            textSize = 50f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Score: \$score", 30f, 80f, paint)
    }

    private fun drawGameOverScreen(canvas: Canvas) {
        val darkPaint = Paint().apply {
            color = Color.parseColor("#80000000")
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), darkPaint)

        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 80f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("GAME OVER", width / 2f, height / 2f, textPaint)

        val scorePaint = Paint().apply {
            color = Color.YELLOW
            textSize = 60f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Score: \$score", width / 2f, height / 2f + 100f, scorePaint)
    }

    fun pauseGame() {
        isPaused = true
    }

    fun resumeGame() {
        isPaused = false
    }

    fun restartGame() {
        score = 0
        gameOver = false
        isPaused = false
        player.reset(width, height)
        enemies.clear()
        coins.clear()
        obstacles.clear()
        lastEnemySpawn = System.currentTimeMillis()
        lastCoinSpawn = System.currentTimeMillis()
        gameListener?.onScoreChange(score)
    }

    private inner class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {
        override fun run() {
            while (isRunning) {
                var canvas: Canvas? = null
                try {
                    canvas = surfaceHolder.lockCanvas()
                    synchronized(surfaceHolder) {
                        canvas?.let { gameView.draw(it) }
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    }
                }
                Thread.sleep(16)
            }
        }
    }
}

class Player {
    var x = 0f
    var y = 0f
    var width = 60f
    var height = 80f
    private var velocityY = 0f
    private val gravity = 0.5f
    private val jumpPower = -20f
    private var isJumping = false

    fun update(screenWidth: Int, screenHeight: Int) {
        velocityY += gravity
        y += velocityY

        if (y + height > screenHeight) {
            y = (screenHeight - height).toFloat()
            isJumping = false
            velocityY = 0f
        }
    }

    fun jump() {
        if (!isJumping) {
            velocityY = jumpPower
            isJumping = true
        }
    }

    fun draw(canvas: Canvas, screenWidth: Int, screenHeight: Int) {
        if (x == 0f) {
            x = (screenWidth / 2 - width / 2).toFloat()
            y = (screenHeight - 200).toFloat()
        }

        val paint = Paint().apply {
            color = Color.parseColor("#FF6B6B")
            style = Paint.Style.FILL
        }
        canvas.drawRect(x, y, x + width, y + height, paint)

        val eyePaint = Paint().apply { color = Color.WHITE }
        canvas.drawCircle(x + 15, y + 20, 5f, eyePaint)
        canvas.drawCircle(x + 45, y + 20, 5f, eyePaint)
    }

    fun collidesWith(other: Any): Boolean {
        return when (other) {
            is Enemy -> {
                x < other.x + other.size && x + width > other.x &&
                y < other.y + other.size && y + height > other.y
            }
            is Coin -> {
                x < other.x + other.size && x + width > other.x &&
                y < other.y + other.size && y + height > other.y
            }
            is Obstacle -> {
                x < other.x + other.width && x + width > other.x &&
                y < other.y + other.height && y + height > other.y
            }
            else -> false
        }
    }

    fun reset(screenWidth: Int, screenHeight: Int) {
        x = (screenWidth / 2 - width / 2).toFloat()
        y = (screenHeight - 200).toFloat()
        velocityY = 0f
        isJumping = false
    }
}

class Enemy(var x: Float, var y: Float) {
    val size = 50f
    private val speed = 5f
    private val paint = Paint().apply {
        color = Color.parseColor("#FF0000")
        style = Paint.Style.FILL
    }

    fun update() {
        y += speed
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(x + size / 2, y + size / 2, size / 2, paint)
        
        val eyePaint = Paint().apply { color = Color.WHITE }
        canvas.drawCircle(x + 15, y + 15, 5f, eyePaint)
        canvas.drawCircle(x + 35, y + 15, 5f, eyePaint)
    }
}

class Coin(var x: Float) {
    var y = 0f
    val size = 30f
    private val speed = 3f
    private val paint = Paint().apply {
        color = Color.parseColor("#FFD700")
        style = Paint.Style.FILL
    }

    fun update() {
        y += speed
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(x + size / 2, y + size / 2, size / 2, paint)
        
        val shinePaint = Paint().apply {
            color = Color.WHITE
            alpha = 150
        }
        canvas.drawCircle(x + size / 3, y + size / 3, size / 6, shinePaint)
    }
}

class Obstacle(var x: Float, var y: Float) {
    val width = 100f
    val height = 20f
    private val speed = 2f
    private val paint = Paint().apply {
        color = Color.parseColor("#8B4513")
        style = Paint.Style.FILL
    }

    fun update() {
        y += speed
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(x, y, x + width, y + height, paint)
    }
}