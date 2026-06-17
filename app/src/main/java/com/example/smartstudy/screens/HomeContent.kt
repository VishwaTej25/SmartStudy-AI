package com.example.smartstudy.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartstudy.backend.BackendProvider
import com.example.smartstudy.backend.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent() {

    val context = LocalContext.current

    // ── Real user profile from Firestore ──────────────────────────────────
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    DisposableEffect(Unit) {
        val reg = BackendProvider.backend.listenProfile(
            onUpdate = { userProfile = it },
            onError  = { /* silently ignore */ }
        )
        onDispose { reg?.remove() }
    }

    val firstName = userProfile?.fullName
        ?.split(" ")
        ?.firstOrNull()
        ?.takeIf { it.isNotBlank() }
        ?: "there"

    val streak = userProfile?.streak ?: 0

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor     = if (isDark) Color(0xFF050B1A) else Color(0xFFF3F4F6)
    val backgroundEndColor  = if (isDark) Color(0xFF0A1B55) else Color(0xFFE5E7EB)
    val textColorMain       = if (isDark) Color.White      else Color(0xFF1F2937)
    val textColorMuted      = if (isDark) Color.LightGray  else Color(0xFF4B5563)
    val textColorSecondary  = if (isDark) Color.Gray       else Color(0xFF6B7280)
    val cardBgColor         = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(20.dp))
                Text(
                    "Smart Study AI 🚀",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                NavigationDrawerItem(
                    label = { Text("🏠 Home") },
                    selected = true,
                    onClick = {}
                )
                NavigationDrawerItem(
                    label = { Text("🏆 Leaderboard") },
                    selected = false,
                    onClick = {
                        Toast.makeText(context, "Leaderboard", Toast.LENGTH_SHORT).show()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("⚙ Settings") },
                    selected = false,
                    onClick = {
                        Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("🚪 Logout") },
                    selected = false,
                    onClick = {
                        Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(backgroundColor, backgroundEndColor))
                )
                .padding(16.dp)
        ) {

            // ── Greeting ──────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(10.dp))

                Column {
                    Text(
                        text = "Welcome Back 👋",
                        color = textColorSecondary,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = firstName,
                        color = textColorMain,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Let's continue your learning journey 🚀",
                        color = textColorMuted,
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.height(20.dp))
            }

            // ── Streak Card (real data) ────────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF8B3DFF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("🔥 Study Streak", color = Color.White)
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = if (streak > 0) "$streak Days" else "Start Today!",
                                color = Color.White,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (streak > 0) "Keep your streak alive!" else "Complete a lesson to begin 🎯",
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6F2BFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔥", fontSize = 34.sp)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            // ── Quick Access ───────────────────────────────────────────────
            item {
                Text(
                    "Quick Access",
                    color = textColorMain,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(15.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickCard("Courses", "📚")
                    QuickCard("AI", "✅")
                    QuickCard("Progress", "🎯")
                    QuickCard("Rank", "📈")
                }
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun QuickCard(
    title:String,
    emoji:String
){
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val cardBgColorSecondary = if (isDark) Color(0xFF1A2033) else Color(0xFFFFFFFF)

    Card(
        modifier=
            Modifier
                .width(90.dp)
                .height(100.dp)
                .clickable {},

        colors=
            CardDefaults.cardColors(
                containerColor=cardBgColorSecondary
            )
    ){

        Column(
            modifier=
                Modifier.fillMaxSize(),

            horizontalAlignment=
                Alignment.CenterHorizontally,

            verticalArrangement=
                Arrangement.Center
        ){

            Text(
                emoji,
                fontSize=30.sp
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                title,
                color=textColorMain
            )

        }

    }

}

@Composable
fun SectionCard(
    title:String,
    subtitle:String
){
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val cardBgColorSecondary = if (isDark) Color(0xFF1A2033) else Color(0xFFFFFFFF)

    Card(
        colors=
            CardDefaults.cardColors(
                containerColor=cardBgColorSecondary
            )
    ){

        Column(
            modifier=
                Modifier.padding(18.dp)
        ){

            Text(
                title,
                color=textColorMain,
                fontSize=13.sp
            )

            Spacer(
                Modifier.height(8.dp)
            )

            LinearProgressIndicator(
                progress={0.85f},
                modifier=
                    Modifier.fillMaxWidth()
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                subtitle,
                color=
                    Color(0xFF8B3DFF)
            )

        }

    }

}

@Composable
fun WeeklyGraph(){
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val cardBgColorSecondary = if (isDark) Color(0xFF1A2033) else Color(0xFFFFFFFF)

    Card(
        colors=
            CardDefaults.cardColors(
                containerColor=cardBgColorSecondary
            )
    ){

        Column(
            modifier=
                Modifier.padding(18.dp)
        ){

            Text(
                "Weekly Student Performance 📈",
                color=textColorMain,
                fontSize=22.sp
            )

            Spacer(
                Modifier.height(20.dp)
            )

            Row(
                modifier=
                    Modifier.fillMaxWidth(),
                horizontalArrangement=
                    Arrangement.SpaceEvenly,
                verticalAlignment=
                    Alignment.Bottom
            ){

                GraphBar(65,"Mon")
                GraphBar(45,"Tue")
                GraphBar(95,"Wed")
                GraphBar(60,"Thu")
                GraphBar(100,"Fri")
                GraphBar(80,"Sat")
                GraphBar(75,"Sun")

            }

        }

    }

}

@Composable
fun GraphBar(value:Int,day:String){
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)

    Column(
        horizontalAlignment=
            Alignment.CenterHorizontally
    ){

        Box(
            modifier=
                Modifier
                    .width(18.dp)
                    .height(value.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart=10.dp,
                            topEnd=10.dp
                        )
                    )
                    .background(
                        Color(0xFF8B3DFF)
                    )
        )

        Spacer(
            Modifier.height(8.dp)
        )

        Text(
            day,
            color=textColorMain
        )

    }

}

@Composable
fun PlanCard(
    title:String,
    time:String
){
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val cardBgColorSecondary = if (isDark) Color(0xFF1A2033) else Color(0xFFFFFFFF)

    Card(
        modifier=
            Modifier.fillMaxWidth(),

        colors=
            CardDefaults.cardColors(
                containerColor=cardBgColorSecondary
            )
    ){

        Row(
            modifier=
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp),

            horizontalArrangement=
                Arrangement.SpaceBetween
        ){

            Column{

                Text(
                    title,
                    color=textColorMain
                )

                Text(
                    "Smart Study Daily Goal",

                    color=
                        Color(0xFF8B3DFF)
                )

            }

            Text(
                time,
                color=textColorMain
            )

        }

    }

}
@Composable
fun SmallStatCard(
    title: String,
    value: String
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorSecondary = if (isDark) Color.Gray else Color(0xFF6B7280)
    val cardBgColor = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)

    Card(

        modifier =
            Modifier.width(160.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = cardBgColor
            )

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Text(
                text = title,
                color = textColorSecondary
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                text = value,
                color = textColorMain,
                style =
                    MaterialTheme.typography.headlineSmall
            )

        }

    }

}
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorSecondary = if (isDark) Color.Gray else Color(0xFF6B7280)
    val cardBgColor = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)

    Card(
        modifier = modifier.height(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = value,
                color = textColorMain,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = textColorSecondary,
                fontSize = 12.sp
            )
        }
    }
}