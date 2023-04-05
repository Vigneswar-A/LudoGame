package com.example.testludo

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock.uptimeMillis
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testludo.ui.theme.TestLudoTheme
import kotlinx.coroutines.*
import kotlin.math.max
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestLudoTheme {
                if(!GameManager.started)
                    MainMenu()
                else if(!GameManager.over)
                    Ludo()
                else
                    LeaderBoard()
            }
        }
    }
}

@Composable
fun LeaderBoard(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(54.dp))
        Text(
            "Leader Boards",
            color = Color(63, 81, 181, 255),
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.height(64.dp))
        for(i in 1 .. GameManager.winners.size) {
            Text(
                "$i ${GameManager.winners[i-1]}",
                modifier = Modifier.width(250.dp),
                color = GameManager.winners[i-1].color,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = {
                GameManager.over = false
                GameManager.started = false
                GameManager.winners.clear()
                      },
            colors = ButtonDefaults.buttonColors(Color.Green),
            border = BorderStroke(1.dp, Color.Black)
        ){
            Text(
                "Exit",
                modifier = Modifier.width(200.dp),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun MainMenu(){
    val mContext = LocalContext.current
    val mMediaPlayer = MediaPlayer.create(mContext, R.raw.click)
    val uiModifier = Modifier
        .background(Color(73, 250, 103))
        .height(84.dp)
        .width(300.dp)
        .border(2.dp, Color.Black, RoundedCornerShape(15.dp))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(47, 176, 226)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        Spacer(modifier = Modifier.height(54.dp))
        Text(
            text = "Ludo Game",
            color = Color.Red,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(108.dp))
        Card(shape = RoundedCornerShape(15.dp)) {
            Button(
                onClick = { GameManager.players = max((GameManager.players + 1) % 5, 2);mMediaPlayer.start()},
                colors = ButtonDefaults.buttonColors(Color(73, 250, 103)),
                modifier = uiModifier
            ) {
                Text(
                    "${GameManager.players} Players",
                    color = Color.Red,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Card(shape = RoundedCornerShape(15.dp)) {
            Button(
                onClick = { GameManager.started = true;mMediaPlayer.start() },
                colors = ButtonDefaults.buttonColors(Color(73, 250, 103)),
                modifier = uiModifier,
            ) {
                Text(
                    "Play",
                    color = Color.Red,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

object GameManager{
    var started by mutableStateOf(false)
    var over by mutableStateOf(false)
    var players by mutableStateOf(2)
    var currentPlayer by mutableStateOf(0)
    fun nextPlayer(){
        currentPlayer = (currentPlayer+1)%players
    }
    var winners = mutableListOf<Teams>()
}

object RandomChoice {
    val randomizer = Random(uptimeMillis())
}

enum class Teams(val color: Color){
    RED(Color(200, 0, 0)),
    BLUE(Color(0, 0, 200)),
    GREEN(Color(0, 200, 0)),
    YELLOW(Color(200, 200, 0))
}

class Team(val team: Teams){
    var coinsIn = 0
    var score = 0
    val coins = when(team){
        Teams.RED -> listOf(
            Coin(listOf(35.dp to 35.dp), Teams.RED, 0),
            Coin(listOf(65.dp to 35.dp), Teams.RED, 0),
            Coin(listOf(35.dp to 65.dp), Teams.RED, 0),
            Coin(listOf(65.dp to 65.dp), Teams.RED, 0)
        )

        Teams.BLUE -> listOf(
            Coin(listOf(215.dp to 35.dp), Teams.BLUE, 1),
            Coin(listOf(245.dp to 35.dp), Teams.BLUE, 1),
            Coin(listOf(215.dp to 65.dp), Teams.BLUE, 1),
            Coin(listOf(245.dp to 65.dp), Teams.BLUE, 1)
        )

        Teams.GREEN -> listOf(
            Coin(listOf(215.dp to 215.dp), Teams.GREEN, 2),
            Coin(listOf(215.dp to 245.dp), Teams.GREEN, 2),
            Coin(listOf(245.dp to 215.dp), Teams.GREEN, 2),
            Coin(listOf(245.dp to 245.dp), Teams.GREEN, 2)
        )

        Teams.YELLOW -> listOf(
            Coin(listOf(35.dp to 215.dp), Teams.YELLOW, 3),
            Coin(listOf(65.dp to 215.dp), Teams.YELLOW, 3),
            Coin(listOf(35.dp to 245.dp), Teams.YELLOW, 3),
            Coin(listOf(65.dp to 245.dp), Teams.YELLOW, 3)
        )
    }
}

object DiceManager{
    var diceFace by mutableStateOf(1)
    var waiting by mutableStateOf(false)
    var extra = 0
    var bonus = 0
}

object Players{
    val teams = mutableListOf<Team>()
}

val totalTeams = Teams.values()

@Composable
fun Ludo(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(142, 239, 255, 255)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        for (i in 0 until GameManager.players){
            Players.teams.add(Team(totalTeams[i]))
        }

        Box(
            modifier = Modifier
                .background(Color(255, 152, 0, 255))
                .border(1.dp, Color.Black)
                .height(54.dp)
                .width(250.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Player: ${totalTeams[GameManager.currentPlayer]}",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(30, 112, 255, 255)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .background(Color(255, 152, 0, 255))
                .border(1.dp, Color.Black)
                .height(54.dp)
                .width(250.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Status: ${if(DiceManager.waiting) "Move" else "Roll"}",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(30, 112, 255, 255)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Board()
        Dice()
        val mContext = LocalContext.current
        val mMediaPlayer = MediaPlayer.create(mContext, R.raw.dicesound)
        Button(onClick = {
            if (DiceManager.waiting)
                return@Button
            mMediaPlayer.start()
            val roll = RandomChoice.randomizer.nextInt(1, 7)
            if (Players.teams[GameManager.currentPlayer].coinsIn + Players.teams[GameManager.currentPlayer].score == 4){
                if(DiceManager.extra == 2) {
                    DiceManager.waiting = false
                    DiceManager.extra = 0
                    DiceManager.bonus = 0
                    GameManager.nextPlayer()
                    return@Button
                }
                if(roll == 6){
                    DiceManager.diceFace = 6
                    DiceManager.extra++
                    DiceManager.bonus += roll
                    var canMove = false
                    for(coin in Players.teams[GameManager.currentPlayer].coins)
                        if (coin.idx != 0 && coin.idx + DiceManager.bonus+DiceManager.diceFace < coin.path.size)
                            canMove = true
                    if (!canMove){
                        DiceManager.bonus = 0
                        DiceManager.extra = 0
                        GameManager.nextPlayer()
                    }
                    return@Button
                }
            }
            DiceManager.bonus = 0
            DiceManager.extra = 0
            DiceManager.diceFace = roll

            var canMove = false
            for(coin in Players.teams[GameManager.currentPlayer].coins)
                if (coin.idx != 0 && coin.idx + DiceManager.bonus+DiceManager.diceFace < coin.path.size)
                    canMove = true

            if((Players.teams[GameManager.currentPlayer].coinsIn > 0 && canMove) ||
                (DiceManager.diceFace == 6 && Players.teams[GameManager.currentPlayer].score + Players.teams[GameManager.currentPlayer].coinsIn < 4))
                DiceManager.waiting = true
            else
                GameManager.nextPlayer()

        }){
            Text("Roll")
        }

    }
}


@Composable
fun Dice(){
    Row(verticalAlignment = Alignment.CenterVertically){

        Image(
            painter = painterResource(
                id = when(DiceManager.diceFace){
                    1 -> R.drawable.dice_1
                    2 -> R.drawable.dice_2
                    3 -> R.drawable.dice_3
                    4 -> R.drawable.dice_4
                    5 -> R.drawable.dice_5
                    6 -> R.drawable.dice_6
                    else -> R.drawable.dice_1
                }),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
    }
}

object Paths {
    val redPath = listOf(
        20.dp to 120.dp,
        40.dp to 120.dp,
        60.dp to 120.dp,
        80.dp to 120.dp,
        100.dp to 120.dp,
        120.dp to 100.dp,
        120.dp to 80.dp,
        120.dp to 60.dp,
        120.dp to 40.dp,
        120.dp to 20.dp,
        120.dp to 0.dp,
        140.dp to 0.dp,
        160.dp to 0.dp,
        160.dp to 20.dp,
        160.dp to 40.dp,
        160.dp to 60.dp,
        160.dp to 80.dp,
        160.dp to 100.dp,
        180.dp to 120.dp,
        200.dp to 120.dp,
        220.dp to 120.dp,
        240.dp to 120.dp,
        260.dp to 120.dp,
        280.dp to 120.dp,
        280.dp to 140.dp,
        280.dp to 160.dp,
        260.dp to 160.dp,
        240.dp to 160.dp,
        220.dp to 160.dp,
        200.dp to 160.dp,
        180.dp to 160.dp,
        160.dp to 180.dp,
        160.dp to 200.dp,
        160.dp to 220.dp,
        160.dp to 240.dp,
        160.dp to 260.dp,
        160.dp to 280.dp,
        140.dp to 280.dp,
        120.dp to 280.dp,
        120.dp to 260.dp,
        120.dp to 240.dp,
        120.dp to 220.dp,
        120.dp to 200.dp,
        120.dp to 180.dp,
        100.dp to 160.dp,
        80.dp to 160.dp,
        60.dp to 160.dp,
        40.dp to 160.dp,
        20.dp to 160.dp,
        0.dp to 160.dp,
        0.dp to 140.dp,
        20.dp to 140.dp,
        40.dp to 140.dp,
        60.dp to 140.dp,
        80.dp to 140.dp,
        100.dp to 140.dp,
        120.dp to 140.dp,
    )

    val bluePath = listOf(
        160.dp to 20.dp,
        160.dp to 40.dp,
        160.dp to 60.dp,
        160.dp to 80.dp,
        160.dp to 100.dp,
        180.dp to 120.dp,
        200.dp to 120.dp,
        220.dp to 120.dp,
        240.dp to 120.dp,
        260.dp to 120.dp,
        280.dp to 120.dp,
        280.dp to 140.dp,
        280.dp to 160.dp,
        260.dp to 160.dp,
        240.dp to 160.dp,
        220.dp to 160.dp,
        200.dp to 160.dp,
        180.dp to 160.dp,
        160.dp to 180.dp,
        160.dp to 200.dp,
        160.dp to 220.dp,
        160.dp to 240.dp,
        160.dp to 260.dp,
        160.dp to 280.dp,
        140.dp to 280.dp,
        120.dp to 280.dp,
        120.dp to 260.dp,
        120.dp to 240.dp,
        120.dp to 220.dp,
        120.dp to 200.dp,
        120.dp to 180.dp,
        100.dp to 160.dp,
        80.dp to 160.dp,
        60.dp to 160.dp,
        40.dp to 160.dp,
        20.dp to 160.dp,
        0.dp to 160.dp,
        0.dp to 140.dp,
        0.dp to 120.dp,
        20.dp to 120.dp,
        40.dp to 120.dp,
        60.dp to 120.dp,
        80.dp to 120.dp,
        100.dp to 120.dp,
        120.dp to 100.dp,
        120.dp to 80.dp,
        120.dp to 60.dp,
        120.dp to 40.dp,
        120.dp to 20.dp,
        120.dp to 0.dp,
        140.dp to 0.dp,
        140.dp to 20.dp,
        140.dp to 40.dp,
        140.dp to 60.dp,
        140.dp to 80.dp,
        140.dp to 100.dp,
        140.dp to 120.dp
    )

    val greenPath = listOf(
        260.dp to 160.dp,
        240.dp to 160.dp,
        220.dp to 160.dp,
        200.dp to 160.dp,
        180.dp to 160.dp,
        160.dp to 180.dp,
        160.dp to 200.dp,
        160.dp to 220.dp,
        160.dp to 240.dp,
        160.dp to 260.dp,
        160.dp to 280.dp,
        140.dp to 280.dp,
        120.dp to 280.dp,
        120.dp to 260.dp,
        120.dp to 240.dp,
        120.dp to 220.dp,
        120.dp to 200.dp,
        120.dp to 180.dp,
        100.dp to 160.dp,
        80.dp to 160.dp,
        60.dp to 160.dp,
        40.dp to 160.dp,
        20.dp to 160.dp,
        0.dp to 160.dp,
        0.dp to 140.dp,
        0.dp to 120.dp,
        20.dp to 120.dp,
        40.dp to 120.dp,
        60.dp to 120.dp,
        80.dp to 120.dp,
        100.dp to 120.dp,
        120.dp to 100.dp,
        120.dp to 80.dp,
        120.dp to 60.dp,
        120.dp to 40.dp,
        120.dp to 20.dp,
        120.dp to 0.dp,
        140.dp to 0.dp,
        160.dp to 0.dp,
        160.dp to 20.dp,
        160.dp to 40.dp,
        160.dp to 60.dp,
        160.dp to 80.dp,
        160.dp to 100.dp,
        180.dp to 120.dp,
        200.dp to 120.dp,
        220.dp to 120.dp,
        240.dp to 120.dp,
        260.dp to 120.dp,
        280.dp to 120.dp,
        280.dp to 140.dp,
        260.dp to 140.dp,
        240.dp to 140.dp,
        220.dp to 140.dp,
        200.dp to 140.dp,
        180.dp to 140.dp,
        160.dp to 140.dp,
    )

    val yellowPath = listOf(
        120.dp to 280.dp,
        120.dp to 260.dp,
        120.dp to 240.dp,
        120.dp to 220.dp,
        120.dp to 200.dp,
        120.dp to 180.dp,
        100.dp to 160.dp,
        80.dp to 160.dp,
        60.dp to 160.dp,
        40.dp to 160.dp,
        20.dp to 160.dp,
        0.dp to 160.dp,
        0.dp to 140.dp,
        0.dp to 120.dp,
        20.dp to 120.dp,
        40.dp to 120.dp,
        60.dp to 120.dp,
        80.dp to 120.dp,
        100.dp to 120.dp,
        120.dp to 100.dp,
        120.dp to 80.dp,
        120.dp to 60.dp,
        120.dp to 40.dp,
        120.dp to 20.dp,
        120.dp to 0.dp,
        140.dp to 0.dp,
        160.dp to 0.dp,
        160.dp to 20.dp,
        160.dp to 40.dp,
        160.dp to 60.dp,
        160.dp to 80.dp,
        160.dp to 100.dp,
        180.dp to 120.dp,
        200.dp to 120.dp,
        220.dp to 120.dp,
        240.dp to 120.dp,
        260.dp to 120.dp,
        280.dp to 120.dp,
        280.dp to 140.dp,
        280.dp to 160.dp,
        260.dp to 160.dp,
        240.dp to 160.dp,
        220.dp to 160.dp,
        200.dp to 160.dp,
        180.dp to 160.dp,
        160.dp to 180.dp,
        160.dp to 200.dp,
        160.dp to 220.dp,
        160.dp to 240.dp,
        160.dp to 260.dp,
        160.dp to 280.dp,
        140.dp to 280.dp,
        140.dp to 260.dp,
        140.dp to 240.dp,
        140.dp to 220.dp,
        140.dp to 200.dp,
        140.dp to 180.dp,
        140.dp to 160.dp,
    )
}

var stall = false

class Coin(path : List<Pair<Dp, Dp>>, private val team: Teams, private val teamId: Int) {
    var idx by mutableStateOf(0)
    private lateinit var context: Context
    private var soundScope = CoroutineScope(AndroidUiDispatcher.Main)

    val path = when(team){
        Teams.RED -> path + Paths.redPath
        Teams.BLUE -> path + Paths.bluePath
        Teams.GREEN -> path + Paths.greenPath
        Teams.YELLOW -> path + Paths.yellowPath
    }

    private suspend fun move(){
        val steps = DiceManager.diceFace + DiceManager.bonus
        if (stall || idx+steps >= path.size)
            return
        stall = true
        if (idx == 0){
            if (DiceManager.diceFace == 6) {
                playSound()
                idx++
                Players.teams[GameManager.currentPlayer].coinsIn++

            }
            else{
                stall = false
                return
            }
        }
        else {
            repeat(steps) {
                playSound()
                if (idx < path.size-1) idx++
                delay(500)
            }
            if (idx == path.size-1){
                Players.teams[teamId].score++
                Players.teams[teamId].coinsIn--
                if(Players.teams[teamId].score == 4){
                    playTeamWinSound()
                    GameManager.winners.add(Players.teams[teamId].team)
                    if(GameManager.winners.size == GameManager.players-1){
                        for(team in Teams.values()){
                            if (!GameManager.winners.contains(team)) {
                                GameManager.winners.add(team)
                                break
                            }
                        }
                        GameManager.over = true
                    }
                }
                else
                    playWinSound()
            }
        }
        var count = 0
        for(player in Players.teams)
            if(player.team != team)
                for(coin in player.coins)
                    if(coin.path[coin.idx] == path[idx]) {
                        count++
                    }

        if(count == 1)
            for(player in Players.teams)
                if(player.team != team)
                    for(coin in player.coins)
                        if(coin.path[coin.idx] == path[idx]) {
                            coin.idx = 0
                            Players.teams[coin.teamId].coinsIn--
                        }

        if(count > 1){
            idx = 0
            Players.teams[teamId].coinsIn -= 1
        }
        stall = false
        DiceManager.waiting = false
        DiceManager.bonus = 0
        DiceManager.extra = 0
        GameManager.nextPlayer()
    }

    private fun playSound(){
        soundScope.launch {
            val mMediaPlayer = MediaPlayer.create(context, R.raw.coinsound)
            mMediaPlayer.start()
        }
    }

    private fun playWinSound(){
        soundScope.launch {
            val mMediaPlayer = MediaPlayer.create(context, R.raw.winsound)
            mMediaPlayer.start()
        }
    }

    private fun playTeamWinSound(){
        soundScope.launch {
            val mMediaPlayer = MediaPlayer.create(context, R.raw.totalwinsound)
            mMediaPlayer.start()
        }
    }

    @Composable
    fun Display(){
        context = LocalContext.current
        val coinCoroutineScope = remember{
            CoroutineScope(AndroidUiDispatcher.Main)
        }

        DisposableEffect(
            coinCoroutineScope,
            effect = {
                onDispose { coinCoroutineScope.cancel() }
            }
        )

        DisposableEffect(
            soundScope,
            effect = {
                onDispose { soundScope.cancel() }
            }
        )

        val posX : Dp by animateDpAsState(
            targetValue = path[idx].first,
            animationSpec = tween(
                durationMillis = 100,
                delayMillis = 200,
                easing = LinearEasing
            )
        )

        val posY : Dp by animateDpAsState(
            targetValue = path[idx].second,
            animationSpec = tween(
                durationMillis = 100,
                delayMillis = 200,
                easing = LinearEasing
            )
        )

        Canvas(
            modifier = Modifier
                .size(20.dp)
                .offset(posX, posY)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        coinCoroutineScope.launch {
                            if (!DiceManager.waiting || GameManager.currentPlayer != teamId)
                                return@launch
                            move()
                        }
                    })
                }
        ){
            drawCircle(
                team.color,
                radius = 10.dp.toPx(),
            )
            drawCircle(
                Color.White,
                radius = 5.dp.toPx(),
            )
        }

    }
}

@Composable
fun Board(){
    val boardPattern = listOf(
        listOf(0, 0, 0),
        listOf(0, 1, 1),
        listOf(0, 1, 0),
        listOf(0, 1, 0),
        listOf(0, 1, 0),
        listOf(0, 1, 0)
    )
    Box(modifier = Modifier.size(300.dp)){
        Column(
            modifier = Modifier.matchParentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .height(120.dp)
                    .width(300.dp)){
                TeamBase(Color.Red)
                Column(
                    Modifier
                        .height(120.dp)
                        .width(60.dp)) {
                    for (row in boardPattern) {
                        Row(
                            Modifier
                                .height(20.dp)
                                .width(60.dp)) {
                            for (item in row) {
                                if (item == 1)
                                    BoardCell(Color.Blue)
                                else
                                    BoardCell()
                            }
                        }
                    }
                }
                TeamBase(Color.Blue)
            }

            Row(
                Modifier
                    .height(60.dp)
                    .width(300.dp)){
                Row(
                    Modifier
                        .height(60.dp)
                        .width(120.dp)){
                    for (row in boardPattern) {
                        Column(
                            Modifier
                                .height(60.dp)
                                .width(20.dp)) {
                            for (item in row.reversed()) {
                                if (item == 1)
                                    BoardCell(Color.Red)
                                else
                                    BoardCell()
                            }
                        }
                    }
                }
                End()
                Row(
                    Modifier
                        .height(60.dp)
                        .width(120.dp)) {
                    for (row in boardPattern.reversed()) {
                        Column(
                            Modifier
                                .height(60.dp)
                                .width(20.dp)) {
                            for (item in row) {
                                if (item == 1)
                                    BoardCell(Color.Green)
                                else
                                    BoardCell()
                            }
                        }
                    }
                }
            }

            Row(
                Modifier
                    .height(120.dp)
                    .width(300.dp)){
                TeamBase(Color.Yellow)
                Column(
                    Modifier
                        .height(120.dp)
                        .width(60.dp)) {
                    for (row in boardPattern.reversed()) {
                        Row(
                            Modifier
                                .height(20.dp)
                                .width(60.dp)) {
                            for (item in row.reversed()) {
                                if (item == 1)
                                    BoardCell(Color.Yellow)
                                else
                                    BoardCell()
                            }
                        }
                    }
                }
                TeamBase(Color.Green)
            }
        }
        for (team in Players.teams)
            for (coin in team.coins)
                coin.Display()
    }
}

@Composable
fun End(){
    Canvas(modifier = Modifier
        .size(60.dp)
        .border(1.dp, Color.Black)){
        var path = Path()
        path.moveTo(x=0.dp.toPx(), y=0.dp.toPx())
        path.lineTo(x=30.dp.toPx(), y=30.dp.toPx())
        path.lineTo(x=0.dp.toPx(), y=60.dp.toPx())
        path.lineTo(x=0.dp.toPx(), y=0.dp.toPx())
        path.close()
        drawPath(path, color = Color.Red)

        path = Path()
        path.moveTo(x=0.dp.toPx(), y=0.dp.toPx())
        path.lineTo(x=30.dp.toPx(), y=30.dp.toPx())
        path.lineTo(x=60.dp.toPx(), y=0.dp.toPx())
        path.lineTo(x=0.dp.toPx(), y=0.dp.toPx())
        path.close()
        drawPath(path, color = Color.Blue)

        path = Path()
        path.moveTo(x=0.dp.toPx(), y=60.dp.toPx())
        path.lineTo(x=30.dp.toPx(), y=30.dp.toPx())
        path.lineTo(x=60.dp.toPx(), y=60.dp.toPx())
        path.lineTo(x=0.dp.toPx(), y=60.dp.toPx())
        path.close()
        drawPath(path, color = Color.Yellow)

        path = Path()
        path.moveTo(x=60.dp.toPx(), y=0.dp.toPx())
        path.lineTo(x=30.dp.toPx(), y=30.dp.toPx())
        path.lineTo(x=60.dp.toPx(), y=60.dp.toPx())
        path.lineTo(x=60.dp.toPx(), y=0.dp.toPx())
        path.close()
        drawPath(path, color = Color.Green)
    }
}

@Composable
fun BoardCell(teamColor: Color = Color.White){
    Canvas(modifier = Modifier
        .size(20.dp)
        .border(1.dp, Color.Black)){
        drawRect(
            color = teamColor,
            size = size
        )
    }
}

@Composable
fun TeamBase(teamColor: Color){
    Canvas(modifier = Modifier
        .size(120.dp)
        .border(1.dp, Color.Black)
) {
        val canvasQuadrantSize = size
        drawRect(
            color = teamColor,
            size = canvasQuadrantSize
        )
        drawRect(
            color = Color.Black,
            size = Size(height=84.dp.toPx(), width=84.dp.toPx()),
            topLeft = Offset(18.dp.toPx(), 18.dp.toPx())
        )
        drawRect(
            color = Color.White,
            size = Size(height=80.dp.toPx(), width=80.dp.toPx()),
            topLeft = Offset(20.dp.toPx(), 20.dp.toPx())
        )

        drawCircle(color=Color.Black, radius = 11.dp.toPx(), center=Offset(45.dp.toPx(), 45.dp.toPx()))
        drawCircle(color=Color.Black, radius = 11.dp.toPx(), center=Offset(45.dp.toPx(), 75.dp.toPx()))
        drawCircle(color=Color.Black, radius = 11.dp.toPx(), center=Offset(75.dp.toPx(), 45.dp.toPx()))
        drawCircle(color=Color.Black, radius = 11.dp.toPx(), center=Offset(75.dp.toPx(), 75.dp.toPx()))

        drawCircle(color=teamColor, radius = 10.dp.toPx(), center=Offset(45.dp.toPx(), 45.dp.toPx()))
        drawCircle(color=teamColor, radius = 10.dp.toPx(), center=Offset(45.dp.toPx(), 75.dp.toPx()))
        drawCircle(color=teamColor, radius = 10.dp.toPx(), center=Offset(75.dp.toPx(), 45.dp.toPx()))
        drawCircle(color=teamColor, radius = 10.dp.toPx(), center=Offset(75.dp.toPx(), 75.dp.toPx()))
    }
}